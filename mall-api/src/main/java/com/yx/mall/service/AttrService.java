package com.yx.mall.service;

import com.yx.mall.bean.BaseAttrInfo;
import com.yx.mall.bean.BaseAttrValue;
import com.yx.mall.bean.BaseSaleAttr;
import com.yx.mall.bean.SearchSkuInfo;

import java.util.List;

public interface AttrService {

    public List<BaseAttrInfo> getBaseAttrInfoListByCatalog3Id(String catalog3Id);

    public void saveAttrInfoAndValues(BaseAttrInfo pmsBaseAttrInfo);

    public List<BaseAttrValue> getAttrValueListByAttrId(String attrId);

    public List<BaseSaleAttr> baseSaleAttrList();

    public List<BaseAttrInfo> getBaseAttrInfo(List<SearchSkuInfo> searchSkuInfos);
}
