package com.miao.interceptor;
import cn.hutool.json.JSONUtil;
import com.miao.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 缪广亮
 * @version 1.0
 */
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}", requestURI);

        // 设置响应类型
        response.setContentType("application/json;charset=utf-8");
        
        // 判断登录状态
        Object employeeId = request.getSession().getAttribute("employee");
        if (employeeId != null) {
            log.info("用户已登录，用户id为：{}", employeeId);
            return true;
        }
        
        log.info("用户未登录");
        response.getWriter().write(JSONUtil.toJsonStr(Result.error("NOTLOGIN")));
        return false;
    }

    
}
