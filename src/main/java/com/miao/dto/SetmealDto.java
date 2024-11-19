package com.miao.dto;

import com.miao.pojo.Setmeal;
import com.miao.pojo.SetmealDish;
import lombok.Data;

import java.util.List;

/**
 * @author 缪广亮
 * @version 1.0
 */
@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
