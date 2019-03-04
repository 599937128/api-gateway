package com.lv.apigateway.filter;

import com.lv.apigateway.constant.RedisConstant;
import com.lv.apigateway.utils.CookieUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_DECORATION_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * @program: api-gateway
 * @Date: 2019/2/26 20:09
 * @Author: Mr.lv
 * @Description: 前置鉴权 适应前置过滤器 权限拦截（区分买家和卖家）
 */
@Component
public class AuthFilter extends ZuulFilter {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        /**
         * /order/create 只能买家访问(cookie 里有openid)
         * /order/finish 只能卖家访问(cookie 有 token,病对用redis里面的值)
         * /product/list 都可访问
         */
        System.out.println("jinlai----------------");
        System.out.println(request.getRequestURL());
        if(request.getRequestURL().equals("http://127.0.0.1:8087/order/order/create")) {
            System.out.println("1234567");
            Cookie cookie = CookieUtil.get(request, "openid");
            if (cookie == null || StringUtils.isEmpty(cookie.getValue())) {
                requestContext.setSendZuulResponse(false);
                requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }

        if("/order/finish".equals(request.getRequestURL())) {
            Cookie cookie = CookieUtil.get(request, "token");
            if (cookie == null || StringUtils.isEmpty(cookie.getValue())
                || StringUtils.isEmpty(stringRedisTemplate.opsForValue().
                    get(String.format(RedisConstant.TOKEN_TEMPLATE,
                            cookie.getValue())))) {
                requestContext.setSendZuulResponse(false);
                requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }

        return null;
    }
}
