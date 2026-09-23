# 운영 가이드

## 구조

```
브라우저 ──HTTPS──▶ nginx (gochang-visitor.duckdns.org, Oracle Cloud 1GB)
                     ├─ /        → /var/www/gochang  (React 빌드, 프론트 저장소)
                     └─ /api/**  → 127.0.0.1:8080    (Spring Boot, systemd `gochang`)
                                      └─ /opt/gochang/data/gochang.mv.db  (읽기 전용 H2 파일)
```

- 데이터는 더 이상 바뀌지 않는 아카이브다. DB 서버 없이 H2 파일 하나를 읽기 전용으로 연다.
- DB 파일은 **배포 산출물이 아니다.** 서버에 한 번 두고, 배포는 jar 와 프론트 정적 파일만 바꾼다.
- 저장소가 public 이므로 DB 파일과 덤프는 어떤 형태로도 커밋하지 않는다 (`.gitignore` 의 `data/`, `*.mv.db`).
- 원본 백업(MySQL 덤프와 H2 파일 사본)은 Synology NAS 에 둔다.

### 서버 현황

| 항목 | 값 |
|---|---|
| 호스트 | Oracle Cloud, Ubuntu 24.04, x86-64, 2 vCPU, 메모리 1GB |
| 스왑 | `/mnt/swapfile` 1GB |
| 접속 | `ssh Oracle` (ubuntu, 비밀번호 없는 sudo) |
| JDK | openjdk-21-jre-headless |
| 앱 | `/opt/gochang/gochang.jar` (소유 gochang), systemd `gochang` |
| 데이터 | `/opt/gochang/data/gochang.mv.db` (소유 gochang) |
| 업로드 임시 폴더 | `/opt/gochang/incoming` (소유 ubuntu) |
| 프론트 | `/var/www/gochang` (소유 ubuntu) |
| nginx | `/etc/nginx/sites-enabled/default` = `deploy/nginx/gochang.conf`, 인증서는 certbot |

## 1. 서버 준비 (새 서버에 처음 세팅할 때만)

```bash
# 스왑이 없으면 1GB 추가 (성능용이 아니라 OOM 방지용)
sudo fallocate -l 1G /mnt/swapfile && sudo chmod 600 /mnt/swapfile
sudo mkswap /mnt/swapfile && sudo swapon /mnt/swapfile
echo '/mnt/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

sudo apt install -y openjdk-21-jre-headless nginx
sudo useradd -r -s /usr/sbin/nologin -d /opt/gochang gochang
sudo mkdir -p /opt/gochang/{data,incoming} /var/www/gochang
sudo chown -R gochang:gochang /opt/gochang
sudo chown -R ubuntu:ubuntu /opt/gochang/incoming /var/www/gochang
```

서버에는 저장소가 없으므로 설정 파일은 로컬에서 올린다.

```bash
scp deploy/systemd/gochang.service deploy/nginx/gochang.conf Oracle:/opt/gochang/incoming/
```

서버에서 적용한다. nginx 백업은 반드시 `sites-enabled/` **밖에** 둔다. 안에 두면 nginx 가 백업까지 읽어서 upstream 중복 오류가 난다.

```bash
sudo mv /opt/gochang/incoming/gochang.service /etc/systemd/system/
sudo cp /etc/nginx/sites-enabled/default /etc/nginx/default.bak
sudo mv /opt/gochang/incoming/gochang.conf /etc/nginx/sites-enabled/default
sudo nginx -t && sudo systemctl reload nginx
sudo systemctl daemon-reload && sudo systemctl enable gochang
```

인증서가 없는 새 도메인이면 nginx 설정 교체 **전에** `sudo certbot certonly --nginx -d <도메인>` 으로 먼저 받는다.
설정 파일이 인증서 경로를 참조하므로, 인증서가 없으면 `nginx -t` 가 실패한다.

## 2. MySQL → H2 파일 이전 (한 번, 완료됨)

NAS 의 MariaDB 에 접근할 수 있는 PC(집)에서 실행한다. 웹 서버는 뜨지 않고, 복사만 하고 끝난다.

```bash
./gradlew bootJar
MYSQL_URL='jdbc:mysql://<NAS-IP>:3307/<DB명>?useUnicode=true&characterEncoding=utf8' \
MYSQL_USER='<계정>' MYSQL_PASSWORD='<비밀번호>' \
GOCHANG_DB_PATH=./data/gochang \
java -jar build/libs/gochang.jar --spring.profiles.active=migrate
```

- Synology 의 MariaDB 10 은 기본 포트가 **3307** 이다. 3306 으로 시도하면 `Connection refused` 가 난다.
- 성공하면 로그에 `원본 MySQL: content N건` 과 `이전 완료` 가 찍히고 파일이 수 MB 가 된다 (2020년 기준 게시글 327, 댓글 240, 약 2.5MB).
- 실패하면 빈 H2 파일을 지우고 마지막에 `이전 실패: <원인>` 을 크게 찍는다. 원본이 0건이어도 실패로 처리한다.
- 숫자로 바꿀 수 없는 값(`content.number`, `reply.indexincontent`, `reply.contentid`)은 NULL 로 넣고 WARN 을 남긴다.
- 대상 파일에 이미 데이터가 있으면 중단한다. 다시 하려면 `data/gochang.mv.db` 를 지운다.

서버로 올리고, NAS 에도 사본을 둔다.

```bash
scp data/gochang.mv.db Oracle:/opt/gochang/incoming/
ssh Oracle 'sudo systemctl stop gochang \
  && sudo install -o gochang -g gochang -m 644 /opt/gochang/incoming/gochang.mv.db /opt/gochang/data/gochang.mv.db \
  && rm /opt/gochang/incoming/gochang.mv.db && sudo systemctl start gochang'
```

이후 NAS 의 MariaDB 는 내려도 되고, 외부에 열린 3307 포트는 닫는다.

## 3. 배포

### 백엔드 (GitHub Actions, `.github/workflows/backend.yml`)

`master` 에 push 하면 테스트 → jar 빌드 → 서버로 업로드 → `systemctl restart` → 헬스체크까지 자동으로 한다.
업로드는 러너의 기본 `scp` 를 keepalive, 타임아웃, 3회 재시도와 함께 쓴다 (도커 기반 scp 액션은 전송이 멈추면 무한 대기했다).

Repository secrets (등록 완료): `SSH_HOST`, `SSH_USER`, `SSH_KEY`

**배포를 건너뛰는 방법**

| 원하는 것 | 방법 |
|---|---|
| 문서만 수정 | 아무것도 안 해도 된다. `*.md`, `docs/**` 만 바뀐 push 는 워크플로가 안 돈다 |
| 이번 커밋은 워크플로 전체 생략 | 커밋 메시지에 `[skip ci]` |
| 테스트는 돌리고 배포만 생략 | 커밋 메시지에 `[skip deploy]` |
| 당분간 자동 배포 끄기 | `gh variable set AUTO_DEPLOY --body false` (다시 켤 땐 `gh variable delete AUTO_DEPLOY`) |
| 원할 때 수동 배포 | `gh workflow run backend.yml --ref master` 또는 Actions 탭의 Run workflow |

수동 배포가 필요하면 로컬에서도 할 수 있다.

```bash
./gradlew bootJar
scp build/libs/gochang.jar Oracle:/opt/gochang/incoming/
ssh Oracle 'sudo install -o gochang -g gochang -m 644 /opt/gochang/incoming/gochang.jar /opt/gochang/gochang.jar \
  && rm /opt/gochang/incoming/gochang.jar && sudo systemctl restart gochang'
```

### 프론트 (지금은 수동)

프론트와 API 가 같은 도메인이므로 `VITE_API_URL` 은 비워 두고 `/api` 상대경로로 호출한다. 백엔드의 CORS 설정은 제거됐다.

```bash
cd ~/git/GochangBoot-frontend && pnpm build
scp -r dist/* Oracle:/var/www/gochang/
```

자동화하려면 `deploy/frontend-deploy.example.yml` 을 프론트 저장소의 `.github/workflows/` 에 복사하고 같은 secrets 를 등록한다.

## 4. 로컬 개발

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'   # 인메모리 H2 + 샘플 데이터, H2 콘솔 /h2-console
./gradlew test

# 실제 데이터로 운영 설정 그대로 띄우기
GOCHANG_DB_PATH=./data/gochang java -jar build/libs/gochang.jar
```

프론트는 `pnpm dev` 로 띄우면 Vite 가 `/api` 를 `localhost:8080` 으로 프록시한다.

## 5. 메모리 예산 (1GB 서버)

| 항목 | 예상 |
|---|---|
| JVM (힙 224MB 상한, Serial GC) | 약 250MB (실데이터로 측정 245MB) |
| nginx | 약 10MB |
| OS 등 | 약 150MB |

JVM 이 OOM 이면 `-XX:+ExitOnOutOfMemoryError` 로 즉시 죽고, systemd 가 5초 뒤 다시 올린다.
캐시(`spring.cache.caffeine.spec`)는 항목 1000개, 6시간 동안 안 쓰이면 제거한다. 힙이 부족하면 이 값부터 줄인다.

## 6. 문제 해결

| 증상 | 확인 |
|---|---|
| 사이트 루트가 403 | `/var/www/gochang` 이 비어 있다. 프론트를 올린다 |
| API 가 0건 | 서버의 `gochang.mv.db` 가 빈 파일이다. 로컬에서 건수를 확인하고 다시 올린다 |
| 서비스가 안 뜬다 | `sudo journalctl -u gochang -n 50`. DB 파일이 없으면 `IFEXISTS=TRUE` 때문에 즉시 실패한다 |
| 8080 포트 충돌 | 옛 프로세스가 남아 있다. `sudo ss -ltnp \| grep 8080` |
| 배포 잡이 멈춤/실패 | Actions 로그의 Upload jar / Swap jar 단계, 서버 `ls /opt/gochang/incoming` |

## 7. API 계약 (프론트가 의존)

응답 봉투와 필드명은 이전과 같다: `transaction_time`, `result_code`, `description`, `data`, `pagination{total_pages,total_elements,current_page,current_elements}`.

| 엔드포인트 | 비고 |
|---|---|
| `GET /api/contents?page&size&sort` | 목록. **본문(`content`) 필드는 빠졌다.** 목록 화면은 본문을 쓰지 않는다 |
| `GET /api/contents/search?searchType=title\|content\|writer&searchWord=` | 대소문자 무시. 검색어가 없으면 전체 |
| `GET /api/contents/{id}` | 본문 포함. 없거나 삭제된 글이면 200 + `result_code=ERROR` (기존 계약 유지) |
| `GET /api/contents/{id}/title` | 없으면 `"게시물 없음"` |
| `GET /api/contents/{id}/neighbors` | 상세 페이지의 앞뒤 글. 삭제된 글을 건너뛴 실제 이웃 `{prev:{id,title}, next:{id,title}}`, 끝이면 null |
| `GET /api/replies/byContent/{contentId}` | `index_in_content` 가 숫자라 정렬이 올바르다 (이전엔 "1,10,11,2" 순). 원본 순번은 0부터 시작 |

`size` 상한은 100. 잘못된 `sort` 는 400, 없는 경로는 404, 그 외 오류는 500 이고 모두 같은 JSON 봉투다.
삭제 플래그(`is_deleted`)가 선 글은 목록, 검색, 상세, 앞뒤 글 어디에도 나오지 않는다.
