package com.yx.mall.search.service.impl;

import com.yx.mall.bean.SearchParam;
import com.yx.mall.bean.SearchSkuInfo;
import com.yx.mall.bean.SkuAttrValue;
import com.yx.mall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private JestClient jestClient;

    private static final String ES_INDEX = "mall";
    private static final String ES_TYPE = "SkuInfo";
    private static final Integer ES_PAGE_SIZE = 20;

    @Override
    public List<SearchSkuInfo> list(SearchParam searchParam) {
        log.debug("【searchParam】="+ searchParam);
        ArrayList<SearchSkuInfo> resSearchSkuInfos = null;
        String searchDSL = this.getSearchDSL(searchParam);
        if(StringUtils.isNotEmpty(searchDSL)){
            try{
                resSearchSkuInfos = new ArrayList<>();
                Search search = new Search.Builder(searchDSL).addIndex(ES_INDEX).addType(ES_TYPE).build();
                SearchResult searchResult = jestClient.execute(search);
                List<SearchResult.Hit<SearchSkuInfo, Void>> hits = searchResult.getHits(SearchSkuInfo.class);
                for (SearchResult.Hit<SearchSkuInfo, Void> hit : hits) {
                    SearchSkuInfo searchSkuInfo = hit.source;
                    Map<String, List<String>> highlight = hit.highlight;
                    if(highlight != null){
                        searchSkuInfo.setSkuName(highlight.get("skuName").get(0));
                    }
                    //highlight
                    resSearchSkuInfos.add(searchSkuInfo);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            log.debug("【resSearchSkuInfos=】"+resSearchSkuInfos);
        }
        return resSearchSkuInfos;
    }
    private String getSearchDSL(SearchParam searchParam) {
        String searchDSL = null;
        if(searchParam != null){
            String keyword = searchParam.getKeyword();
            Long catalog3Id = searchParam.getCatalog3Id();
            String[] selectedSkuAttrIds = searchParam.getValueId();

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

            //from
            searchSourceBuilder.from(0);
            //size
            searchSourceBuilder.size(ES_PAGE_SIZE);
            //sort
            searchSourceBuilder.sort("price", SortOrder.DESC);
            //highlight
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<span style='color:red;'>");
            highlightBuilder.field("skuName");
            highlightBuilder.postTags("</span>");
            searchSourceBuilder.highlighter(highlightBuilder);
            //query
            searchSourceBuilder.query(boolQueryBuilder);

            //filter
            if(catalog3Id != null){
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id",catalog3Id);
                boolQueryBuilder.filter(termQueryBuilder);
            }
            if(selectedSkuAttrIds != null){
                for (String selectedSkuAttrIdStr : selectedSkuAttrIds) {
                    TermQueryBuilder termQueryBuilder =
                            new TermQueryBuilder("skuAttrValueList.valueId", selectedSkuAttrIdStr);
                    boolQueryBuilder.filter(termQueryBuilder);
                }
            }
            //must
            if(StringUtils.isNotBlank(keyword)){
                MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName",keyword);
                boolQueryBuilder.must(matchQueryBuilder);
            }
            searchDSL = searchSourceBuilder.toString();
        }
        log.debug("【searchDSL=】" + searchDSL);
        return searchDSL;
    }
}
