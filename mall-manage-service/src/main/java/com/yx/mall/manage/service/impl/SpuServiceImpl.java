package com.yx.mall.manage.service.impl;

import com.yx.mall.bean.ProductImage;
import com.yx.mall.bean.ProductInfo;
import com.yx.mall.bean.ProductSaleAttr;
import com.yx.mall.bean.ProductSaleAttrValue;
import com.yx.mall.manage.mapper.ProductImageMapper;
import com.yx.mall.manage.mapper.ProductInfoMapper;
import com.yx.mall.manage.mapper.ProductSaleAttrMapper;
import com.yx.mall.manage.mapper.ProductSaleAttrValueMapper;
import com.yx.mall.service.SpuService;
import lombok.extern.log4j.Log4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log4j
public class SpuServiceImpl implements SpuService {

    @Autowired
    private ProductInfoMapper productInfoMapper;

    @Autowired
    private ProductImageMapper productImageMapper;

    @Autowired
    private ProductSaleAttrMapper productSaleAttrMapper;

    @Autowired
    private ProductSaleAttrValueMapper productSaleAttrValueMapper;

    @Override
    public List<ProductInfo> getProductInfoByCatalog3Id(String catalog3Id) {
        ProductInfo productInfo = new ProductInfo();
        productInfo.setCatalog3Id(Long.parseLong(catalog3Id));
        List<ProductInfo> productInfos = productInfoMapper.select(productInfo);
        log.debug("【productInfos】=" + productInfos);
        return productInfos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSpuInfo(ProductInfo productInfo) {
        log.debug("【productInfo=】" + productInfo);
        int spuCount = productInfoMapper.insertSelective(productInfo);
        Long spuId = productInfo.getId();
        if(spuCount > 0){
            for (ProductImage productImage : productInfo.getSpuImageList()) {
                productImage.setSpuId(spuId);
                productImageMapper.insert(productImage);
            }
            for (ProductSaleAttr productSaleAttr : productInfo.getSpuSaleAttrList()) {
                productSaleAttr.setSpuId(spuId);
                productSaleAttrMapper.insertSelective(productSaleAttr);
                for (ProductSaleAttrValue productSaleAttrValue : productSaleAttr.getSpuSaleAttrValueList()) {
                    productSaleAttrValue.setSpuId(spuId);
                    productSaleAttrValueMapper.insert(productSaleAttrValue);
                }
            }
        }
    }

    @Override
    public List<ProductImage> spuImageList(Long spuId) {
        ProductImage productImage = new ProductImage();
        productImage.setSpuId(spuId);
        List<ProductImage> productImages = productImageMapper.select(productImage);
        log.debug("【productImages=】" + productImages);
        return productImages;
    }

    @Override
    public List<ProductSaleAttr> spuSaleAttrList(Long spuId) {
        ProductSaleAttr productSaleAttr = new ProductSaleAttr();
        productSaleAttr.setSpuId(spuId);
        List<ProductSaleAttr> productSaleAttrs = productSaleAttrMapper.select(productSaleAttr);
        for (ProductSaleAttr saleAttr : productSaleAttrs) {
            ProductSaleAttrValue saleAttrValue = new ProductSaleAttrValue();
            saleAttrValue.setSaleAttrId(saleAttr.getSaleAttrId());
            saleAttrValue.setSpuId(spuId);
            saleAttr.setSpuSaleAttrValueList(productSaleAttrValueMapper.select(saleAttrValue));
        }
        log.debug("【productSaleAttrs=】" + productSaleAttrs);
        return productSaleAttrs;
    }

    @Override
    public List<ProductSaleAttr> spuSaleAttrListCheckBySku(Long productId, Long skuId) {
        List<ProductSaleAttr> productSaleAttrs = productSaleAttrMapper.
                selectProductSaleAttrAndValueListBySku(productId, skuId);
        log.debug("【productSaleAttrs=】" + productSaleAttrs);
        return productSaleAttrs;
    }


}
