package com.yx.mall;

import org.apache.ibatis.annotations.Mapper;

/**
 * 该接口的作用是防止控制台出现如下WARN
 * No MyBatis mapper was found in '[com.yx.mall.member]' package. Please check your configuration.
 * 解决办法：合理的目录结果绝对不允许所有的mapper都在启动类目录下，所以在启动类目录下添加了一个伪mapper
 * 参考内容：https://my.oschina.net/kevin2kelly/blog/2046324
 */
@Mapper
public interface NoWarmMapper {
}
