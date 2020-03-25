package com.yx.mall.bean;

import java.io.Serializable;
import java.util.List;

public class SearchSkuInfo implements Serializable {

    private Long id;
    private String skuName;
    private String skuDesc;
    private Long catalog3Id;
    private Double price;
    private String skuDefaultImg;
    private double hotScore;
    private Long spuId;
    private List<SkuAttrValue> skuAttrValueList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuDesc() {
        return skuDesc;
    }

    public void setSkuDesc(String skuDesc) {
        this.skuDesc = skuDesc;
    }

    public Long getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(Long catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSkuDefaultImg() {
        return skuDefaultImg;
    }

    public void setSkuDefaultImg(String skuDefaultImg) {
        this.skuDefaultImg = skuDefaultImg;
    }

    public double getHotScore() {
        return hotScore;
    }

    public void setHotScore(double hotScore) {
        this.hotScore = hotScore;
    }

    public Long getSpuId() {
        return spuId;
    }

    public void setSpuId(Long spuId) {
        this.spuId = spuId;
    }

    public List<SkuAttrValue> getSkuAttrValueList() {
        return skuAttrValueList;
    }

    public void setSkuAttrValueList(List<SkuAttrValue> skuAttrValueList) {
        this.skuAttrValueList = skuAttrValueList;
    }

    @Override
    public String toString() {
        return "SearchSkuInfo{" +
                "id=" + id +
                ", skuName='" + skuName + '\'' +
                ", skuDesc='" + skuDesc + '\'' +
                ", catalog3Id=" + catalog3Id +
                ", price=" + price +
                ", skuDefaultImg='" + skuDefaultImg + '\'' +
                ", hotScore=" + hotScore +
                ", spuId=" + spuId +
                ", skuAttrValueList=" + skuAttrValueList +
                '}';
    }
}
