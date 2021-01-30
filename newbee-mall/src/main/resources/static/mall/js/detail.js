function saveToCart(id){
    var goodsCount = 1;
    var data = {
        "goodsId" : id,
        "goodsCount" : goodsCount
    };

    $.ajax({
        type: 'POST',
        url: '/shop-cart',
        contentType :'application/json',
        data: JSON.stringify(data),
        success: function (result){
            if(result.code == 200){
                swal({
                    title: "添加成功",
                    text: "确认框",
                    icon: "success",
                    buttons: true,
                    dangerMode: true,
                }).then((flag) => {
                        window.location.reload();
                    }
                );
            } else {
                swal(result.msg,{
                    icon: "error",
                });
            }
        },
        error: function (){
            swal("操作失败",{
                icon: "error",
            });
        }
    });



}