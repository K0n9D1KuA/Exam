package com.apxy.courseSystem.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;


@Data
public class Member {
    // 注意 如果Java类中的属性字段的顺序和excel中表头字段顺序一致，那么可以不写  @ExcelProperty(value = "用户名")注解
    @ExcelProperty(value = "用户名")
    private String userName;
    @ExcelProperty(value = "密码")
    private String password;
    @ExcelProperty(value = "性别")
    private String sex;
    @ExcelProperty(value = "姓名")
    private String memberName;
    @ExcelProperty(value = "专业")
    private String profession;
    @ExcelProperty(value = "身份")
    private String role;
    @ExcelProperty(value = "科任老师")
    private String teacher;
    //科任老师
}
