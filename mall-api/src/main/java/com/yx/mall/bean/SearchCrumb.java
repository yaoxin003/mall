package com.yx.mall.bean;

public class SearchCrumb {
    private String urlParam;
    private Long valueId;
    private String valueName;

    public String getUrlParam() {
        return urlParam;
    }

    public void setUrlParam(String urlParam) {
        this.urlParam = urlParam;
    }

    public Long getValueId() {
        return valueId;
    }

    public void setValueId(Long valueId) {
        this.valueId = valueId;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    @Override
    public String toString() {
        return "SearchCrumb{" +
                "urlParam='" + urlParam + '\'' +
                ", valueId=" + valueId +
                ", valueName='" + valueName + '\'' +
                '}';
    }
}
