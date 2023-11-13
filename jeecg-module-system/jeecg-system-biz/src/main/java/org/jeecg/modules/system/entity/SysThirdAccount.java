package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 第三方登录账号表
 * @Author: jeecg-boot
 * @Date:   2020-11-17
 * @Version: V1.0
 */
@Data
@TableName("sys_third_account")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_third_accountOBJECT", description="Third-party login account form")
public class SysThirdAccount {
 
	/**Numbering*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "Numbering")
	private java.lang.String id;
	/**Third-party login ID*/
	@Excel(name = "Third-party login ID", width = 15)
	@ApiModelProperty(value = "Third-party login ID")
	private java.lang.String sysUserId;
	/**Sign-in source*/
	@Excel(name = "Sign-in source", width = 15)
	@ApiModelProperty(value = "Sign-in source")
	private java.lang.String thirdType;
	/**Avatar*/
	@Excel(name = "Avatar", width = 15)
	@ApiModelProperty(value = "Avatar")
	private java.lang.String avatar;
	/**Status (1-Normal, 2-Frozen)*/
	@Excel(name = "Status (1-Normal, 2-Frozen)", width = 15)
	@ApiModelProperty(value = "Status (1-Normal, 2-Frozen)")
	private java.lang.Integer status;
	/**Deleted Status (0 - Normal, 1 - Deleted)*/
	@Excel(name = "Deleted Status (0 - Normal, 1 - Deleted)", width = 15)
	@ApiModelProperty(value = "Deleted Status (0 - Normal, 1 - Deleted)")
	private java.lang.Integer delFlag;
	/**Real name*/
	@Excel(name = "Real name", width = 15)
	@ApiModelProperty(value = "Real name")
	private java.lang.String realname;
	/**Third-Party User UUIDs*/
	@Excel(name = "Third-Party User UUIDs", width = 15)
	@ApiModelProperty(value = "Third-Party User UUIDs")
	private java.lang.String thirdUserUuid;
	/**Third-Party User Accounts*/
	@Excel(name = "Third-Party User Accounts", width = 15)
	@ApiModelProperty(value = "Third-Party User Accounts")
	private java.lang.String thirdUserId;
    /**Created by*/
    @Excel(name = "Created by", width = 15)
    private java.lang.String createBy;
    /**Date of creation*/
    @Excel(name = "Date of creation", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private java.util.Date createTime;
    /**Modified by*/
    @Excel(name = "Modified by", width = 15)
    private java.lang.String updateBy;
    /**Date modified*/
    @Excel(name = "Date modified", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private java.util.Date updateTime;

	/**Tenant ID*/
	private java.lang.Integer tenantId;
}
