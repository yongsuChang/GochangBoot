(function ($) {

    var maxBtnSize = 10;            // 검색 하단 최대 범위
    var indexBtn = [];              // 인덱스 버튼
    var elementPerPage = 15;        // 페이지당 데이터수
    var searchType = "";
    var searchWord = "";

    // 페이징 처리 데이터
    var pagination = {
        total_pages : 0,            // 전체 페이지수
        total_elements : 0,         // 전체 데이터수
        current_page :  0,          // 현재 페이지수
        current_elements : 0        // 현재 데이터수
    };

    var elementPerPageMethod = new Vue({
        el : '#elementPerPage',
        methods: {
            changeSelection: function() {
                elementPerPage = document.getElementById('elementPerPage').value;
                searchByUrl(0);
            }
        }
    })

    var searchPartMethod = new Vue({
        el : '#searchPart',
        methods: {
            searchContents: function() {
                searchType = document.getElementById('searchType').value;
                searchWord = document.getElementById('searchWord').value;
                searchDetail(0);
            }
        }
    })

    // 총 게시글 정보
    var totalElements = new Vue({
        el : '#totalElements',
        data : {
            totalElements : {},
        }
    });

    // 데이터 리스트
    var itemList = new Vue({
        el : '#itemList',
        data : {
            itemList : {}
        },
        methods: {
            onRowClick: function(dto){
                var id = dto.id;
                window.location.href='/contents/'+id;
            }
        }
    });


    // 페이지 버튼 리스트
    var pageBtnList = new Vue({
        el : '#pageBtn',
        data : {
            btnList : {}
        },
        methods: {
            indexClick: function (id) {
                searchByUrl(id-1);
            },
            previousClick: function () {
                if(pagination.current_page !== 0){
                    searchByUrl(pagination.current_page-1);
                }
            },
            nextClick: function () {

                if(pagination.current_page !== pagination.total_pages-1){
                    searchByUrl(pagination.current_page+1);
                }
            },
            firstPageClick: function () {
                if(pagination.current_page !== 0){
                    searchByUrl(0);
                }
            },
            lastPageClick: function () {
                if(pagination.current_page !== pagination.total_pages-1){
                    searchByUrl(pagination.total_pages-1);
                }
            }
        },
        mounted:function () {
            // 제일 처음 랜더링 후 색상 처리
            setTimeout(function () {
                $('li[btn_id]').removeClass( "active" );
                $('li[btn_id='+(pagination.current_page+1)+']').addClass( "active" );
            },50)
        }
    });

    // 페이지별 로딩
    $(document).ready(function () {
        searchByUrl(0);
    });

    // TODO: 좋지 않은 방식, 변경 필요
    function searchByUrl(index){

        var href = document.location.href;  // 밑에서 URL 사용할 수도 있어서 별도의 변수로
        var hrefSplit = href.split("/");    // 밑에서 반복 사용하기 때문에 별도의 변수로
        var mapping = hrefSplit[hrefSplit.length-1].split("?")[0];
        switch(mapping){
        case "" :{
            searchStart(index);
            break;
        }
        case "search" : {
            const url = new URL(href);
            const urlParams = url.searchParams;
            searchType = urlParams.get("searchType");
            searchWord = urlParams.get("searchWord");
            searchDetail(index);
            break;
        }
        default : {
            searchStart(index);
        }
        }
    }

    function searchStart(index) {
        console.log("call index : "+index);
        $.get("/api/contents?page="+index+"&size="+elementPerPage,
            function (response) {searchContentsAndPaging(response)});
    }

    function searchDetail(index) {
        console.log("call index : "+index);
        $.get("/api/contents/search?page="+index+"&size="+elementPerPage
                +"&searchType="+searchType+"&searchWord="+searchWord,
            function (response) {searchContentsAndPaging(response)});
    }

    function searchContentsAndPaging(response){

        /* 데이터 셋팅 */
        // 페이징 처리 데이터
        indexBtn = [];
        pagination = response.pagination;


        // 총 게시글
        totalElements.totalElements = pagination.total_elements;


        // 검색 데이터
        itemList.itemList = response.data;


        // 이전 버튼, 맨앞 버튼
        if(pagination.current_page === 0){
            $('#previousBtn').addClass("disabled")
            $('#firstPageBtn').addClass("disabled")
        }else{
            $('#previousBtn').removeClass("disabled")
            $('#firstPageBtn').removeClass("disabled")
        }


        // 다음 버튼, 맨뒤 버튼
        if(pagination.current_page === pagination.total_pages-1){
            $('#nextBtn').addClass("disabled")
            $('#lastPageBtn').addClass("disabled")
        }else{
            $('#nextBtn').removeClass("disabled")
            $('#lastPageBtn').removeClass("disabled")
        }

        // 페이징 버튼 처리
        var temp = Math.floor(pagination.current_page / maxBtnSize);
        for(var i = 1; i <= maxBtnSize; i++){
            var value = i+(temp*maxBtnSize);

            if(value <= pagination.total_pages){
                indexBtn.push(value)
            }
        }

        // 페이지 버튼 셋팅
        pageBtnList.btnList = indexBtn;


        // 색상처리
        setTimeout(function () {
            $('li[btn_id]').removeClass( "active" );
            $('li[btn_id='+(pagination.current_page+1)+']').addClass( "active" );
        },50)
    }
})(jQuery);
