package com.yx.mall.bean;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name="pms_base_catalog2")
public class BaseCatalog2 implements Serializable {

    @Id
    private Integer id;

    private String name;

    private Integer catalog1Id;

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
        this.name = name == null ? null : name.trim();
    }

    public Integer getCatalog1Id() {
        return catalog1Id;
    }

    public void setCatalog1Id(Integer catalog1Id) {
        this.catalog1Id = catalog1Id;
    }

    @Override
    public String toString() {
        return "BaseCatalog2{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", catalog1Id=" + catalog1Id +
                '}';
    }
}