package com.lv.apigateway.constant;

/**
 * @program: user
 * @Date: 2019/2/26 22:33
 * @Author: Mr.lv
 * @Description: cookie 常量设置
 */
public interface CookieConstant {

    String TOKEN = "token";

    String OPENID = "openid";

    /**
     * 过期时间 单位（秒）
     */
    Integer expire = 7200;
}
