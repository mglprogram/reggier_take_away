package com.miao;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Collections;

/**
 * @author 缪广亮
 * @version 1.0
 */
public class MiaoCode {
    public static void main(String[] args) {
        // 项目路径
        String projectPath = System.getProperty("user.dir");

        // 使用FastAutoGenerator快速生成
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/reggie?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8",
                        "root",
                        "root")
                // 全局配置
                .globalConfig(builder -> {
                    builder.author("缪广亮") // 设置作者
                            .enableSwagger() // 开启swagger模式
//                            .fileOverride() // 覆盖已生成文件
                            .outputDir(projectPath + "/src/main/java") // 指定输出目录
                            .dateType(DateType.TIME_PACK) // 时间策略 设置时间字段类型为 LocalDateTime
                            .commentDate("yyyy-MM-dd") // 注释日期
                            .disableOpenDir(); // 禁止打开输出目录
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent("com.miao") // 设置父包名
                            .entity("pojo") // 实体类包名
                            .service("service") // service包名
                            .serviceImpl("service.impl") // serviceImpl包名
                            .mapper("mapper") // mapper包名
                            .controller("controller") // controller包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, 
                                    projectPath + "/src/main/resources/mapper")); // 设置mapperXml生成路径
                })
                // 策略配置
                .strategyConfig(builder -> {
                    builder.addInclude("order_detail") // 设置需要生成的表名
                            // 实体策略配置
                            .entityBuilder()
                            .enableLombok() // 开启lombok
                            .enableTableFieldAnnotation() // 开启字段注解
                            .logicDeleteColumnName("is_deleted") // 逻辑删除字段
                            .naming(NamingStrategy.underline_to_camel) // 表名转驼峰命名
                            .columnNaming(NamingStrategy.underline_to_camel) // 列名转驼峰命名
                            .addTableFills(
                                    new Column("create_time", FieldFill.INSERT),
                                    new Column("update_time", FieldFill.INSERT_UPDATE),
                                    new Column("create_user",FieldFill.INSERT),
                                    new Column("update_user",FieldFill.INSERT_UPDATE)
                            ) // 添加表字段填充
                            .idType(IdType.ASSIGN_ID) // 主键策略
                            .versionColumnName("version") // 乐观锁字段名
                            // 控制器策略配置
                            .controllerBuilder()
                            .enableRestStyle() // 开启生成@RestController
                            .enableHyphenStyle() // 开启驼峰转连字符
                            // service策略配置
                            .serviceBuilder()
                            .formatServiceFileName("%sService") // 格式化service接口文件名称
                            .formatServiceImplFileName("%sServiceImpl") // 格式化service实现类文件名称
                            // mapper策略配置
                            .mapperBuilder()
                            .enableMapperAnnotation() // 开启@Mapper注解
                            .formatMapperFileName("%sMapper") // 格式化mapper文件名称
                            .formatXmlFileName("%sMapper"); // 格式化xml文件名称
                })
                .execute(); // 执行
    }
}
