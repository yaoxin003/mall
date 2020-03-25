package com.yx.mall.passport.controller;

import com.alibaba.fastjson.JSON;
import com.yx.mall.bean.Member;
import com.yx.mall.constant.MallConstant;
import com.yx.mall.service.MemberService;
import com.yx.mall.util.HttpClientUtil;
import com.yx.mall.util.JwtUtil;
import com.yx.mall.util.WebUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Log4j
@Controller
public class PassportController {

    @Reference
    private MemberService memberService;

    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token,String ip){
        log.debug("【ip=】" + ip + ",【token=】" + token);
        String status = MallConstant.LOGIN_TOKEN_STATUS_FAIL;
        Map<String, Object> retMap = new HashMap<>();
        if(StringUtils.isNotBlank(token)){
            Map<String, Object> jwtMap = JwtUtil.decode(token, MallConstant.JWT_SECRET_KEY,DigestUtils.md5Hex(ip));
            log.debug("【jwtMap=】" + jwtMap);
            if(jwtMap != null){
                status = MallConstant.LOGIN_TOKEN_STATUS_SUCCESS;
                retMap.put("memberId",(String)jwtMap.get("memberId"));
                retMap.put("nickname",(String)jwtMap.get("nickname"));
            }
        }
        retMap.put("status",status);
        String loginTokenJSONStr = JSON.toJSONString(retMap);
        log.debug("【loginTokenJSONStr=】" + loginTokenJSONStr);
        return loginTokenJSONStr;
    }
    @RequestMapping("index")
    public String index(String ReturnUrl,ModelMap modelMap){
        log.debug("【ReturnUrl=】"+ReturnUrl);
        modelMap.put("ReturnUrl",ReturnUrl);
        modelMap.put("weiboAuthorizeURI", MallConstant.WEIBO_AUTHORIZE_URL);
        modelMap.put("weiboClientId", MallConstant.WEIBO_CLIENT_ID);
        modelMap.put("weiboCallbackURI",MallConstant.WEIBO_CALLBACK_URL);
        return "index";
    }

    /**
     *
     * @param paramMember
     * @return jwt的token
     */
    @RequestMapping("login")
    @ResponseBody
    public String login(HttpServletRequest request, Member paramMember){
        log.debug("【memberParam=】"+paramMember);
        String currentIp = WebUtil.getClientIp(request);
        log.debug("【currentIp=】" + currentIp);
        String jwtToken = null;
        if(paramMember != null){
            jwtToken = memberService.login(paramMember,currentIp);
        }
        log.debug("【jwtToken=】" + jwtToken);
        return jwtToken;
    }

    @RequestMapping("vlogin")
    public String vlogin(String code,HttpServletRequest request){
        log.debug("【code=】" + code);
        String ip = WebUtil.getClientIp(request);
        log.debug("【ip=】" + ip);
        String jwtToken = memberService.weiboLogin(code,ip);
        log.debug("【jwtToken=】" + jwtToken);
        return "redirect:" + MallConstant.MALL_SERACH_URI + "?token=" + jwtToken;
    }


}
