package org.jeecg.modules.demo.test.entity;

import java.io.Serializable;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @Description: Process testing
 * @Author: jeecg-boot
 * @Date:   2019-05-14
 * @Version: V1.0
 */
@Data
@TableName("joa_demo")
public class JoaDemo implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
	private java.lang.String id;
	/**Leave of absence*/
	@Excel(name = "Leave of absence", width = 15)
	private java.lang.String name;
	/**Number of days off leave*/
	@Excel(name = "Number of days off leave", width = 15)
	private java.lang.Integer days;
	/**Start time*/
	@Excel(name = "Start time", width = 20, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	private java.util.Date beginDate;
	/**The end time of the leave of absence*/
	@Excel(name = "The end time of the leave of absence", width = 20, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	private java.util.Date endDate;
	/**Reason for leave*/
	@Excel(name = "Reason for leave", width = 15)
	private java.lang.String reason;
	/**Process status*/
	@Excel(name = "Process status", width = 15)
	private java.lang.String bpmStatus;
	/**The ID of the person who created it*/
	@Excel(name = "The ID of the person who created it", width = 15)
	private java.lang.String createBy;
	/**Creation time*/
	@Excel(name = "Creation time", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date createTime;
	/**Modify time*/
	@Excel(name = "Modify time", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date updateTime;
	/**Modify the ID of the person*/
	@Excel(name = "Modify the ID of the person", width = 15)
	private java.lang.String updateBy;
}
