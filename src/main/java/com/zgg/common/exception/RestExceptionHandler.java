package com.zgg.common.exception;

import com.zgg.common.enums.CodeEC;
import com.zgg.common.json.JsonResult;
import com.zgg.common.util.LoggerUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description: 辅助Controller 异常处理器
 * Author: zy
 * Date: 2019-07-16 17:17:17
 */
@ControllerAdvice
public class RestExceptionHandler {

    /**
     * Description: 系统异常
     * Author: zy
     * Date: 2019-07-16 17:21:18
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public JsonResult ExceptionHandler(Exception ex) {
        LoggerUtil.getInstance().api_error("ExceptionHandler", null, null, ex);
        return JsonResult.FAIL(CodeEC.SERVICE_ERROR);
    }

    /**
     * Description: 业务异常
     * Author: zy
     * Date: 2019-07-16 17:21:29
     */
    @ResponseBody
    @ExceptionHandler(value = ZcyException.class)
    public JsonResult ZcyExceptionHandler(ZcyException ex) {
        LoggerUtil.getInstance().api_error("ZcyExceptionHandler", null, null, ex);
        return JsonResult.FAIL(ex.getCodeEC());
    }

    /**
     * 可直接在Service曾抛出，会被RestExceptionHandler捕获并返回给前端
     */
    @Data
    @AllArgsConstructor
    public static class ZcyException extends RuntimeException {
        private CodeEC codeEC;
    }
}
