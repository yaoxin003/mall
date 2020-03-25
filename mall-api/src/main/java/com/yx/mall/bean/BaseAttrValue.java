package com.yx.mall.bean;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name="pms_base_attr_value")
public class BaseAttrValue implements Serializable{

    @Id
    private Long id;

    private String valueName;

    private Long attrId;

    private String isEnabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public Long getAttrId() {
        return attrId;
    }

    public void setAttrId(Long attrId) {
        this.attrId = attrId;
    }

    public String getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(String isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public String toString() {
        return "BaseAttrValue{" +
                "id=" + id +
                ", valueName='" + valueName + '\'' +
                ", attrId=" + attrId +
                ", isEnabled='" + isEnabled + '\'' +
                '}';
    }
}