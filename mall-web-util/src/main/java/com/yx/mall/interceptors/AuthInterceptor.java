package com.yx.mall.interceptors;

import com.alibaba.fastjson.JSON;
import com.yx.mall.annotations.LoginRequired;
import com.yx.mall.constant.MallConstant;
import com.yx.mall.util.CookieUtil;
import com.yx.mall.util.HttpClientUtil;
import com.yx.mall.util.WebUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Log4j
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    private final String COOKIE_TOKEN = "token";
    private final String URL_MALL_PASSPORT_INDEX = "http://passport.mall.com:8085/index";
    private final String URL_MALL_PASSPORT_VERIFY = "http://passport.mall.com:8085/verify";
    private final int COOKIE_EXPIRE_TIME = 60*60*2;
    private final String LOGIN_TOKEN_STATUS_SUCCESS = "success";
    private final String LOGIN_TOKEN_STATUS_FAIL = "fail";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            LoginRequired loginRequired = handlerMethod.getMethodAnnotation(LoginRequired.class);

            //1.不需要登录验证
            if(loginRequired == null){
                log.debug("【 //1.不需要登录验证】"+handlerMethod.getMethod().getName()+","
                                +",url=" + request.getRequestURL());
                return true;
            }
            //2.需要登录验证
            log.debug("【 //2.需要登录验证】");
            String token = null;
            String cookieToken = CookieUtil.getCookieValue(request, COOKIE_TOKEN, true);
            log.debug("【cookieToken=】" + cookieToken);
            if(StringUtils.isNotBlank(cookieToken)){
                token = cookieToken;
            }
            String requestToken = request.getParameter("token");
            log.debug("【requestToken=】" + requestToken);
            if(StringUtils.isNotBlank(requestToken)){
                token = requestToken;
            }

            log.debug("【token=】" + token);

            String verifyResult = null;
            if(StringUtils.isNotBlank(token)){
                String url = URL_MALL_PASSPORT_VERIFY + "?token=" + token + "&ip="
                        + WebUtil.getClientIp(request);
                log.debug("【 HttpClientUtil->url=】" + url);
                verifyResult =  HttpClientUtil.doGet(url);
            }
            log.debug("【verifyResult=】" + verifyResult);

            Map<String,String> loginTokenMap = null;
            String loginTokenStatus = LOGIN_TOKEN_STATUS_FAIL;
            if(StringUtils.isNotBlank(verifyResult)){
                loginTokenMap = JSON.parseObject(verifyResult,Map.class);
                loginTokenStatus = loginTokenMap.get("status");
            }
            log.debug("【loginTokenStatus=】" + loginTokenStatus);
            //检查注解方法loginSuccess
            if(loginRequired.loginSuccess()){ //2.1 必须登录成功
                log.debug("【2.1 必须登录成功】");
                if(LOGIN_TOKEN_STATUS_FAIL.equals(loginTokenStatus)){
                    log.debug("【fail】");
                    this.sendRedirectPassportIndex(request,response);
                    return false;
                }else{
                    log.debug("【success】");
                    this.setTokenIntoCookie(request,response,token);
                    this.setMemeberIntoRequest(request,loginTokenMap);
                }
            }else{//2.2 不必登录成功
                log.debug("【2.2 不必登录成功】");
                if(LOGIN_TOKEN_STATUS_SUCCESS.equals(loginTokenStatus)){
                    log.debug("【success】");
                    this.setTokenIntoCookie(request,response,token);
                    this.setMemeberIntoRequest(request,loginTokenMap);
                }
            }
        }
        return true;
    }

    private void setMemeberIntoRequest(HttpServletRequest request, Map<String,String>  loginTokenMap) {
        if(loginTokenMap != null) {
            log.debug("【set Request member】");
            String memberId = loginTokenMap.get("memberId");
            request.setAttribute("memberId", memberId);
            request.setAttribute("nickname", loginTokenMap.get("nickname"));
            log.debug("【request.set memberId=】" + memberId);
        }
    }

    private void setTokenIntoCookie(HttpServletRequest request, HttpServletResponse response, String token) {
        if(StringUtils.isNotBlank(token)){
            log.debug("【set cookie token】");
            CookieUtil.setCookie(request,response,COOKIE_TOKEN,token,COOKIE_EXPIRE_TIME,true);
        }
    }

    private void sendRedirectPassportIndex(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String directURL = URL_MALL_PASSPORT_INDEX + "?ReturnUrl=" + request.getRequestURL();
        log.debug("【directURL=】"+ directURL);
        response.sendRedirect(directURL);
    }



}
