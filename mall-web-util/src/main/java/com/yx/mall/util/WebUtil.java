package com.yx.mall.util;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Log4j
public class WebUtil {

    public static String getClientIp(HttpServletRequest request) {
        String ip = null;
        ip = request.getHeader("x-forwarded-for");
        if(StringUtils.isBlank(ip)){
            log.debug("【nginxIP=】" + ip);
            ip = request.getRemoteAddr();
        }else{
            log.debug("【remoteIP=】" + ip);
        }
        return ip;
    }
}
