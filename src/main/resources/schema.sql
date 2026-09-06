-- H2 스키마. 컬럼명은 원본 MySQL 과 같고, 정렬/조인에 쓰이는 컬럼만 숫자 타입으로 바꿨다.
CREATE TABLE IF NOT EXISTS content (
    id          BIGINT PRIMARY KEY,
    number      BIGINT,
    title       VARCHAR(1000),
    writedate   VARCHAR(100),
    writer      VARCHAR(200),
    count       VARCHAR(50),
    recommend   VARCHAR(50),
    replycount  VARCHAR(50),
    content     VARCHAR,   -- H2 VARCHAR 는 길이 미지정 시 최대 1GB. CLOB 이면 lower() 검색이 안 된다.
    picture     VARCHAR(2000),
    is_deleted  BOOLEAN DEFAULT FALSE NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_content_number ON content (number);

CREATE TABLE IF NOT EXISTS reply (
    id              BIGINT PRIMARY KEY,
    writedate       VARCHAR(100),
    writer          VARCHAR(200),
    content         VARCHAR,
    isrereply       VARCHAR(10),
    indexincontent  INT,
    contentid       BIGINT
);
CREATE INDEX IF NOT EXISTS idx_reply_content ON reply (contentid, indexincontent);
