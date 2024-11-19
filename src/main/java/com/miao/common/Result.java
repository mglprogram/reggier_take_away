package com.miao.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果，服务端响应的数据最终都会封装成此对象
 *
 * @param <T>
 */
@Data
@ApiModel(value = "Result对象", description = "通用返回结果")
public class Result<T> implements Serializable {
    @ApiModelProperty("编码")
    private Integer code; //编码：1成功，0和其它数字为失败
    @ApiModelProperty("错误信息")
    private String msg; //错误信息
    @ApiModelProperty("数据")
    private T data; //数据
    @ApiModelProperty("动态数据")
    private Map map = new HashMap(); //动态数据

    public static <T> Result<T> success(T object) {
        Result<T> r = new Result<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> Result<T> error(String msg) {
        Result r = new Result();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public Result<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
