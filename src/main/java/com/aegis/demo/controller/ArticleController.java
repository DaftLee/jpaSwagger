package com.aegis.demo.controller;

import com.aegis.demo.pojo.Article;
import com.aegis.demo.pojo.SimpleResponse;
import com.aegis.demo.pojo.Student;
import com.aegis.demo.repository.ArticleRepository;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 李成超
 * @date 2019/10/14 15:12
 * @description TODO
 **/
@Api(description = "文章接口（es实现）")
@RestController
@RequestMapping("article")
public class ArticleController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
    static final String preTag = "<font color='#dd4b39'>";
    static final String postTag = "</font>";

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @ApiOperation(value = "查询文章列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",defaultValue = "0",dataType = "int",paramType = "query",value = "分页页数",required = false),
            @ApiImplicitParam(name = "size",defaultValue = "5",dataType = "int",paramType = "query",value = "每页条数",required = false)
    })
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public SimpleResponse<List<Article>> list(@RequestParam(value = "page",defaultValue = "0") Integer page, @RequestParam(value = "size",defaultValue = "5") Integer size){
        SimpleResponse<List<Article>> simpleResponse = new SimpleResponse<>();
        try {
            Sort sort = new Sort(Sort.Direction.DESC,"id");
            Pageable pageable = new PageRequest(page,size,sort);
            Page<Article> all = articleRepository.findAll(pageable);
            if (all.getContent()!=null || all.getContent().size()>0){
                simpleResponse.setData(all.getContent());
            }
            simpleResponse.setCode("0");
            simpleResponse.setMsg("success");

        }catch (Exception e){
            logger.error("查询文章列表失败",e);
            simpleResponse.setCode("99");
            simpleResponse.setMsg("查询学生列表异常,"+e.getMessage());

        }
        return simpleResponse;
    }
    @ApiOperation(value = "查询列表(queryString、分页、高亮)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",defaultValue = "0",dataType = "int",paramType = "query",value = "分页页数",required = false),
            @ApiImplicitParam(name = "size",defaultValue = "5",dataType = "int",paramType = "query",value = "每页条数",required = false),
            @ApiImplicitParam(name = "keyword",dataType = "String",paramType = "query",value = "关键字",required = true),
    })
    @RequestMapping(value = "/get",method = RequestMethod.GET)
    public SimpleResponse<List<Article>> get(@RequestParam(value = "page",defaultValue = "0") Integer page, @RequestParam(value = "size",defaultValue = "5") Integer size,@RequestParam(value = "keyword") String keyword){
        SimpleResponse<List<Article>> simpleResponse = new SimpleResponse<>();

        try {
            Sort sort = new Sort(Sort.Direction.DESC,"id");
            Pageable pageable = new PageRequest(page,size,sort);
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
//                    .withQuery(QueryBuilders.matchQuery("title",keyword))
                    .withQuery(QueryBuilders.queryStringQuery(keyword))
                    .withHighlightFields(new HighlightBuilder.Field("title").preTags(preTag).postTags(postTag),new HighlightBuilder.Field("content").preTags(preTag).postTags(postTag))
                    .withPageable(pageable)
                    .build();
//            Page<Article> all = articleRepository.search(searchQuery);

            Page<Article> all = elasticsearchTemplate.queryForPage(searchQuery, Article.class, new SearchResultMapper() {
                // 高亮
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                    //获取高亮的结果
                    SearchHits searchHit = searchResponse.getHits();
                    long totalHits = searchHit.getTotalHits();
                    SearchHit[] hits = searchHit.getHits();
                    //定义查询出来内容存储的集合
                    List<Article> list = new ArrayList<>();
                    for (SearchHit hit : hits){
                        Article article = new Article();
                        String id = hit.getId();
                        article.setId(Long.parseLong(id));
                        //获取title字段的高亮内容
                        HighlightField title = hit.getHighlightFields().get("title");
                        if (title != null){
                            //获取第一个字段的值并封装给实体类
                            String freagmentstring = title.fragments()[0].toString();
                            article.setTitle(freagmentstring);
                        } else {
                            //获取原始值
                            String title1 = (String) hit.getSourceAsMap().get("title");
                            article.setTitle(title1);
                        }

                        //获取content字段的高亮内容
                        HighlightField content = hit.getHighlightFields().get("content");
                        if (content != null){
                            //获取第一个字段的值并封装给实体类
                            String freagmentstring = content.fragments()[0].toString();
                            article.setContent(freagmentstring);
                        } else {
                            //获取原始值
                            String content1 = (String) hit.getSourceAsMap().get("content");
                            article.setContent(content1);
                        }

                        list.add(article);
                    }

                    return new AggregatedPageImpl<>((List<T>)list);
                }
            });


            if (all.getContent()!=null){
                simpleResponse.setData(all.getContent());
            }
            simpleResponse.setCode("0");
            simpleResponse.setMsg("success");

        }catch (Exception e){
            logger.error("查询列表(queryString)失败",e);
            simpleResponse.setCode("99");
            simpleResponse.setMsg("查询列表(queryString)异常,"+e.getMessage());

        }
        return simpleResponse;
    }

    @ApiOperation(value = "查询列表(根据文章title（reposirory方法）强匹配、不分词、分页、不高亮)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",defaultValue = "0",dataType = "int",paramType = "query",value = "分页页数",required = false),
            @ApiImplicitParam(name = "size",defaultValue = "5",dataType = "int",paramType = "query",value = "每页条数",required = false),
            @ApiImplicitParam(name = "keyword",dataType = "String",paramType = "query",value = "关键字",required = true),
    })
    @RequestMapping(value = "/getByTitle",method = RequestMethod.GET)
    public SimpleResponse<List<Article>> getByTitle(@RequestParam(value = "page",defaultValue = "0") Integer page, @RequestParam(value = "size",defaultValue = "5") Integer size,@RequestParam(value = "keyword") String keyword){
        SimpleResponse<List<Article>> simpleResponse = new SimpleResponse<>();

        try {
            Sort sort = new Sort(Sort.Direction.DESC,"id");
            Pageable pageable = new PageRequest(page,size,sort);
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.termQuery("title",keyword))
                    .withPageable(pageable)
                    .build();
            Page<Article> all = articleRepository.search(searchQuery);

            if (all.getContent()!=null){
                simpleResponse.setData(all.getContent());
            }
            simpleResponse.setCode("0");
            simpleResponse.setMsg("success");

        }catch (Exception e){
            logger.error("查询列表(queryString)失败",e);
            simpleResponse.setCode("99");
            simpleResponse.setMsg("查询列表(queryString)异常,"+e.getMessage());

        }
        return simpleResponse;
    }
}
