package com.yx.mall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

@Table(name="pms_product_sale_attr_value")
public class ProductSaleAttrValue implements Serializable {
    @Id
    private Long id;

    @Column(name="product_id")
    private Long spuId;

    private Long saleAttrId;

    private String saleAttrValueName;

    @Transient
    private String isChecked;

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

    public String getSaleAttrValueName() {
        return saleAttrValueName;
    }

    public void setSaleAttrValueName(String saleAttrValueName) {
        this.saleAttrValueName = saleAttrValueName;
    }

    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public String toString() {
        return "ProductSaleAttrValue{" +
                "id=" + id +
                ", spuId=" + spuId +
                ", saleAttrId=" + saleAttrId +
                ", saleAttrValueName='" + saleAttrValueName + '\'' +
                ", isChecked='" + isChecked + '\'' +
                '}';
    }
}