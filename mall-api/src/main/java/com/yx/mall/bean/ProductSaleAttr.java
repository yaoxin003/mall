package com.yx.mall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

@Table(name="pms_product_sale_attr")
public class ProductSaleAttr implements Serializable {
    @Id
    private Long id;

    @Column(name="product_id")
    private Long spuId;

    private Long saleAttrId;

    private String saleAttrName;

    @Transient
    private List<ProductSaleAttrValue> spuSaleAttrValueList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSpuId() {
        return spuId;
    }

    public void setSpuId(Long spuId) {
        this.spuId = spuId;
    }

    public Long getSaleAttrId() {
        return saleAttrId;
    }

    public void setSaleAttrId(Long saleAttrId) {
        this.saleAttrId = saleAttrId;
    }

    public String getSaleAttrName() {
        return saleAttrName;
    }

    public void setSaleAttrName(String saleAttrName) {
        this.saleAttrName = saleAttrName;
    }

    public List<ProductSaleAttrValue> getSpuSaleAttrValueList() {
        return spuSaleAttrValueList;
    }

    public void setSpuSaleAttrValueList(List<ProductSaleAttrValue> spuSaleAttrValueList) {
        this.spuSaleAttrValueList = spuSaleAttrValueList;
    }

    @Override
    public String toString() {
        return "ProductSaleAttr{" +
                "id=" + id +
                ", spuId=" + spuId +
                ", saleAttrId=" + saleAttrId +
                ", saleAttrName='" + saleAttrName + '\'' +
                ", spuSaleAttrValueList=" + spuSaleAttrValueList +
                '}';
    }
}