package com.miao.common;

/**
 * @author 缪广亮
 * @version 1.0
 * desc:自定义业务异常
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
