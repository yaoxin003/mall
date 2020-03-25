package com.yx.mall.manage.service.impl;

import com.yx.mall.bean.BaseCatalog1;
import com.yx.mall.bean.BaseCatalog2;
import com.yx.mall.bean.BaseCatalog3;
import com.yx.mall.manage.mapper.BaseCatalog1Mapper;
import com.yx.mall.manage.mapper.BaseCatalog2Mapper;
import com.yx.mall.manage.mapper.BaseCatalog3Mapper;
import com.yx.mall.service.BaseCatalogService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class BaseCatalogServiceImpl implements BaseCatalogService {

    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;

    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;

    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;

    @Override
    public List<BaseCatalog1> getAllCatalog1List() {
        return baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getCatalog2ListByCatalog1Id(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(Integer.parseInt(catalog1Id));
        return baseCatalog2Mapper.select(baseCatalog2);
    }

    @Override
    public List<BaseCatalog3> getCatalog3ListByCatalog2Id(String catalog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(Long.parseLong(catalog2Id));
        List<BaseCatalog3> baseCatalog3List = baseCatalog3Mapper.select(baseCatalog3);
        return baseCatalog3List;
    }


}
