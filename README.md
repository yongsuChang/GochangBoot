# 고창농악 전수 후기 백업 사이트 (API)

- 옛 게시판 글과 댓글을 보관하는 읽기 전용 아카이브. 데이터는 더 이상 바뀌지 않는다.
- Spring Boot 3.5 / Java 21 / 읽기 전용 H2 파일 DB. 프론트(React)는 별도 저장소.
- 현재 https://gochang-visitor.duckdns.org 에서 확인 가능.

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'   # 샘플 데이터로 로컬 실행
./gradlew test
```

서버 구성, MySQL→H2 이전, 배포 절차는 [docs/OPERATIONS.md](docs/OPERATIONS.md) 참고.
데이터 파일(`data/`, `*.mv.db`)은 절대 커밋하지 않는다.
