# 실행 계획 (React 전환)

현재 Thymeleaf/jQuery 기반의 프론트엔드를 React + Vite로 전환하기 위한 단계별 계획입니다. 기존 스타일(AdminLTE 등)을 유지하면서 구조를 변경합니다.

## 1. 프로젝트 설정 (Project Setup)
- [x] **React 프로젝트 생성**: `src/main/frontend` (권장) 또는 루트 경로에 Vite로 React 프로젝트 생성
    ```bash
    pnpm create vite frontend --template react
    ```
- [x] **패키지 설치**:
    - `axios`: API 통신용
    - `react-router-dom`: 페이지 라우팅용 (필요시)
- [x] **Vite 프록시 설정**: 개발 중 CORS 문제 해결을 위해 `/api` 요청을 스프링 부트 서버(`localhost:8080`)로 프록시 설정.

## 2. 에셋 마이그레이션 (Assets)
기존 디자인을 100% 유지하기 위해 기존 정적 파일을 React 프로젝트로 가져옵니다.
- [x] **CSS/JS**: `src/main/resources/static/lib` 및 `app` 폴더의 내용을 React의 `public` 또는 `src/assets`로 이동.
- [x] **HTML 구조 이식**: `index.html`에 기존 `head.html`의 CSS/JS import 구문을 추가하여 스타일 로드.

## 3. 컴포넌트 분리 (Components)
Thymeleaf 프래그먼트를 React 컴포넌트로 변환합니다.

| Thymeleaf Fragment | React Component | 설명 |
|-------------------|-----------------|------|
| `navbar.html` | `<Navbar />` | 상단 네비게이션 |
| `contentBoard.html` | `<BoardList />` | 게시글 목록 테이블 |
| `searchPart.html` | `<SearchBar />` | 검색 필터 영역 |
| `pagingPart.html` | `<Pagination />` | 페이지네이션 버튼 |
| `main.html` | `<MainPage />` | 전체 레이아웃 조립 |

## 4. 로직 전환 (JS to React Hooks)
- [x] `boardStandard.js`에 있는 jQuery/Vue 로직을 React Hook으로 재작성 (`useBoardData.js` 구현 완료)
- [x] `useState`를 사용하여 `itemList`, `pagination` 상태 관리
- [x] `useEffect`를 사용하여 컴포넌트 마운트 및 페이지 번호 변경 시 API 호출
- [x] `axios`를 사용하여 `/api/contents` 및 `/api/contents/search` 호출

## 5. 스프링 부트 통합 (Integration)
- [x] **빌드 설정**: Gradle 빌드 시 React 앱도 같이 빌드되어 `src/main/resources/static`으로 복사되도록 설정.
  - Node Gradle 플러그인 추가 (`com.github.node-gradle.node`)
  - `installFrontend`, `buildFrontend`, `copyFrontend` 태스크 구성
  - `processResources`에 의존성 추가하여 자동 빌드
- [x] **Controller 정리**: 기존 Thymeleaf Controller는 API Controller로 유지하거나, View 반환 로직을 제거하고 React의 `index.html`을 바라보게 수정.
  - `PageController`를 SPA 폴백 컨트롤러로 변경 (`forward:/index.html`)
  - `WebConfig` 추가하여 정적 리소스 핸들링 설정
- [x] **AWS Parameter Store 설정**: 로컬 개발을 위해 `optional:` 접두사 추가

## 시작 방법
이 `PROGRESS.md` 파일의 체크리스트를 따라 하나씩 진행하면 됩니다.
가장 먼저 React 프로젝트를 초기화하고 프록시를 설정하여 "Hello World"가 기존 API와 통신하는지 확인하는 것부터 시작하는 것을 추천합니다.

## 6. CI/CD를 위한 Repository 분리 (2026-01-20)
독립적인 배포 파이프라인 구성을 위해 Frontend를 별도 Repository로 분리했습니다.

### Frontend Repository 분리
- [x] **새 Repository 생성**: `/Users/chang-yongsu/git/GochangBoot-frontend`
  - 기존 `frontend/` 디렉토리를 복사하여 새 repository 생성
  - Git 초기화 및 초기 커밋 완료
  - `.gitignore`, `.env.example`, `README.md` 추가

### Backend Repository 정리
- [x] **build.gradle 정리**:
  - Node.js Gradle 플러그인 제거 (`com.github.node-gradle.node`)
  - Frontend 빌드 태스크 제거 (`installFrontend`, `buildFrontend`, `copyFrontend`)
  - `processResources` 의존성 제거
- [x] **.gitignore 업데이트**: `frontend/` 디렉토리 제외 추가
- [x] **CORS 설정 추가** (`WebConfig.java`):
  - 로컬 개발: `http://localhost:5173`
  - Cloudflare Pages: `https://*.pages.dev`

### Frontend Production 설정
- [x] **환경 변수 설정**:
  - `.env.production.example` 생성
  - `VITE_API_BASE_URL` 환경 변수 사용하도록 `useBoardData.js` 수정
- [x] **API Endpoint 설정**:
  - Development: Vite proxy 사용 (`/api` → `localhost:8080`)
  - Production: 환경 변수로 Backend URL 지정

### 배포 전략
- **Backend**: GitHub Actions → AWS/서버 배포
- **Frontend**: GitHub Actions → Cloudflare Pages 배포
- 각 Repository가 독립적으로 빌드 및 배포됨

### 다음 단계
1. Frontend Repository를 GitHub에 Push
2. Cloudflare Pages 연결 및 환경 변수 설정 (`VITE_API_BASE_URL`)
3. Backend CI/CD 파이프라인 구성

## 7. 1GB 서버용 경량화 (2026-09-06)
서버(Lightsail 1GB, 스왑 0)와 NAS MySQL 왕복 지연을 없애기 위해 구조를 바꿨다. 자세한 절차는 `docs/OPERATIONS.md`.

- [x] **DB 를 읽기 전용 H2 파일로**: 아카이브라 DB 서버 불필요. `migrate` 프로파일로 NAS MySQL → H2 1회 이전.
- [x] **프론트를 같은 서버 nginx 로**: DuckDNS 는 CNAME 을 지원하지 않아 Cloudflare Pages 불가. 같은 출처가 되어 CORS 설정 제거.
- [x] **성능**: 조회 전부 Caffeine 캐시, 목록 응답에서 본문 제외, gzip, 가상 스레드, 페이지 크기 상한, 댓글 조회 쿼리 1회로.
- [x] **버그 수정**: 댓글 순번 문자열 정렬, 와일드카드 CORS 미매칭, DELETE 500, 빈 응답 쓰기 엔드포인트 제거, H2 없어서 실패하던 테스트.
- [x] **정리**: Thymeleaf 템플릿/의존성, AWS Parameter Store, 제네릭 CRUD 추상화, nohup.out 제거. Boot 3.3.0 → 3.5.16.
- [x] **배포 파일**: `deploy/nginx`, `deploy/systemd`, `.github/workflows/backend.yml`, 프론트용 워크플로 예시.
- [ ] 서버 준비(스왑, JDK 21, nginx, systemd) 및 DB 파일 이전
- [ ] GitHub secrets 등록 후 첫 배포
- [x] 프론트 저장소: 상세 페이지(`/contents/:id`) React 로 구현, 앞뒤 글은 `/neighbors` API 사용, 옛 jQuery/Vue 스크립트 삭제 (커밋 전)
- [ ] 프론트 저장소에 워크플로 적용 (`VITE_API_URL` 은 비워 둔다)
