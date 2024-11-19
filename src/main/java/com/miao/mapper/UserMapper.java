package com.miao.mapper;

import com.miao.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户信息 Mapper 接口
 * </p>
 *
 * @author 缪广亮
 * @since 2024-11-16
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
