package com.yx.mall.manage.controller;

import com.yx.mall.bean.BaseCatalog1;
import com.yx.mall.bean.BaseCatalog2;
import com.yx.mall.bean.BaseCatalog3;
import com.yx.mall.service.BaseCatalogService;
import lombok.extern.log4j.Log4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
@Log4j
public class BaseCatalogController {

    @Reference
    private BaseCatalogService baseCatalogService;

    @RequestMapping("getCatalog1")
    @ResponseBody
    public List<BaseCatalog1> getCatalog1() {

        List<BaseCatalog1> baseCatalog1List = baseCatalogService.getAllCatalog1List();
        log.debug(baseCatalog1List);
        return baseCatalog1List;
    }

    @RequestMapping("getCatalog2")
    @ResponseBody
    public List<BaseCatalog2> getCatalog2(String catalog1Id){
        List<BaseCatalog2> baseCatalog2List = baseCatalogService.getCatalog2ListByCatalog1Id(catalog1Id);
        log.debug(baseCatalog2List);
        return baseCatalog2List;
    }

    @RequestMapping("getCatalog3")
    @ResponseBody
    public List<BaseCatalog3> getCatalog3(String catalog2Id){
        List<BaseCatalog3> baseCatalog3List = baseCatalogService.getCatalog3ListByCatalog2Id(catalog2Id);
        log.debug(baseCatalog3List);
        return baseCatalog3List;
    }


}
