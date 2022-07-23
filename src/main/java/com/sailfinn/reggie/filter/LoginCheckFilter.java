package com.sailfinn.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.sailfinn.reggie.common.BaseContext;
import com.sailfinn.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * to check if user has logged in
 */

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse)  servletResponse;

        //1. get URI
        String requestURI = request.getRequestURI();

        log.info("Request acquired: {}", requestURI);

        //2. define urls that we let pass
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",//text verification on mobile end
                "/user/login",//login on mobile end
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };

        //check if current request needs to be dealt with(which means should we check if user is logged in)
        boolean check = check(urls, requestURI);

        //3. if no manipulations needed, let pass
        if(check){
            log.info("No manipulations needed for this request: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //4-1. check if logged in (back end)
        if(request.getSession().getAttribute("employee") != null){
            //user has logged in
            log.info("User has logged in, ID is: {}", request.getSession().getAttribute("employee"));

            //ThreadLocal. 每一个http请求都会被分配一个新的thread，ThreadLocal为每个线程提供单独的存储空间
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
//            long id = Thread.currentThread().getId();
//            log.info("Thread id is: {}", id);

            filterChain.doFilter(request, response);
            return;
        }

        //4-2. check if logged in (front end)
        if(request.getSession().getAttribute("user") != null){
            //user has logged in
            log.info("User has logged in, ID is: {}", request.getSession().getAttribute("user"));

            //ThreadLocal. 每一个http请求都会被分配一个新的thread，ThreadLocal为每个线程提供单独的存储空间
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        }

        //5. if not logged in, 通过输出流的方式向客户端页面响应数据
        log.info("User not logged in");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
//        log.info("Request caught: {}", request.getRequestURI());
//        filterChain.doFilter(request, response);
    }

    /**
     * path matching, to check if we let pass current request
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
