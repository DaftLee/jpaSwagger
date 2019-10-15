package com.aegis.demo.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author 李成超
 * @date 2019/10/14 10:12
 * @description TODO
 **/
@Document(indexName = "article")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("serial")
@Accessors(chain = true)
@ApiModel("文章")
public class Article implements Serializable {

    @Id
    @Field(type = FieldType.Long,store = true)
    @ApiModelProperty(value = "主键",dataType = "long")
    private Long id;
    @Field(type = FieldType.Text,store = true,analyzer = "ik_smart",searchAnalyzer = "ik_smart")
    @ApiModelProperty(value = "标题",dataType = "String")
    private String title;
    @Field(type = FieldType.Text,store = true,analyzer = "ik_smart",searchAnalyzer = "ik_smart")
    @ApiModelProperty(value = "内容",dataType = "String")
    private String content;

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
