package com.yx.mall.bean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class SearchParam implements Serializable {

    private String keyword;
    private Long catalog3Id;
    private String[] valueId;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(Long catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String[] getValueId() {
        return valueId;
    }

    public void setValueId(String[] valueId) {
        this.valueId = valueId;
    }

    @Override
    public String toString() {
        return "SearchParam{" +
                "keyword='" + keyword + '\'' +
                ", catalog3Id=" + catalog3Id +
                ", valueId=" + Arrays.toString(valueId) +
                '}';
    }
}
