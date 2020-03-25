package com.yx.mall.service;

import com.yx.mall.bean.BaseAttrInfo;
import com.yx.mall.bean.SearchParam;
import com.yx.mall.bean.SearchSkuInfo;

import java.util.List;

public interface SearchService {

    public List<SearchSkuInfo> list(SearchParam searchParam);

}
