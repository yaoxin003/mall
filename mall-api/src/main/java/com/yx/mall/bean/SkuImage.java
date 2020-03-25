package com.yx.mall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name="pms_sku_image")
public class SkuImage implements Serializable{

    @Id
    private Long id;

    private Long skuId;

    private String imgName;

    private String imgUrl;

    @Column(name="product_img_id")
    private Long spuImgId;

    private String isDefault;

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

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName == null ? null : imgName.trim();
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl == null ? null : imgUrl.trim();
    }

    public Long getSpuImgId() {
        return spuImgId;
    }

    public void setSpuImgId(Long spuImgId) {
        this.spuImgId = spuImgId;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault == null ? null : isDefault.trim();
    }

    @Override
    public String toString() {
        return "SkuImage{" +
                "id=" + id +
                ", skuId=" + skuId +
                ", imgName='" + imgName + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", spuImgId=" + spuImgId +
                ", isDefault='" + isDefault + '\'' +
                '}';
    }
}