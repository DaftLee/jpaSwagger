package com.aegis.demo.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.ManyToAny;

import javax.persistence.*;
import java.util.Set;

/**
 * @author 李成超
 * @date 2019/10/9 16:10
 * @description TODO
 **/
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("serial")
@Accessors(chain = true)
@ApiModel("学生基础信息")
public class Student {
    @Id
    @Column(name = "s_id")
    @JsonProperty("sId") // 避免转到前台大写转小写
    @ApiModelProperty(value = "学生Id",dataType = "String")
    private String sId;
    @Column(name = "s_name")
    @JsonProperty("sName")
    @ApiModelProperty(value = "学生姓名",dataType = "String")
    private String sName;
    @Column(name = "s_sex")
    @JsonProperty("sSex")
    @ApiModelProperty(value = "学生性别",dataType = "String")
    private String sSex;
    @Column(name = "s_birth")
    @JsonProperty("sBirth")
    @ApiModelProperty(value = "学生生日",dataType = "String")
    private String sBirth;


    @OneToMany(mappedBy = "student",cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    //级联保存、更新、删除、刷新;延迟加载。当删除学生，会级联删除该学生的所有分数
    //拥有mappedBy注解的实体类为关系被维护端
    //mappedBy="s_id"中的s_id是Score中的s_id属性
    private Set<Score> scores;
}
