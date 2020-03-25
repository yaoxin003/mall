package com.yx.mall.manage.controller;

import com.yx.mall.bean.BaseAttrInfo;
import com.yx.mall.bean.BaseAttrValue;
import com.yx.mall.bean.BaseSaleAttr;
import com.yx.mall.service.AttrService;
import lombok.extern.log4j.Log4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@Log4j
@CrossOrigin
public class AttrController {

    @Reference
    private AttrService attrService;

    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<BaseAttrInfo> getAttrInfoList(String catalog3Id){
        List<BaseAttrInfo> baseAttrInfoList = attrService.getBaseAttrInfoListByCatalog3Id(catalog3Id);
        log.debug(baseAttrInfoList);
        return baseAttrInfoList;
    }

    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody BaseAttrInfo pmsBaseAttrInfo){
        log.info("入参{}:"+ pmsBaseAttrInfo );
        attrService.saveAttrInfoAndValues(pmsBaseAttrInfo);
        return "success";
    }

    @RequestMapping("getAttrValueList")
    @ResponseBody
    public List<BaseAttrValue> getAttrValueList(String attrId){
        List<BaseAttrValue> attrValueList = attrService.getAttrValueListByAttrId(attrId);
        log.info("attrValueList："+ attrValueList);
        return attrValueList;
    }


    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<BaseSaleAttr> baseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrList = attrService.baseSaleAttrList();
        log.info("【baseSaleAttrList】=" + baseSaleAttrList);
        return baseSaleAttrList;
    }


}
