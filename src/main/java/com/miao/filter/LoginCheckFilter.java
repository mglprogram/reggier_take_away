package com.miao.filter;

import com.alibaba.fastjson.JSON;
import com.miao.common.BaseContext;
import com.miao.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 缪广亮
 * @version 1.0
 */
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //    路径匹配器，支持通配符（* 和 **） /backend/page/login/login.html用PATH_MATCHER
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 获取请求路径
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}", requestURI);
//        不需要拦截的url列表
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/login",//移动端登录
                "/user/sendMsg",//移动端发送信息
                "/v2/api-docs", // Swagger API 文档
                "/swagger-resources", // Swagger 资源
                "/swagger-resources/**", // Swagger 资源
                "/configuration/ui", // Swagger UI 配置
                "/configuration/security", // Swagger 安全配置
                "/swagger-ui.html", // Swagger UI 页面
                "/webjars/**" // Swagger UI 静态资源
        };
//        判断本次请求是否需要处理 ，检查是否是不需要验证的URL // 如果是，不进行拦截
        if (match(urls, requestURI)) {
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
//        判断PC登陆状态，如果一登陆则直接放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("PC用户已登录，用户id为：{}", request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
            return;
        }
        //        判断移动端登陆状态，如果一登陆则直接放行
        if (request.getSession().getAttribute("user") != null) {
            log.info("移动端用户已登录，用户id为：{}", request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }
        log.info("用户未登录");
    //            如果未登录则返回login页面,通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
        return;



    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     *
     * @param urls
     * @param requestURI
     * @return
     */
    private boolean match(String[] urls, String requestURI) {
        for (String url : urls) {
            if (PATH_MATCHER.match(url, requestURI)) {
                return true;
            }
        }
        return false;
    }
}
