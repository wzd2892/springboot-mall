//KindEditor变量
var editor;

$(function () {

    //详情编辑器
    editor = KindEditor.create('textarea[id="editor"]', {
        items: ['source', '|', 'undo', 'redo', '|', 'preview', 'print', 'template', 'code', 'cut', 'copy', 'paste',
            'plainpaste', 'wordpaste', '|', 'justifyleft', 'justifycenter', 'justifyright',
            'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
            'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 'fullscreen', '/',
            'formatblock', 'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold',
            'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'multiimage',
            'table', 'hr', 'emoticons', 'baidumap', 'pagebreak',
            'anchor', 'link', 'unlink'],
        uploadJson: '/api/v1/pri/admin/upload/file',
        filePostName: 'file'
    });

    new AjaxUpload('#uploadGoodsCoverImg', {
        action: '/api/v1/pri/admin/upload/file',
        name: 'file',
        autoSubmit: true,
        responseType: "json",
        onSubmit: function (file, extension) {
            if (!(extension && /^(jpg|jpeg|png|gif)$/.test(extension.toLowerCase()))) {
                alert('只支持jpg、png、gif格式的文件！');
                return false;
            }
        },
        onComplete: function (file, r) {
            if (r != null && r.code == 200) {
                $("#goodsCoverImg").attr("src", r.data);
                $("#goodsCoverImg").attr("style", "width: 128px;height: 128px;display:block;");
                return false;
            } else {
                alert("error");
            }
        }
    });
});

// 添加页面  使得一个静态的model展示
function showModal() {
    $('.modal-title').html('轮播图添加');
    $('#goodsModal').modal('show');
}

$('#confirmButton').on('click',function(){
    showModal();

});

$('#cancelButton').click(function (){
    window.location.href = "/api/v1/pri/admin/goods/edit";
})


$('#saveButton').click(function () {
    var goodsId = $('#goodsId').val();
    var goodsCategoryId = $('#levelThree option:selected').val();
    var goodsName = $('#goodsName').val();
    var tag = $('#tag').val();
    var originalPrice = $('#originalPrice').val();
    var sellingPrice = $('#sellingPrice').val();
    var goodsIntro = $('#goodsIntro').val();
    var stockNum = $('#stockNum').val();
    var goodsSellStatus = $("input[name='goodsSellStatus']:checked").val();
    var goodsDetailContent = editor.html();
    var goodsCoverImg = $("#goodsCoverImg")[0].src;
    if(isNull(goodsCoverImg) || goodsCoverImg.indexOf('img-upload') != -1){
        swal("封面不能为空",{
                icon : "error",
        });
        return;
    }
    var url = '/api/v1/pri/admin/goods/save';
    var swlMessage = '保存成功';
    var data = {
        "goodsName": goodsName,
        "goodsIntro": goodsIntro,
        "goodsCategoryId": goodsCategoryId,
        "tag": tag,
        "originalPrice": originalPrice,
        "sellingPrice": sellingPrice,
        "stockNum": stockNum,
        "goodsDetailContent": goodsDetailContent,
        "goodsCoverImg": goodsCoverImg,
        "goodsCarousel": goodsCoverImg,
        "goodsSellStatus": goodsSellStatus
    }
    if(goodsId > 0){
        url = '/api/v1/pri/admin/goods/update';
        swlMessage = '修改成功';
        data = {
            "goodsId":goodsId,    // 多了一个这个
            "goodsName": goodsName,
            "goodsIntro": goodsIntro,
            "goodsCategoryId": goodsCategoryId,
            "tag": tag,
            "originalPrice": originalPrice,
            "sellingPrice": sellingPrice,
            "stockNum": stockNum,
            "goodsDetailContent": goodsDetailContent,
            "goodsCoverImg": goodsCoverImg,
            "goodsCarousel": goodsCoverImg,
            "goodsSellStatus": goodsSellStatus
        };

    }
    console.log(data)
    $.ajax({
        type:'POST',
        url:url,
        contentType:'application/json',
        data: JSON.stringify(data),   // var 转 json
        success: function (result){
            if(result.code == 200){
                $('#goodsModal').modal('hide');
                swal({
                    title:swlMessage,
                    type:'success',
                    showCancelButton: false,
                    confirmButtonColor: '#1baeae',
                    confirmButtonText: '返回商品列表',
                    confirmButtonClass: 'btn btn-success',
                    buttonsStyling: false
                }).then(function () {
                    window.location.href = "/api/v1/pri/admin/index";
                })
            } else {
                $('#goodsModal').modal('hide');
                swal(result.msg,{
                     icon:"error",
                });
            }
            ;
        },
        error: function () {
            swal("操作失败", {
                icon: "error",
            });
        }

    });
});







$('#levelOne').on('change', function (){
    $.ajax({
        url:'/api/v1/pri/admin/goods/listForSelect?categoryId='+$(this).val(),
        type:'GET',
        success: function (result){
            if(result.code == 200){
                var levelTwoSelect = '';
                var secondLevelCategories = result.data.secondLevelCategories;
                var length2 = secondLevelCategories.length;
                for(var i = 0 ; i< length2;i++){
                    levelTwoSelect += '<option value=\"' + secondLevelCategories[i].categoryId + '\">' + secondLevelCategories[i].categoryName + '</option>';
                }
                $('#levelTwo').html(levelTwoSelect);
                var levelThreeSelect = '';
                var thirdLevelCategories = result.data.thirdLevelCategories;
                var length3 = thirdLevelCategories.length;
                for(var i = 0 ; i< length3;i++){
                    levelThreeSelect += '<option value=\"' + thirdLevelCategories[i].categoryId + '\">' + thirdLevelCategories[i].categoryName + '</option>';
                }
                $('#levelThree').html(levelThreeSelect);
            }else{
                swal(result.msg,{
                    icon:"error"
                });
            }
            ;
        },
        error: function (){
            swal("操作失败",{
                icon:"error"
            });
        }
    });
});


$('#levelTwo').on('change', function (){
    $.ajax({
        url:'/api/v1/pri/admin/goods/listForSelect?categoryId='+$(this).val(),
        type:'GET',
        success: function (result){
            if(result.code == 200){
                var levelThreeSelect = '';
                var thirdLevelCategories = result.data.thirdLevelCategories;
                var length3 = thirdLevelCategories.length;
                for(var i = 0 ; i< length3;i++){
                    levelThreeSelect += '<option value=\"' + thirdLevelCategories[i].categoryId + '\">' + thirdLevelCategories[i].categoryName + '</option>';
                }
                $('#levelThree').html(levelThreeSelect);
            }else{
                swal(result.msg,{
                    icon:"error"
                });
            }
            ;
        },
        error: function (){
            swal("操作失败",{
                icon:"error"
            });
        }
    });
});