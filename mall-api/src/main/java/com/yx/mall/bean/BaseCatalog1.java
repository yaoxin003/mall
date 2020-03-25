package com.yx.mall.bean;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name="pms_base_catalog1")
public class BaseCatalog1 implements Serializable {
    @Id
    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BaseCatalog1{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}