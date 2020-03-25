package com.yx.mall.member.controller;

import com.yx.mall.service.MemberReceiveAddressService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MemberReceiveAddressController {

    @Reference(version = "${dubbo.service.version}")
    private MemberReceiveAddressService memberReceiveAddressService;

    @RequestMapping("/getMemberReceiveAddressListByMemberId")
    @ResponseBody
    public String getMemberReceiveAddressListByMemberId(Long memberId){
        memberReceiveAddressService.getMemberReceiveAddressListByMemberId(memberId);
        return "显示信息";
    }

}
