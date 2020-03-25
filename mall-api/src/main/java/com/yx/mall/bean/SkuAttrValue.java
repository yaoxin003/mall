package com.yx.mall.bean;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name="pms_sku_attr_value")
public class SkuAttrValue implements Serializable{

    @Id
    private Long id;

    private Long attrId;

    private Long valueId;

    private Long skuId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAttrId() {
        return attrId;
    }

    public void setAttrId(Long attrId) {
        this.attrId = attrId;
    }

    public Long getValueId() {
        return valueId;
    }

    public void setValueId(Long valueId) {
        this.valueId = valueId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    @Override
    public String toString() {
        return "SkuAttrValue{" +
                "id=" + id +
                ", attrId=" + attrId +
                ", valueId=" + valueId +
                ", skuId=" + skuId +
                '}';
    }
}