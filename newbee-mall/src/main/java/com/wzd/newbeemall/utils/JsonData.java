package com.wzd.newbeemall.utils;

import java.io.Serializable;

public class JsonData implements Serializable {

    private static final Integer SUCCESS_CODE = 200;

    private static final Integer ERROR_CODE = 500;

    // 业务码 自定义

    // 0 成功 1 处理中 -1 失败
    private Integer code;

    private Object data;

    private String msg;

    public JsonData() {}

    public JsonData(Integer code, Object data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 成功返回状态码 0
     * @return
     */
    public static JsonData buildSuccess(){
        return new JsonData(SUCCESS_CODE,null,null);
    }

    /**
     * 成功返回 状态码 0 data 数据
     * @param data
     * @return
     */
    public static JsonData buildSuccess(Object data){
        return new JsonData(SUCCESS_CODE,data,null);
    }

    /**
     * 失败返回错误信息 和错误码 -1
     * @param msg
     * @return
     */
    public static JsonData buildError(String msg){
        return new JsonData(ERROR_CODE,null,msg);
    }

    /**
     * 失败返回错误信息和 自定义错误码
     * @param code
     * @param msg
     * @return
     */
    public static JsonData buildError(Integer code, String msg){
        return new JsonData(code,null,msg);
    }


}
