package com.yx.mall.order.bean;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @description:
 * @author: yx
 * @date: 2019/12/31/17:58
 */
public class Test {

    public static void main(String[] args) {
        Student s = new Student();
        s.setName("yx");
        s.setNo(1);
        StudentVo svo = new StudentVo();
        try {
            BeanUtils.copyProperties(svo,s);
            System.out.println(svo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
