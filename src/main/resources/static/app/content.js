(function ($) {
    var currentContentIndex;

    var prevNextPart = new Vue({
        el: '#upperPrevNextPart',
        methods :{
            prevContentClick: function(){
                moveToPrev();
            },
            nextContentClick: function(){
                moveToNext();
            }
        }
    });

    var prevNextPart = new Vue({
        el: '#lowerPrevNextPart',
        methods :{
            prevContentClick: function(){
                moveToPrev();
            },
            nextContentClick: function(){
                moveToNext();
            }
        }
    });

    function moveToPrev(){
        if(currentContentIndex != 327) {
            window.location.href='/contents/' + (currentContentIndex + 1);
        }
    }

    function moveToNext(){
        if(currentContentIndex != 1) {
            window.location.href='/contents/' + (currentContentIndex - 1);
        }
    }

    var contentDetailPart = new Vue({
        el: '#contentDetailPart',
        data: {
            contentDetail : {}
        }
    });

    var replyPart = new Vue({
        el: '#replyPart',
        data: {
            replyList: {}
        }
    });

    $(document).ready(function(){
        var href = document.location.href;  // 밑에서 URL 사용할 수도 있어서 별도의 변수로
        var hrefSplit = href.split("/");    // 밑에서 반복 사용하기 때문에 별도의 변수로
        currentContentIndex = new Number(hrefSplit[hrefSplit.length-1]);

        // 글 내용
        getContentDetailByIndex(currentContentIndex);

        // 댓글 내용
        getReplyByContentIndex(currentContentIndex);
    });

    function getContentDetailByIndex(index){
        $.get("/api/contents/" + index, function(response){
            contentDetailPart.contentDetail = response.data;
        });
    }

    function getReplyByContentIndex(index){
        $.get("/api/replies/byContent/" + index, function(response){
            replyPart.replyList = response.data;
        });
    }

})(jQuery);