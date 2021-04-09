package com.zgg.common.filter;

import com.alibaba.fastjson.JSONObject;
import com.zgg.common.action.BaseAction;
import com.zgg.common.enums.CodeEC;
import com.zgg.common.json.JsonResult;
import com.zgg.common.util.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: token 拦截器
 * Author: zy
 * Date: 2020-08-05 15:15:06
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        BaseAction ba = null;
        // 是否有此方法
        if (handler instanceof HandlerMethod) {
            // 封装请求的request和response
            HandlerMethod method = (HandlerMethod) handler;
            Object obj1 = method.getBean();
            ba = (BaseAction) obj1;
            ba.init(request, response);
        } else {
            // 没有此方法 直接返回404错误
            this.outWriteErrorNotFind(response);
            return false;
        }
        // 是否不需登陆
        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            UnCheckLogin unCheckLogin = ((HandlerMethod) handler).getMethodAnnotation(UnCheckLogin.class);
            if (unCheckLogin != null && unCheckLogin.check() == true) {
                return true;
            }
        }

        // 登陆验证 根据token查UserSession，未登陆则拒绝访问，已登陆则刷新token超时时间
       /* String ticket = request.getHeader("token");
        // 是否传来了token
        if (null == ticket) {
            this.outWriteError(response);
            return false;
        }

        UserSession userSession = (UserSession) redisService.get(ticket);
        if (null == userSession) {
            // 没有此token，未登陆
            this.outWriteError(response);
            return false;
        } else {
            // 刷新超时时间7天 60*60*24*7
            redisService.expire(ticket, 604800L);
        }

        if (null != ba) {
            ba.setUserSession(userSession);
        }*/

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception arg3)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            Object obj1 = method.getBean();
            BaseAction ba = (BaseAction) obj1;
            ba.destroy();
        }
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler, ModelAndView arg3) throws Exception {

    }

    /**
     * 未登录
     */
    private void outWriteError(HttpServletResponse response) throws Exception {
        JsonResult result = new JsonResult();
        result.setCode("401");
        result.setMessage("请先登录");
        result.setSuccess(Boolean.FALSE);
        result.setData(null);
        String jsonStr = JSONObject.toJSONString(result);
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(jsonStr);
    }

    /**
     * 404
     */
    private void outWriteErrorNotFind(HttpServletResponse response) throws Exception {
        JsonResult result = new JsonResult();
        result.setCode("404");
        result.setMessage("您访问的资源无效");
        result.setSuccess(Boolean.FALSE);
        result.setData(null);
        String jsonStr = JSONObject.toJSONString(result);
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(jsonStr);
    }

    /**
     * 无权访问
     */
    private void outWriteAccessError(HttpServletResponse response) throws Exception {
        JsonResult result = new JsonResult();
        result.setCode(CodeEC.PERMISSION_REFUSE.getCode());
        result.setMessage(CodeEC.PERMISSION_REFUSE.getMsg());
        result.setSuccess(Boolean.FALSE);
        result.setData(null);
        String jsonStr = JSONObject.toJSONString(result);
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(jsonStr);
    }


}