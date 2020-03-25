package com.yx.mall.bean;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name="pms_sku_sale_attr_value")
public class SkuSaleAttrValue implements Serializable{

    @Id
    private Long id;

    private Long skuId;

    private Long saleAttrId;

    private Long saleAttrValueId;

    private String saleAttrName;

    private String saleAttrValueName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getSaleAttrId() {
        return saleAttrId;
    }

    public void setSaleAttrId(Long saleAttrId) {
        this.saleAttrId = saleAttrId;
    }

    public Long getSaleAttrValueId() {
        return saleAttrValueId;
    }

    public void setSaleAttrValueId(Long saleAttrValueId) {
        this.saleAttrValueId = saleAttrValueId;
    }

    public String getSaleAttrName() {
        return saleAttrName;
    }

    public void setSaleAttrName(String saleAttrName) {
        this.saleAttrName = saleAttrName == null ? null : saleAttrName.trim();
    }

    public String getSaleAttrValueName() {
        return saleAttrValueName;
    }

    public void setSaleAttrValueName(String saleAttrValueName) {
        this.saleAttrValueName = saleAttrValueName == null ? null : saleAttrValueName.trim();
    }

    @Override
    public String toString() {
        return "SkuSaleAttrValue{" +
                "id=" + id +
                ", skuId=" + skuId +
                ", saleAttrId=" + saleAttrId +
                ", saleAttrValueId=" + saleAttrValueId +
                ", saleAttrName='" + saleAttrName + '\'' +
                ", saleAttrValueName='" + saleAttrValueName + '\'' +
                '}';
    }
}