package com.zgg.common.json;

import com.fasterxml.jackson.annotation.JsonView;
import com.zgg.common.enums.CodeEC;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: web前端json数据处理类
 * Author: zy
 * Date: 2019-07-16 17:25:57
 */
@Data
public class JsonResult<T> implements Serializable {
    private static final long serialVersionUID = 3863559687276427577L;

    @JsonView(ErrorView.class)
    public String message;
    @JsonView(ErrorView.class)
    private Boolean success = true;
    @JsonView(NormalView.class)
    private T data;
    @JsonView(ErrorView.class)
    private String code;


    public JsonResult() {
    }

    public JsonResult(T data, Boolean success, String code, String message) {
        this.data = data;
        this.success = success;
        this.message = message;
        this.code = code;
    }

    public static JsonResult SUCCESS() {
        return new JsonResult(null, true, CodeEC.SUCCESS.getCode(),CodeEC.SUCCESS.getMsg());
    }
    public static JsonResult SUCCESS(CodeEC codeEC) {
        return new JsonResult(null, true, codeEC.getCode(),codeEC.getMsg());
    }
    public static <T> JsonResult<T> SUCCESS(T data) {
        return new JsonResult(data, true, CodeEC.SUCCESS.getCode(),CodeEC.SUCCESS.getMsg());
    }
    public static <T> JsonResult<T> SUCCESS(CodeEC codeEC, T data) {
        return new JsonResult(data, true, codeEC.getCode(),codeEC.getMsg());
    }
    public static JsonResult FAIL() {
        return new JsonResult(null, false, CodeEC.SUCCESS.getCode(),CodeEC.SUCCESS.getMsg());
    }
    public static JsonResult FAIL(CodeEC codeEC) {
        return new JsonResult(null, false, codeEC.getCode(),codeEC.getMsg());
    }
    public static <T> JsonResult<T> FAIL(T data) {
        return new JsonResult(data, false, CodeEC.SUCCESS.getCode(),CodeEC.SUCCESS.getMsg());
    }
    public static <T> JsonResult<T> FAIL(CodeEC codeEC, T data) {
        return new JsonResult(data, false, codeEC.getCode(),codeEC.getMsg());
    }

    public interface ErrorView {
    }

    public interface NormalView extends ErrorView {
    }

    public static void main(String[] args) {
        JsonResult<Map<String,String>> o = JsonResult.SUCCESS(CodeEC.SUCCESS, new HashMap());
        System.out.println(o.getData());
    }
}
