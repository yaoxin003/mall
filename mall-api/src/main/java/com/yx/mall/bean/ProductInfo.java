package com.yx.mall.bean;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Table(name="pms_product_info")
public class ProductInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="product_name")
    private String spuName;

    private String description;

    private Long catalog3Id;

    private Long tmId;

    @Transient
    private List<ProductImage> spuImageList;

    @Transient
    private List<ProductSaleAttr> spuSaleAttrList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpuName() {
        return spuName;
    }

    public void setSpuName(String spuName) {
        this.spuName = spuName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Long getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(Long catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public Long getTmId() {
        return tmId;
    }

    public void setTmId(Long tmId) {
        this.tmId = tmId;
    }

    public List<ProductImage> getSpuImageList() {
        return spuImageList;
    }

    public void setSpuImageList(List<ProductImage> spuImageList) {
        this.spuImageList = spuImageList;
    }

    public List<ProductSaleAttr> getSpuSaleAttrList() {
        return spuSaleAttrList;
    }

    public void setSpuSaleAttrList(List<ProductSaleAttr> spuSaleAttrList) {
        this.spuSaleAttrList = spuSaleAttrList;
    }

    @Override
    public String toString() {
        return "ProductInfo{" +
                "id=" + id +
                ", spuName='" + spuName + '\'' +
                ", description='" + description + '\'' +
                ", catalog3Id=" + catalog3Id +
                ", tmId=" + tmId +
                ", spuImageList=" + spuImageList +
                ", spuSaleAttrList=" + spuSaleAttrList +
                '}';
    }
}