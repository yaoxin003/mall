package com.yx.mall.manage.mapper;

import com.yx.mall.bean.ProductSaleAttr;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ProductSaleAttrMapper extends Mapper<ProductSaleAttr> {

    public List<ProductSaleAttr> selectProductSaleAttrAndValueListBySku(
            @Param("productId")Long productId,@Param("skuId") Long skuId);
}
