package com.yx.mall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name="pms_product_vertify_record")
public class ProductVertifyRecord implements Serializable {
    @Id
    private Long id;

    @Column(name="product_id")
    private Long spuId;

    private Date createTime;

    private String vertifyMan;

    private Integer status;

    private String detail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSpuId() {
        return spuId;
    }

    public void setSpuId(Long spuId) {
        this.spuId = spuId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getVertifyMan() {
        return vertifyMan;
    }

    public void setVertifyMan(String vertifyMan) {
        this.vertifyMan = vertifyMan;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "ProductVertifyRecord{" +
                "id=" + id +
                ", spuId=" + spuId +
                ", createTime=" + createTime +
                ", vertifyMan='" + vertifyMan + '\'' +
                ", status=" + status +
                ", detail='" + detail + '\'' +
                '}';
    }
}