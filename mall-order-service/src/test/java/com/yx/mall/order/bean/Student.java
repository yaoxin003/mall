package com.yx.mall.order.bean;

/**
 * @description:
 * @author: yx
 * @date: 2019/12/31/17:57
 */
public class Student {
    private String name;

    private Integer no;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", no=" + no +
                '}';
    }
}
