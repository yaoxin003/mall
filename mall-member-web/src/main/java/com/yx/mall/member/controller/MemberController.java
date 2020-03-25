package com.yx.mall.member.controller;

import com.yx.mall.service.MemberService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MemberController {

   @Reference(version = "${dubbo.service.version}")
    private MemberService memberService;

    @RequestMapping("/index")
    @ResponseBody
    public String index(){
        return "Spring boot index new";
    }

    @RequestMapping("/getAll")
    @ResponseBody
    public String getAll(){
        System.out.println(memberService.getAll());
        return "Spring boot getAll()";
    }
}