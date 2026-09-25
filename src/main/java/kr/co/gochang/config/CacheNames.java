package kr.co.gochang.config;

/**
 * 캐시 이름. application.properties 의 spring.cache.cache-names 와 맞춰야 한다.
 * 데이터가 바뀌지 않는 아카이브라 무효화 없이 크기 상한만 둔다.
 */
public final class CacheNames {

    public static final String CONTENT_LIST = "contentList";
    public static final String CONTENT_SEARCH = "contentSearch";
    public static final String CONTENT_DETAIL = "contentDetail";
    public static final String CONTENT_TITLE = "contentTitle";
    public static final String CONTENT_NEIGHBORS = "contentNeighbors";
    public static final String REPLIES = "replies";

    private CacheNames() {
    }
}
