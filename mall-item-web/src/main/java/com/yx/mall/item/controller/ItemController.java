package com.yx.mall.item.controller;

import com.alibaba.fastjson.JSON;
import com.yx.mall.bean.ProductSaleAttr;
import com.yx.mall.bean.SkuInfo;
import com.yx.mall.bean.SkuSaleAttrValue;
import com.yx.mall.service.SkuService;
import com.yx.mall.service.SpuService;
import lombok.extern.log4j.Log4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

@Controller
@Log4j
public class ItemController {

    @Reference
    private SkuService skuService;

    @Reference
    private SpuService spuService;

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable Long skuId, ModelMap modelMap){
        log.debug("开始调用item");

        /**
         * sku信息和sku图片列表
         */
        SkuInfo skuInfo = skuService.getSkuInfoById(skuId);
        log.debug("【skuInfo=】" + skuInfo);
        modelMap.put("skuInfo",skuInfo);

        /**
         * spu销售属性和销售属性值列表，sku销售属性值
         */
        Long supId = skuInfo.getSpuId();
        List<ProductSaleAttr> spuSaleAttrListCheckBySku = spuService.spuSaleAttrListCheckBySku(
                supId,skuId);
        log.debug("【spuSaleAttrListCheckBySku=】" + spuSaleAttrListCheckBySku);
        modelMap.put("spuSaleAttrListCheckBySku",spuSaleAttrListCheckBySku);

        /**
         * spu对应sku销售属性值与skuId的Hash，例如101|102|:12,101|103|:13
         */
        HashMap<String,String> skuSaleAttrValueHash = new HashMap<>();
        List<SkuInfo> skuInfos = skuService.getSkuSaleAttrValueListBySpu(supId);
        for (SkuInfo info : skuInfos) {
            StringBuffer saleAttrValueHashKey = new StringBuffer("");
            for (SkuSaleAttrValue skuSaleAttrValue : info.getSkuSaleAttrValueList()) {
                saleAttrValueHashKey.append(skuSaleAttrValue.getSaleAttrValueId());
                saleAttrValueHashKey.append("|"); //101|102
            }
            skuSaleAttrValueHash.put(saleAttrValueHashKey.toString(),String.valueOf(info.getId()));
        }
        String skuSaleAttrValueHashJsonStr = JSON.toJSONString(skuSaleAttrValueHash);
        log.debug("【skuSaleAttrValueHashJsonStr=】"+ skuSaleAttrValueHashJsonStr);
        modelMap.put("skuSaleAttrValueHashJsonStr",skuSaleAttrValueHashJsonStr);
        return "item";
    }
}
