package com.yx.mall.bean;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name="pms_base_sale_attr")
public class BaseSaleAttr implements Serializable{
    @Id
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    @Override
    public String toString() {
        return "BaseSaleAttr{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}