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
