package org.jeecg.modules.system.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: User Proxy Settings
 * @Author: jeecg-boot
 * @Date:  2019-04-17
 * @Version: V1.0
 */
@Data
@TableName("sys_user_agent")
public class SysUserAgent implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**Serial Number*/
	@TableId(type = IdType.ASSIGN_ID)
	private java.lang.String id;
	/**Username*/
	@Excel(name = "Username", width = 15)
	private java.lang.String userName;
	/**Proxy username*/
	@Excel(name = "Proxy username", width = 15)
	private java.lang.String agentUserName;
	/**Agent start time*/
	@Excel(name = "Agent start time", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date startTime;
	/**Agent end time*/
	@Excel(name = "Agent end time", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date endTime;
	/**Status 0 is invalid and 1 is valid*/
	@Excel(name = "Status 0 is invalid and 1 is valid", width = 15)
	private java.lang.String status;
	/**The name of the creator*/
	@Excel(name = "The name of the creator", width = 15)
	private java.lang.String createName;
	/**Creator login name*/
	@Excel(name = "Creator login name", width = 15)
	private java.lang.String createBy;
	/**Date of creation*/
	@Excel(name = "Date of creation", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date createTime;
	/**The name of the person who updated it*/
	@Excel(name = "The name of the person who updated it", width = 15)
	private java.lang.String updateName;
	/**The login name of the person who updated it*/
	@Excel(name = "The login name of the person who updated it", width = 15)
	private java.lang.String updateBy;
	/**Updated date*/
	@Excel(name = "Updated date", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date updateTime;
	/**Affiliation*/
	@Excel(name = "Affiliation", width = 15)
	private java.lang.String sysOrgCode;
	/**Affiliation*/
	@Excel(name = "Affiliation", width = 15)
	private java.lang.String sysCompanyCode;
}
