package com.yx.mall.search;

import com.yx.mall.annotations.LoginRequired;
import com.yx.mall.bean.*;
import com.yx.mall.constant.MallConstant;
import com.yx.mall.service.AttrService;
import com.yx.mall.service.SearchService;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Controller
@Log4j
public class SearchController {

    @Reference
    private SearchService searchService;

    @Reference
    private AttrService attrService;

    @LoginRequired(loginSuccess = false)
    @RequestMapping("index")
    public String index(){
        return "index";
    }

    @RequestMapping("list")
    public String list(SearchParam searchParam, ModelMap modelMap){
        log.debug("【list】");
        log.debug("【searchParam=】="+ searchParam);

        if(searchParam != null){
            //ES中Sku集合
            List<SearchSkuInfo> searchSkuInfos = searchService.list(searchParam);
            log.debug("【searchSkuInfos=】"+ searchSkuInfos);
            modelMap.put("skuLsInfoList",searchSkuInfos);

            //前台客户选中的平台属性集合
            List<BaseAttrInfo> baseAttrInfos = attrService.getBaseAttrInfo(searchSkuInfos);

            //面包屑平台属性集合
            List<SearchCrumb> searchCrumbs = null;
            String urlParam = null;
            String[] selectedSkuAttrValues = searchParam.getValueId();
            log.debug("【selectedSkuAttrValues=】" + Arrays.toString(selectedSkuAttrValues));
            if(selectedSkuAttrValues != null){
                searchCrumbs = new ArrayList<SearchCrumb>();
                for (String selectedSkuAttrValueIdStr : selectedSkuAttrValues) {
                    //面包屑
                    Long selectedSkuAttrValueId = Long.parseLong(selectedSkuAttrValueIdStr);
                    SearchCrumb searchCrumb = new SearchCrumb();
                    searchCrumb.setValueId(selectedSkuAttrValueId);
                    searchCrumb.setUrlParam(this.buildUrlParam(searchParam,selectedSkuAttrValueId));
                    Iterator<BaseAttrInfo> iterator = baseAttrInfos.iterator();
                    while(iterator.hasNext()){
                        BaseAttrInfo baseAttrInfo = iterator.next();
                        for (BaseAttrValue baseAttrValue : baseAttrInfo.getAttrValueList()) {
                            if(selectedSkuAttrValueId.equals(baseAttrValue.getId())){
                                searchCrumb.setValueName(baseAttrValue.getValueName());
                                //删除前台客户选中的平台属性集合
                                log.debug("【删除前台客户选中的平台属性集合=】" + baseAttrInfo.getAttrName());
                                iterator.remove();
                            }
                        }
                    }
                    searchCrumbs.add(searchCrumb);
                }
                log.debug("【searchCrumbs】=" + searchCrumbs);
                modelMap.put("attrValueSelectedList",searchCrumbs);
            }
            //urlParam
            urlParam = this.buildUrlParam(searchParam,null);
            log.debug("【urlParam=】" + urlParam);
            if(StringUtils.isNotBlank(urlParam)){
                modelMap.put("urlParam",urlParam);
            }
            log.debug("【baseAttrInfos=】" + baseAttrInfos);
            modelMap.put("attrList",baseAttrInfos);

            //keyword
            String keyword = searchParam.getKeyword();
            if(StringUtils.isNotBlank(keyword)){
                modelMap.put("keyword",keyword);
            }
        }
        return "list";
    }

    /**
     * 面包屑urlParam和平台属性值urlParam
     * @param searchParam
     * @param skuAttrValueId
     * @return
     */
    private String buildUrlParam(SearchParam searchParam, Long skuAttrValueId) {
        String keyword = searchParam.getKeyword();
        Long catalog3Id = searchParam.getCatalog3Id();
        String[] selectedSkuAttrValueIds = searchParam.getValueId();

        String urlParam = "";
        //keyword
        if(StringUtils.isNotBlank(keyword)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam += "&";
            }
                urlParam += "keyword=" + keyword;
        }
        //catalog3Id
        if(catalog3Id != null){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam += "&";
            }
            urlParam += "catalog3Id=" + catalog3Id;
        }
        //平台属性
        if(selectedSkuAttrValueIds != null){
            for (String selectedSkuAttrValueIdStr : selectedSkuAttrValueIds) {
                Long selectedSkuAttrValueId = Long.parseLong(selectedSkuAttrValueIdStr);
                if(skuAttrValueId==null || skuAttrValueId!=selectedSkuAttrValueId){
                    if(StringUtils.isNotBlank(urlParam)){
                        urlParam += "&";
                    }
                    urlParam += "valueId=" + selectedSkuAttrValueId;
                }
            }
        }

        return urlParam;
    }

}
