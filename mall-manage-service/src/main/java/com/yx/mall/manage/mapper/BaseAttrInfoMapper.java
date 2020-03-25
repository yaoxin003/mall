package com.yx.mall.manage.mapper;

import com.yx.mall.bean.BaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo>{

    public List<BaseAttrInfo> selectBaseAttrInfoListByBaseAttrValueIdList(
                            @Param("baseAttrValueIdList") List<Long> baseAttrValueIdList);
}
