# 운영 가이드

## 구조

```
브라우저 ──HTTPS──▶ nginx (gochang-visitor.duckdns.org, Lightsail 1GB)
                     ├─ /        → /var/www/gochang  (React 빌드, 프론트 저장소가 배포)
                     └─ /api/**  → 127.0.0.1:8080    (Spring Boot, systemd `gochang`)
                                      └─ /opt/gochang/data/gochang.mv.db  (읽기 전용 H2 파일)
```

- 데이터는 절대 바뀌지 않는 아카이브다. DB 서버 없이 H2 파일 하나를 읽기 전용으로 연다.
- DB 파일은 **배포 산출물이 아니다.** 서버에 한 번 복사해 두고, 배포는 jar 와 프론트 정적 파일만 교체한다.
- 저장소는 public 이므로 DB 파일과 덤프는 어떤 형태로도 커밋하지 않는다 (`.gitignore` 에 `data/`, `*.mv.db` 등).
- 원본 백업(MySQL 덤프 + H2 파일 사본)은 Synology NAS 에 둔다.

## 1. 서버 준비 (한 번)

```bash
# 스왑 1GB: 성능용이 아니라 OOM 방지용
sudo fallocate -l 1G /swapfile && sudo chmod 600 /swapfile
sudo mkswap /swapfile && sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

sudo apt install -y openjdk-21-jre-headless nginx
sudo useradd -r -s /usr/sbin/nologin -d /opt/gochang gochang
sudo mkdir -p /opt/gochang/{data,incoming} /var/www/gochang
sudo chown -R gochang:gochang /opt/gochang
sudo chown -R ubuntu:ubuntu /opt/gochang/incoming /var/www/gochang

```

서버에는 저장소가 없으므로 설정 파일 두 개를 로컬에서 올린다:

```bash
scp deploy/systemd/gochang.service deploy/nginx/gochang.conf ubuntu@<서버>:/opt/gochang/incoming/
```

서버에서 (백업은 `sites-enabled/` 밖에 둔다. 그 안에 두면 nginx 가 백업까지 로드해서 upstream 중복 오류가 난다):

```bash
sudo mv /opt/gochang/incoming/gochang.service /etc/systemd/system/
sudo cp /etc/nginx/sites-enabled/default /etc/nginx/default.bak
sudo mv /opt/gochang/incoming/gochang.conf /etc/nginx/sites-enabled/default
sudo nginx -t && sudo systemctl reload nginx
sudo systemctl daemon-reload && sudo systemctl enable gochang
```

배포 계정이 `sudo systemctl restart gochang` 만 비밀번호 없이 실행할 수 있게 sudoers 에 한 줄 추가한다:

```
ubuntu ALL=(root) NOPASSWD: /usr/bin/systemctl restart gochang
```

## 2. MySQL → H2 파일 이전 (한 번)

NAS 의 MySQL 에 접근 가능한 PC(집)에서 실행한다. 웹 서버는 뜨지 않고 복사만 하고 종료한다.

```bash
./gradlew bootJar
MYSQL_URL='jdbc:mysql://<NAS-IP>:3306/<DB명>?useUnicode=true&characterEncoding=utf8' \
MYSQL_USER='<계정>' MYSQL_PASSWORD='<비밀번호>' \
GOCHANG_DB_PATH=./data/gochang \
java -jar build/libs/gochang.jar --spring.profiles.active=migrate
```

- 결과: `./data/gochang.mv.db`. 로그 마지막에 게시글/댓글 건수가 찍힌다. MySQL 쪽 `select count(*)` 와 맞는지 확인한다.
- 숫자 컬럼(`content.number`, `reply.indexincontent`, `reply.contentid`)으로 못 바꾸는 값은 NULL 로 넣고 WARN 을 남긴다. WARN 이 있으면 원본을 확인한다.
- 대상 테이블에 이미 데이터가 있으면 중단한다. 다시 하려면 파일을 지우거나 `GOCHANG_DB_PATH` 를 바꾼다.
- 로컬 확인: `GOCHANG_DB_PATH=./data/gochang java -jar build/libs/gochang.jar` 로 띄우고 `/api/contents` 호출.

서버로 복사하고 NAS 에도 사본을 둔다:

```bash
scp data/gochang.mv.db build/libs/gochang.jar ubuntu@<서버>:/opt/gochang/incoming/
ssh ubuntu@<서버> 'sudo mv /opt/gochang/incoming/gochang.mv.db /opt/gochang/data/ && sudo chown gochang:gochang /opt/gochang/data/gochang.mv.db \
  && sudo mv /opt/gochang/incoming/gochang.jar /opt/gochang/gochang.jar && sudo chown gochang:gochang /opt/gochang/gochang.jar \
  && sudo systemctl restart gochang && sleep 5 && curl -s http://127.0.0.1:8080/actuator/health'
```

이후 NAS 의 MySQL 은 내려도 되고, 외부에 열린 3306 포트는 닫는다.

## 3. 배포

- **백엔드**: `master` push → GitHub Actions(`.github/workflows/backend.yml`) 가 테스트, jar 빌드, scp, `systemctl restart`, 헬스체크까지 한다.
  Repository secrets: `SSH_HOST`, `SSH_USER`, `SSH_KEY`(배포 계정의 개인키).
- **프론트**: 프론트 저장소에 `deploy/frontend-deploy.example.yml` 을 복사해서 쓴다. `dist/` 를 `/var/www/gochang` 로 올리기만 하면 끝.
  같은 도메인이므로 `VITE_API_URL` 은 비우고 `/api` 상대경로로 호출한다. 백엔드의 CORS 설정은 제거됐다.

## 4. 로컬 개발

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'   # 인메모리 H2 + 샘플 데이터, H2 콘솔 /h2-console
./gradlew test
```

## 5. 메모리 예산 (1GB 서버)

| 항목 | 예상 |
|---|---|
| JVM (힙 224MB 상한, Serial GC) | 250~300MB |
| nginx | ~10MB |
| OS 등 | ~150MB |

JVM 이 OOM 이면 `-XX:+ExitOnOutOfMemoryError` 로 즉시 죽고 systemd 가 5초 뒤 다시 올린다.
캐시(`spring.cache.caffeine.spec`)는 항목 수 1000, 6시간 미접근 시 제거. 힙이 부족하면 이 값을 먼저 줄인다.

## 6. API 계약 (프론트가 의존)

응답 봉투와 필드명은 이전과 동일하다: `transaction_time`, `result_code`, `description`, `data`, `pagination{total_pages,total_elements,current_page,current_elements}`.

| 엔드포인트 | 비고 |
|---|---|
| `GET /api/contents?page&size&sort` | 목록. **본문(`content`) 필드가 빠졌다.** 목록 화면은 본문을 쓰지 않는다. |
| `GET /api/contents/search?searchType=title\|content\|writer&searchWord=` | 대소문자 무시. 검색어 없으면 전체. |
| `GET /api/contents/{id}` | 본문 포함. 없으면 200 + `result_code=ERROR` (기존 계약 유지) |
| `GET /api/contents/{id}/title` | 없으면 `"게시물 없음"` |
| `GET /api/contents/{id}/neighbors` | 상세 페이지 앞뒤 글. 삭제된 글을 건너뛴 실제 이웃 `{prev:{id,title}, next:{id,title}}`, 끝이면 null |
| `GET /api/replies/byContent/{contentId}` | `index_in_content` 가 숫자라 정렬이 올바르다 (이전엔 "1,10,11,2" 순) |

`size` 상한 100. 잘못된 `sort` 는 400, 없는 경로는 404, 그 외 오류는 500 이며 모두 같은 JSON 봉투다.
