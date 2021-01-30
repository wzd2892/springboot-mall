$(function () {
    $('#keyword').keypress(function (e){
        var key = e.which; // 是按键的值，13 是enter
        if(key == 13){
            var q = $(this).val(); // 当前keyword的val
            if(q!=null && q != ''){
                window.location.href='/search?keyword=' + q;
            }
        }
    });
});

function search(){
    var q = $('#keyword').val();
    if(q!=null && q != ''){
        window.location.href='/search?keyword=' + q;
    }

}