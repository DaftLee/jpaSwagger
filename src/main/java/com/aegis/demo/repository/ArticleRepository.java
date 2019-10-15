package com.aegis.demo.repository;

import com.aegis.demo.pojo.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author 李成超
 * @date 2019/10/14 10:22
 * @description TODO
 **/
public interface ArticleRepository extends ElasticsearchRepository<Article,Long> {
    /**
     * 根据标题和内容查找
     * @param title
     * @param content
     * @return
     */
    Page<Article> findByTitleOrContent(String title, String content, Pageable pageable);
    List<Article> findByContent(String content);
}
