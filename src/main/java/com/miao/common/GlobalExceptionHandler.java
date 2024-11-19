package com.miao.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author 缪广亮
 * @version 1.0
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 相同字段的异常处理方法
     * @param e
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result exceptionHandler(SQLIntegrityConstraintViolationException e) {
        log.error(e.getMessage());
        if (e.getMessage().contains("Duplicate entry")) {
            String msg = e.getMessage().split(" ")[2] + "已存在";
            return Result.error(msg);
        }
        return Result.error("新增员工失败");
    }

    /**
     * 涉及关联菜品和套餐的分类异常处理方法
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public Result exceptionHandler(CustomException e) {
        log.error(e.getMessage());
        return Result.error(e.getMessage());
    }
}
