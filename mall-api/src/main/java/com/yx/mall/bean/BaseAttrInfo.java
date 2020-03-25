package com.yx.mall.bean;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Table(name="pms_base_attr_info")
public class BaseAttrInfo implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  /*-IDENTITY：采用数据库ID自增长的方式来自增主键字段，Oracle 不支持这种方式；
    –AUTO： JPA自动选择合适的策略，是默认选项；*/

    private String attrName;

    private Long catalog3Id;

    private String isEnabled;

    @Transient
    private List<BaseAttrValue> attrValueList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName == null ? null : attrName.trim();
    }

    public Long getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(Long catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(String isEnabled) {
        this.isEnabled = isEnabled == null ? null : isEnabled.trim();
    }

    public List<BaseAttrValue> getAttrValueList() {
        return attrValueList;
    }

    public void setAttrValueList(List<BaseAttrValue> attrValueList) {
        this.attrValueList = attrValueList;
    }

    @Override
    public String toString() {
        return "BaseAttrInfo{" +
                "id=" + id +
                ", attrName='" + attrName + '\'' +
                ", catalog3Id=" + catalog3Id +
                ", isEnabled='" + isEnabled + '\'' +
                ", attrValueList=" + attrValueList +
                '}';
    }
}