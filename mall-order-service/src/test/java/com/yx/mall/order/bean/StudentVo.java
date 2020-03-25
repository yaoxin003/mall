package com.yx.mall.order.bean;

/**
 * @description:
 * @author: yx
 * @date: 2019/12/31/17:57
 */
public class StudentVo {
    private String name;
    private int no;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    @Override
    public String toString() {
        return "StudentVo{" +
                "name='" + name + '\'' +
                ", no=" + no +
                '}';
    }
}
