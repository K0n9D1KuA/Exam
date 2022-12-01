package com.apxy.courseSystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 *
 * @author apxy
 * @email 3179735066@qq.com
 * @date 2022-08-17 01:48:02
 */
@Data
@TableName("paper")
public class PaperEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;
	/**
	 * 试卷名称
	 */
	private String paperName;
	/**
	 * 开始时间
	 */

	private Date beginTime;
	/**
	 * 结束时间
	 */

	private Date endTime;
	/**
	 * 总共持续时间 以分钟为单位
	 */
	private Integer totalTime;
	/**
	 * 总分
	 */
	private Integer totalScore;
	/**
	 * 关联老师id
	 */
	private Long teacherId;

}
