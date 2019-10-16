package com.aegis.demo;

import com.aegis.demo.pojo.Article;
import com.aegis.demo.repository.ArticleRepository;
import com.aegis.demo.repository.StudentRepository;
import org.apache.lucene.queryparser.xml.QueryBuilderFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.hibernate.cfg.annotations.QueryBinder;
import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Test
    public void contextLoads() {
        for (int i = 0; i < 20; i++) {
            System.out.println(new Random().nextInt(899)+100);
        }
    }

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    ArticleRepository articleRepository;

    @Test
    public void addDocument() {
        for (long i = 0; i < 20; i++) {
            Article article = new Article();
            article.setId(i).setTitle("习近平结束对尼泊尔国事访问回到北京"+i).setContent("新华社北京10月13日电 10月13日晚，国家主席习近平在结束同印度总理莫迪第二次非正式会晤和对尼泊尔国事访问后，回到北京。"+i);
            articleRepository.save(article);
        }
        for (long i = 20; i < 40; i++) {
            Article article = new Article();
            article.setId(i).setTitle("韩国瑜首就两岸政策下战书 蔡英文猖狂回应"+i).setContent("据中国台湾网报道，蔡英文12日上午宣称，台湾不缺电，是一个不必争辩的事实。对此，韩国瑜深夜在社交媒体怒怼。"+i);
            articleRepository.save(article);
        }
        for (long i =40; i < 60; i++) {
            Article article = new Article();
            article.setId(i).setTitle("遭特朗普\"背叛\"后 美国的这个\"马前卒\"祭狠招报复"+i).setContent("据报道，特朗普与土耳其总统埃尔多安6日通电话，随后白宫宣布，土耳其即将向叙利亚北部库尔德武装控制区发起军事行动，美军将撤出这一地区。"+i);
            articleRepository.save(article);
        }
    }

    @Test
    public void createIndex() {
        elasticsearchTemplate.createIndex(Article.class);
    }

    @Test
    public void query() {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("title","对尼");
//        List<Article> l = articleRepository.findByContent("习近平同志");
        Iterable<Article> search = articleRepository.search(queryBuilder);
        search.forEach(article -> {
            System.out.println(article);
        });
    }

    @Test
    public void generatePassword(){
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        //加密所需的salt(盐)
        textEncryptor.setPassword("AegisData");
        //要加密的数据（数据库的用户名或密码）
        String username = textEncryptor.encrypt("psg");
        String password = textEncryptor.encrypt("psg");
        System.out.println("username:"+username);
        System.out.println("password:"+password);
    }

}
