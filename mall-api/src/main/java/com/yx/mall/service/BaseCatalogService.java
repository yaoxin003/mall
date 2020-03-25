package com.yx.mall.service;

import com.yx.mall.bean.BaseCatalog1;
import com.yx.mall.bean.BaseCatalog2;
import com.yx.mall.bean.BaseCatalog3;

import java.util.List;

public interface BaseCatalogService {

    public List<BaseCatalog1> getAllCatalog1List();

    public List<BaseCatalog2> getCatalog2ListByCatalog1Id(String catalog1Id);

    List<BaseCatalog3> getCatalog3ListByCatalog2Id(String catalog2Id);
}
