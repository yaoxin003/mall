package com.yx.mall.manage.service.impl;

import com.yx.mall.bean.*;
import com.yx.mall.manage.mapper.BaseAttrInfoMapper;
import com.yx.mall.manage.mapper.BaseAttrValueMapper;
import com.yx.mall.manage.mapper.BaseSaleAttrMapper;
import com.yx.mall.service.AttrService;
import lombok.extern.log4j.Log4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Log4j
@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;


    @Override
    public List<BaseAttrInfo> getBaseAttrInfoListByCatalog3Id(String catalog3Id) {
        Long cataLog3Id = Long.parseLong(catalog3Id);
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(cataLog3Id);
        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.select(baseAttrInfo);
        for (BaseAttrInfo attrInfo : baseAttrInfos) {
            BaseAttrValue baseAttrValue = new BaseAttrValue();
            baseAttrValue.setAttrId(attrInfo.getId());
            attrInfo.setAttrValueList(baseAttrValueMapper.select(baseAttrValue));
        }
        return baseAttrInfos;
    }

    /**添加和修改是一个方法。
     * 整体操作方式：先判断前端系统是否传递AttrId，若未传递AttrId则为为添加操作，否则为修改操作。
     *  修改方法使用：先删除BaseAttrValue再添加BaseAttrValue的方式。
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public void saveAttrInfoAndValues(BaseAttrInfo pmsBaseAttrInfo)  {
        log.info("入参{}:"+ pmsBaseAttrInfo );
        Long attrId = pmsBaseAttrInfo.getId();
        //修改操作
        if( attrId != null && attrId != 0){
            this.updateAttrInfoAndValue(pmsBaseAttrInfo);
        }else{//添加操作
            this.addAttrInfoAndValue(pmsBaseAttrInfo);
        }

    }

    private void updateAttrInfoAndValue(BaseAttrInfo pmsBaseAttrInfo) {
        Long attrId = pmsBaseAttrInfo.getId();
        log.info("修改平台属名和值");
        //修改BaseAttrInfo
        log.info("修改平台属名：" + attrId);
        Example example = new Example(BaseAttrInfo.class);
        example.createCriteria().andEqualTo("id",attrId);
        int count = baseAttrInfoMapper.updateByExampleSelective(pmsBaseAttrInfo,example);
        if(count > 0){
            //删除BaseAttrValue
            BaseAttrValue dataAttrValue = new BaseAttrValue();
            dataAttrValue.setAttrId(attrId);
            log.info("删除" + attrId + "属性值");
            int valueCount= baseAttrValueMapper.delete(dataAttrValue);
            log.info("删除" + attrId + "属性值共" + valueCount + "条");
            if(valueCount > 0){
                //添加BaseAttrValue
                log.info("添加" + attrId + "属性值");
                for(BaseAttrValue value : pmsBaseAttrInfo.getAttrValueList()){
                    value.setId(null);
                    value.setAttrId(attrId);
                    baseAttrValueMapper.insert(value);
                }
            }
        }

    }

    private void addAttrInfoAndValue(BaseAttrInfo pmsBaseAttrInfo) {
        log.info("添加平台属名和值");
        List<BaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
        log.info("添加属性名");
        long attrInfoId = baseAttrInfoMapper.insertSelective(pmsBaseAttrInfo);
        if(attrInfoId > 0){
            Long attrId = pmsBaseAttrInfo.getId();
            log.info("添加" + attrId + "属性值");
            for(BaseAttrValue value : attrValueList){
                value.setAttrId(attrId);
                baseAttrValueMapper.insert(value);
            }
        }
    }

    @Override
    public List<BaseAttrValue> getAttrValueListByAttrId(String attrId) {
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(Long.parseLong(attrId));
        return baseAttrValueMapper.select(baseAttrValue);
    }

    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {
        List<BaseSaleAttr> baseSaleAttrList = baseSaleAttrMapper.selectAll();
        log.info("查询结果：" + baseSaleAttrList);
        return baseSaleAttrList;
    }


    /**
     * 前台客户选中的平台属性名和属性值集合
     * @param searchSkuInfos
     * @return
     */
    @Override
    public List<BaseAttrInfo> getBaseAttrInfo(List<SearchSkuInfo> searchSkuInfos) {
        List<Long> baseAttrValueIDList = this.getBaseAttrValueIdList(searchSkuInfos);
        List<BaseAttrInfo> baseAttrInfos = null;
        if(baseAttrValueIDList != null && baseAttrValueIDList.size()>0){
            baseAttrInfos = baseAttrInfoMapper.selectBaseAttrInfoListByBaseAttrValueIdList(baseAttrValueIDList);
        }
        return baseAttrInfos;
    }

    /**
     * 前台客户选中的属性值集合
     * @param searchSkuInfos
     * @return
     */
    private List<Long> getBaseAttrValueIdList(List<SearchSkuInfo> searchSkuInfos) {
        List<Long> baseAttrValueIdList = null;
        if(searchSkuInfos != null){
            baseAttrValueIdList = new ArrayList<Long>();
            for (SearchSkuInfo searchSkuInfo : searchSkuInfos) {
                for (SkuAttrValue skuAttrValue : searchSkuInfo.getSkuAttrValueList()) {
                    Long valueId = skuAttrValue.getValueId();
                    baseAttrValueIdList.add(valueId);
                }
            }
        }
        return baseAttrValueIdList;
    }
}
