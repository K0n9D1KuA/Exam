package com.apxy.courseSystem.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author apxy
 * @email 3179735066@qq.com
 * @date 2022-08-17 01:48:02
 */
@Data
@TableName("student_papar")
public class StudentPaparEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Integer id;
	/**
	 * 学生id
	 */
	private Long studentId;
	/**
	 * 试卷id
	 */
	private Long paperId;

}
