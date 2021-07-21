package kr.co.gochang.utils;

import kr.co.gochang.model.enumclass.SearchType;

import java.util.Arrays;

public final class SearchTypeUtils {

    /** Utility class는 인스턴스화 되면 안 됨 */
    private SearchTypeUtils() {};

    /** String으로 SearchType 받아오기*/
    public static SearchType getSearchTypeByString(String searchType){
        return Arrays.stream(SearchType.values())
                .filter(v -> v.getValue().equals(searchType))
                .findAny()
                .orElseGet(() -> SearchType.NONE);
    }
}
