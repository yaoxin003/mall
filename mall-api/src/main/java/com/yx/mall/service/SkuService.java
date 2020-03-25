package com.yx.mall.service;

import com.yx.mall.bean.SkuInfo;
import java.util.List;

public interface SkuService {

    public void saveSkuInfo(SkuInfo skuInfo);

    public SkuInfo getSkuInfoByIdFromDBAndCache(Long skuId);

    public SkuInfo getSkuInfoById(Long skuId);

    public List<SkuInfo> getSkuSaleAttrValueListBySpu(Long spuId);

    public List<SkuInfo> getAllSkuInfoList();
}
