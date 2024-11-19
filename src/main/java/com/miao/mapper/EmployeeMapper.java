package com.miao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miao.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 缪广亮
 * @version 1.0
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
