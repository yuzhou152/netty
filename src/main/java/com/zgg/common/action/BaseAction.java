/**
 * @Title: BaseAction.java
 * @version V1.0
 */
package com.zgg.common.action;

import com.zgg.common.enums.CodeEC;
import com.zgg.common.json.JsonResult;
import com.zgg.session.UserSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Description: 基础控制层
 * Author: zy
 * Date: 2019-07-16 17:24:28
 */
public class BaseAction {

    private static ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();
    private static ThreadLocal<HttpServletResponse> responseHolder = new ThreadLocal<>();
    private static ThreadLocal<String> tokenHolder = new ThreadLocal<>();
    private static ThreadLocal<UserSession> userSessionHolder = new ThreadLocal<>();

    /**
     * 初始化
     */
    public void init(HttpServletRequest request, HttpServletResponse response) {
        requestHolder.set(request);
        responseHolder.set(response);
    }

    /**
     * 销毁
     */
    public void destroy() {
        requestHolder.remove();
        responseHolder.remove();
        tokenHolder.remove();
        userSessionHolder.remove();
    }

    /**
     * @throws
     * @Title:getCurrentUser
     * @Description:获取当前用户
     * @author:
     * @param:无
     * @return:UserSession
     */
    public UserSession getCurrentUser() {
        return userSessionHolder.get();
    }

    public void setUserSession(UserSession userSession) {
        userSessionHolder.set(userSession);
    }


    public String getToken() {
        return tokenHolder.get();
    }

    public void setToken(String token) {
        tokenHolder.set(token);
    }

    public String getUserId() {
        return TokenProcessor.ID.apply(tokenHolder.get());
    }

    public String getUserPassport() {
        return TokenProcessor.PASSPORT.apply(tokenHolder.get());
    }

    public String getUserNickName() {
        return TokenProcessor.NICKNAME.apply(tokenHolder.get());
    }

    public HttpServletRequest getRequest() {
        return requestHolder.get();
    }

    public HttpServletResponse getResponse() {
        return responseHolder.get();
    }

    private enum TokenProcessor {
        ID {
            String apply(String token) {
                return null;
            }
        },
        PASSPORT {
            String apply(String token) {
                return null;
            }
        },
        NICKNAME {
            String apply(String token) {
                return null;
            }
        };

        TokenProcessor() {
        }

        abstract String apply(String token);
    }
}
