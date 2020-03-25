package com.yx.mall.service;

import com.yx.mall.bean.ProductImage;
import com.yx.mall.bean.ProductInfo;
import com.yx.mall.bean.ProductSaleAttr;

import java.util.List;

public interface SpuService {

    public List<ProductInfo> getProductInfoByCatalog3Id(String catalog3Id);

    public void saveSpuInfo(ProductInfo productInfo);

    public List<ProductImage> spuImageList(Long spuId);

    public List<ProductSaleAttr> spuSaleAttrList(Long spuId);

    public List<ProductSaleAttr> spuSaleAttrListCheckBySku(Long productId, Long skuId);
}
