package com.yx.mall.search;

import com.yx.mall.bean.SearchSkuInfo;
import com.yx.mall.bean.SkuInfo;
import com.yx.mall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import lombok.extern.log4j.Log4j;
import org.apache.dubbo.config.annotation.Reference;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log4j
public class MallSearchServiceApplicationTests {

    @Reference
    private SkuService skuService;

    @Autowired
    private JestClient jestClient;

    private static final String ES_INDEX = "mall";
    private static final String ES_TYPE = "SkuInfo";

    /**
     * 使用jest客户端，将mysql库中pms_sku_info表数据导入EalsticSearch库SkuInfo表中。
     * @throws Exception
     */
    @Test
    public void contextLoads() throws Exception {
        //init();
        search();
    }

    private void search()  throws Exception{
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //过滤条件
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", "43");
        boolQueryBuilder.filter(termQueryBuilder);
        //查询条件
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", "华为");
        boolQueryBuilder.must(matchQueryBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(100);
        searchSourceBuilder.highlighter();

        String dslStr = searchSourceBuilder.toString();

        log.debug("【searchSourceBuilder=】"+dslStr);
        Search search = new Search.Builder(dslStr).addIndex(ES_INDEX).addType(ES_TYPE).build();

        SearchResult execute = jestClient.execute(search);

        //打印查询结果
        List<SearchResult.Hit<SearchSkuInfo, Void>> hits = execute.getHits(SearchSkuInfo.class);
        int resCount = 0;
        for (SearchResult.Hit<SearchSkuInfo, Void> hit : hits) {
            SearchSkuInfo source = hit.source;
            log.debug("【resCount=】" + ++resCount + "【searchSkuInfo=】" + source);
        }

    }

    private void init() throws Exception{
        List<SkuInfo> skuInfoList = skuService.getAllSkuInfoList();
        List<SearchSkuInfo> searchSkuInfos = new ArrayList<SearchSkuInfo>();
        for (SkuInfo skuInfo : skuInfoList) {
            SearchSkuInfo searchSkuInfo = new SearchSkuInfo();
            BeanUtils.copyProperties(skuInfo, searchSkuInfo);
            searchSkuInfos.add(searchSkuInfo);
        }
        log.debug("【searchSkuInfos】" + searchSkuInfos);

        for (SearchSkuInfo searchSkuInfo : searchSkuInfos) {
            Long searchSkuInfoId = searchSkuInfo.getId();
            if (searchSkuInfoId !=null){
                Index index = new Index.Builder(searchSkuInfo).index(ES_INDEX).type(ES_TYPE).
                        id(String.valueOf(searchSkuInfoId)).build();
                jestClient.execute(index);
            }
        }
    }

}
