package com.yx.mall.manage.mapper;

import com.yx.mall.bean.SkuInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuInfoMapper extends Mapper<SkuInfo>{

    public List<SkuInfo> selectSkuSaleAttrValueListBySpu(@Param("spuId") Long spuId);
}
