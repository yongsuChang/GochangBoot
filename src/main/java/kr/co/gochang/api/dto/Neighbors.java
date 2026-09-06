package kr.co.gochang.api.dto;

/** 목록이 최신순이므로 prev 는 id 가 큰 쪽(더 최근 글), next 는 id 가 작은 쪽. 없으면 null. */
public record Neighbors(Link prev, Link next) {
    public record Link(Long id, String title) {}
}
