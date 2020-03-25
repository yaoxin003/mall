package com.yx.mall.manage.controller;

import com.yx.mall.bean.ProductImage;
import com.yx.mall.bean.ProductInfo;
import com.yx.mall.bean.ProductSaleAttr;
import com.yx.mall.manage.util.UploadUtil;
import com.yx.mall.service.SpuService;
import lombok.extern.log4j.Log4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@CrossOrigin
@Log4j
public class SpuController {

    @Reference
    private SpuService spuService;

    @RequestMapping("spuList")
    @ResponseBody
    public List<ProductInfo> spuList(String catalog3Id){
        List<ProductInfo> productInfoList = spuService.getProductInfoByCatalog3Id(catalog3Id);
        log.info("【productInfoList=】" + productInfoList);
        return productInfoList;
    }

    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file")MultipartFile multipartFile){
        String imgUrl = "";
        try{
            imgUrl = UploadUtil.uploadFile(multipartFile);
        }catch (Exception e){
            e.printStackTrace();
        }
        return imgUrl;
    }

    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String saveSpuInfo(@RequestBody ProductInfo productInfo){
        log.info("入参：" + productInfo);
        spuService.saveSpuInfo(productInfo);
        return "success";
    }

    @RequestMapping("spuImageList")
    @ResponseBody
    public List<ProductImage> spuImageList(Long spuId){
        List<ProductImage> productImages = spuService.spuImageList(spuId);
        log.debug("【productImages=】" + productImages);
        return productImages;
    }

    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<ProductSaleAttr> spuSaleAttrList(Long spuId){
        List<ProductSaleAttr> productSaleAttrs = spuService.spuSaleAttrList(spuId);
        log.debug("【productSaleAttrs=】" + productSaleAttrs);
        return productSaleAttrs;
    }

}
