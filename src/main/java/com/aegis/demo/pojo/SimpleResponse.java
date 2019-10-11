package com.aegis.demo.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author 李成超
 * @date 2019/10/9 17:21
 * @description TODO
 **/
@ApiModel("统一返回类")
public class SimpleResponse<T> implements Serializable {

    /**
     * 返回 内容 （json格式）
     */

    @ApiModelProperty(value = "返回状态",dataType = "String",example = "0",notes = "0为成功，其余为失败")
    private String code;

    @ApiModelProperty(value = "返回描述",dataType = "String")
    private String msg;

    @ApiModelProperty(value = "返回数据",dataType = "T")
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
