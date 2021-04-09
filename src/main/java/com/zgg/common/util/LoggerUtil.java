package com.zgg.common.util;


import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 日志处理类
 * Author: zy
 * Date: 2019-07-16 17:27:16
 */
public class LoggerUtil {

    private final String FQCN = getClass().getName();
    private final int LEVEL_DEBUG = 10;
    private final int LEVEL_INFO = 20;
    private final int LEVEL_WARN = 30;
    private final int LEVEL_ERROR = 40;
    private Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    private LoggerUtil() {
    }

    public static LoggerUtil getInstance() {
        return SintletonClassInstance.instance;
    }

    private void log(int level, String msg, Integer operatorId, String token, Throwable t) {
        try {
            StringBuffer sp = new StringBuffer();
            if (null != msg) {
                sp.append("msg:").append(msg).append(" | ");
            }
            if (null != operatorId) {
                sp.append("operator:").append(operatorId + "").append(" | ");
            }
            if (null != token) {
                sp.append("token:").append(token).append(" | ");
            }
            if (null != t) {
                sp.append("exception:").append(t.getMessage()).append(" |");
            }
            this.logger.log(null, FQCN, level, sp == null ? "" : sp.toString(), null, t);
        } catch (Exception e) {
            String exStr = "exception:" + e.getMessage();
            this.logger.log(null, FQCN, LEVEL_WARN, exStr, null, e);
        }
    }

    /**
     * API日志——info
     *
     * @param msg      消息内容
     * @param operator 当前操作人id
     * @param token    登录凭证
     */
    public void api_info(String msg, String operator, String token) {
        this.log(this.LEVEL_INFO, msg, Integer.parseInt(operator == null ? "0" : operator), token, null);
    }

    /**
     * API日志——debug
     *
     * @param msg      消息内容
     * @param operator 当前操作人id
     * @param token    登录凭证
     */
    public void api_debug(String msg, Integer operator, String token) {
        this.log(this.LEVEL_DEBUG, msg, operator, token, null);
    }

    /**
     * API日志——warn
     *
     * @param msg      消息内容
     * @param operator 当前操作人id
     * @param token    登录凭证
     */
    public void api_warn(String msg, Integer operator, String token) {
        this.log(this.LEVEL_WARN, msg, operator, token, null);
    }

    /**
     * API日志——error
     *
     * @param msg      消息内容
     * @param operator 当前操作人id
     * @param token    登录凭证
     * @param t        异常
     */
    public void api_error(String msg, Integer operator, String token, Throwable t) {
        this.log(this.LEVEL_ERROR, msg, operator, token, t);
    }

    private static class SintletonClassInstance {
        private static final LoggerUtil instance = new LoggerUtil();
    }

}

