package kr.co.gochang.migration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * NAS MySQL -> H2 파일 1회 이전 도구. `migrate` 프로파일에서만 올라온다.
 *
 * <pre>
 * MYSQL_URL=jdbc:mysql://nas:3306/gochang MYSQL_USER=... MYSQL_PASSWORD=... \
 *   java -jar gochang.jar --spring.profiles.active=migrate
 * </pre>
 *
 * 결과: ./data/gochang.mv.db (GOCHANG_DB_PATH 로 위치 변경 가능). 이 파일은 git 에 올리지 않는다.
 */
@Slf4j
@Component
@Profile("migrate")
public class MysqlToH2Migrator implements ApplicationRunner {

    private static final int BATCH = 500;

    private final JdbcTemplate h2;
    private final JdbcTemplate mysql;
    private final String h2Url;

    public MysqlToH2Migrator(JdbcTemplate h2, MysqlProps props, @Value("${spring.datasource.url}") String h2Url) {
        this.h2 = h2;
        this.h2Url = h2Url;
        DriverManagerDataSource source = new DriverManagerDataSource(props.url(), props.username(), props.password());
        source.setDriverClassName("com.mysql.cj.jdbc.Driver");
        this.mysql = new JdbcTemplate(source);
        // MySQL 드라이버는 fetchSize=MIN_VALUE 일 때만 스트리밍한다 (본문이 커도 메모리를 안 잡아먹는다)
        this.mysql.setFetchSize(Integer.MIN_VALUE);
    }

    @ConfigurationProperties("gochang.migrate.mysql")
    public record MysqlProps(String url, String username, String password) {}

    @Override
    public void run(ApplicationArguments args) {
        try {
            migrate();
        } catch (RuntimeException e) {
            // 스키마만 든 빈 H2 파일이 남으면 성공한 것처럼 보이므로 지운다
            deleteH2File();
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            log.error("==================================================================");
            log.error("이전 실패: {}", root.getMessage());
            log.error("H2 파일은 삭제했습니다. MYSQL_URL / MYSQL_USER / MYSQL_PASSWORD 와 NAS 의 3306 포트 개방 여부를 확인하세요.");
            log.error("==================================================================");
            throw e;
        }
    }

    private void migrate() {
        requireEmpty("content");
        requireEmpty("reply");

        Long sourceContents = mysql.queryForObject("select count(*) from content", Long.class);
        Long sourceReplies = mysql.queryForObject("select count(*) from reply", Long.class);
        log.info("원본 MySQL: content {}건, reply {}건", sourceContents, sourceReplies);
        if (sourceContents == null || sourceContents == 0) {
            throw new IllegalStateException("원본 MySQL 의 content 테이블이 비어 있습니다. MYSQL_URL 의 DB 이름이 맞는지 확인하세요: "
                    + "현재 DB = " + mysql.queryForObject("select database()", String.class));
        }

        long contents = copyContent();
        long replies = copyReply();
        h2.execute("CHECKPOINT SYNC");

        log.info("이전 완료: content {}건, reply {}건", contents, replies);
        log.info("H2 상 게시글 수: {}, 댓글 수: {}",
                h2.queryForObject("select count(*) from content", Long.class),
                h2.queryForObject("select count(*) from reply", Long.class));
    }

    private void deleteH2File() {
        // jdbc:h2:file:/path/to/gochang;OPTS -> /path/to/gochang.mv.db
        String path = h2Url.replaceFirst("^jdbc:h2:file:", "").split(";")[0];
        try {
            h2.execute("SHUTDOWN");
        } catch (RuntimeException ignored) {
            // 이미 닫혔거나 열리지 않은 경우
        }
        for (String suffix : new String[]{".mv.db", ".trace.db"}) {
            try {
                Files.deleteIfExists(Path.of(path + suffix));
            } catch (IOException ex) {
                log.warn("{}{} 삭제 실패: {}", path, suffix, ex.getMessage());
            }
        }
    }

    private void requireEmpty(String table) {
        Long rows = h2.queryForObject("select count(*) from " + table, Long.class);
        if (rows != null && rows > 0) {
            throw new IllegalStateException("대상 H2 의 " + table + " 테이블에 이미 " + rows
                    + "건이 있습니다. 새 파일에 이전하세요 (GOCHANG_DB_PATH 를 바꾸거나 기존 파일 삭제).");
        }
    }

    private long copyContent() {
        String insert = "insert into content (id, number, title, writedate, writer, count, recommend, replycount, "
                + "content, picture, is_deleted) values (?,?,?,?,?,?,?,?,?,?,?)";
        Batcher batcher = new Batcher(insert);
        mysql.query("select id, number, title, writedate, writer, count, recommend, replycount, content, picture, "
                + "is_deleted from content", rs -> {
            batcher.add(new Object[]{
                    rs.getLong("id"),
                    toLong(rs.getString("number"), "content.number", rs.getLong("id")),
                    rs.getString("title"),
                    rs.getString("writedate"),
                    rs.getString("writer"),
                    rs.getString("count"),
                    rs.getString("recommend"),
                    rs.getString("replycount"),
                    rs.getString("content"),
                    rs.getString("picture"),
                    toBoolean(rs, "is_deleted")
            });
        });
        return batcher.finish();
    }

    private long copyReply() {
        String insert = "insert into reply (id, writedate, writer, content, isrereply, indexincontent, contentid) "
                + "values (?,?,?,?,?,?,?)";
        Batcher batcher = new Batcher(insert);
        mysql.query("select id, writedate, writer, content, isrereply, indexincontent, contentid from reply", rs -> {
            Long id = rs.getLong("id");
            Long index = toLong(rs.getString("indexincontent"), "reply.indexincontent", id);
            batcher.add(new Object[]{
                    id,
                    rs.getString("writedate"),
                    rs.getString("writer"),
                    rs.getString("content"),
                    rs.getString("isrereply"),
                    index == null ? null : index.intValue(),
                    toLong(rs.getString("contentid"), "reply.contentid", id)
            });
        });
        return batcher.finish();
    }

    private static Long toLong(String raw, String column, Long rowId) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(raw.trim());
        } catch (NumberFormatException e) {
            log.warn("{} 값 '{}' (id={}) 을 숫자로 바꿀 수 없어 NULL 로 넣습니다", column, raw, rowId);
            return null;
        }
    }

    private static boolean toBoolean(ResultSet rs, String column) throws SQLException {
        Object value = rs.getObject(column);
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean b) {
            return b;
        }
        if (value instanceof Number n) {
            return n.intValue() != 0;
        }
        String s = value.toString().trim();
        return s.equals("1") || s.equalsIgnoreCase("true");
    }

    /** 500건씩 모아서 batchUpdate 한다. */
    private final class Batcher {
        private final String sql;
        private final List<Object[]> rows = new ArrayList<>(BATCH);
        private long total;

        Batcher(String sql) {
            this.sql = sql;
        }

        void add(Object[] row) {
            rows.add(row);
            if (rows.size() >= BATCH) {
                flush();
            }
        }

        long finish() {
            flush();
            return total;
        }

        private void flush() {
            if (rows.isEmpty()) {
                return;
            }
            h2.batchUpdate(sql, rows);
            total += rows.size();
            rows.clear();
            log.info("{} ... {}건", sql.substring(0, sql.indexOf('(')).trim(), total);
        }
    }
}
