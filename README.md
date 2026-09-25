# 고창농악 전수 후기 백업 사이트 (API)

- 옛 게시판 글과 댓글을 보관하는 읽기 전용 아카이브. 데이터는 더 이상 바뀌지 않는다 (2020년 12월 31일 기준).
- Spring Boot 3.5 / Java 21 / 읽기 전용 H2 파일 DB. 프론트(React)는 별도 저장소 [GochangBoot-frontend](https://github.com/yongsuChang/GochangBoot-frontend).
- 운영: https://gochang-visitor.duckdns.org (Oracle Cloud 1GB, nginx 가 프론트와 `/api` 를 같은 도메인으로 서빙)

## 실행

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'   # 인메모리 H2 + 샘플 데이터
./gradlew test
GOCHANG_DB_PATH=./data/gochang java -jar build/libs/gochang.jar   # 실데이터, 운영 설정
```

| 프로파일 | 용도 |
|---|---|
| (기본) | 운영. `GOCHANG_DB_PATH` 의 H2 파일을 읽기 전용으로 연다. 파일이 없으면 기동 실패 |
| `dev` | 로컬 개발. 인메모리 H2 + `sample-data.sql`, H2 콘솔 `/h2-console` |
| `migrate` | NAS MySQL → H2 파일 1회 이전. 웹 서버 없이 복사만 하고 종료 |

## 패키지 구조

```
kr.co.gochang
├── config/        CacheNames            캐시 이름 상수
├── controller/    ContentController, ReplyController
├── service/       ContentService, ReplyService       조회 전부 Caffeine 캐시
├── repository/    ContentRepository, ReplyRepository
├── domain/        Content, Reply, SearchType         읽기 전용(@Immutable) 엔티티
├── dto/response/  ApiResponse, PageInfo, ContentSummary, ContentDetail, ReplyResponse, Neighbors
├── exception/     GlobalExceptionHandler             모든 오류를 같은 JSON 봉투로
└── migration/     MysqlToH2Migrator                  migrate 프로파일 전용
```

리소스: `schema.sql`(H2 스키마), `sample-data.sql`(dev/test 샘플), `application*.properties`.

## 문서

- 서버 구성, MySQL→H2 이전, 배포, 배포 건너뛰기, 문제 해결, API 계약: [docs/OPERATIONS.md](docs/OPERATIONS.md)
- 작업 이력: [PROGRESS.md](PROGRESS.md)

데이터 파일(`data/`, `*.mv.db`)은 절대 커밋하지 않는다.
