package com.aegis.demo.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @author 李成超
 * @date 2019/10/9 15:55
 * @description TODO
 **/
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("serial")
@Accessors(chain = true)
@ApiModel("学生成绩")
public class Score implements Serializable {

    @Id
    @Column(name = "id")
    @JsonProperty("id")
    @ApiModelProperty(value = "主键",dataType = "String")
    private String id;

    @Column(name = "s_score")
    @JsonProperty("sScore")
    @ApiModelProperty(value = "分数",dataType = "int")
    private int sScore;

    @Column(name = "c_id")
    @JsonProperty("cId")
    @ApiModelProperty(value = "课程ID",dataType = "String")
    private String cId;

//    @Column(name = "s_id")
//    @JsonProperty("sId")
//    @ApiModelProperty(value = "学生ID",dataType = "String")
//    private String sId;
    @JsonIgnore
    @ManyToOne(cascade={CascadeType.MERGE,CascadeType.REFRESH},optional=false)//可选属性optional=false,表示学生不能为空。删除分数，不影响学生
    @JoinColumn(name="s_id",nullable=true) //设置在score表中的关联字段(外键)
    private Student student;
}
