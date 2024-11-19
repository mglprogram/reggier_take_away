package com.miao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miao.mapper.EmployeeMapper;
import com.miao.pojo.Employee;
import com.miao.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author 缪广亮
 * @version 1.0
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
