/**
 * 购物车中数量为0的提示
 */
function tip(){
    swal("购物车无数据，请去添加宝贝哦！",{
       icon:"error",
    });
}

/**
 * 跳转至结算页面
 */
function settle(){
    window.location.href='/shop-cart/settle'
}

/**
 * 更新购物项
 * todo 判断是否和原值一样
 * @param id
 */
function updateItem(id){
    var domId = 'goodsCount'+id;
    var goodsCount = $("#" + domId).val();
    if (goodsCount > 5) {
        swal("单个商品最多可购买5个", {
            icon: "error",
        });
        return;
    }
    if (goodsCount < 1) {
        swal("数量异常", {
            icon: "error",
        });
        return;
    }
    var data = {
        "cartItemId": id,
        "goodsCount": goodsCount
    };

    $.ajax({
        type: 'PUT',
        url: '/shop-cart',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (result) {
            if (result.code == 200) {
                window.location.reload(); // 刷新
            } else {
                swal(result.msg, {
                    icon: "error",
                });
            }
        },
        error: function () {
            swal("操作失败", {
                icon: "error",
            });
        }
    });
}

function deleteItem(id) {
    swal({
        title: "确认弹框",
        text: "确认要删除数据吗?",
        icon: "warning",
        buttons: true,
        dangerMode: true,
    }).then((flag) => {
            if (flag) {
                $.ajax({
                    type: 'DELETE',
                    url: '/shop-cart/' + id,
                    success: function (result) {
                        if (result.code == 200) {
                            window.location.reload();
                        } else {
                            swal(result.msg, {
                                icon: "error",
                            });
                        }
                    },
                    error: function () {
                        swal("操作失败", {
                            icon: "error",
                        });
                    }
                });
            }
        }
    );
}