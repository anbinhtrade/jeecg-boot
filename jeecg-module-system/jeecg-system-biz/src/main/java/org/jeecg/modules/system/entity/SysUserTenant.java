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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: sys_user_tenant_relation
 * @Author: jeecg-boot
 * @Date:   2022-12-23
 * @Version: V1.0
 */
@Data
@TableName("sys_user_tenant")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sys_user_tenantOBJECT", description="sys_user_tenant")
public class SysUserTenant implements Serializable {
    private static final long serialVersionUID = 1L;

	/**Primary key ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "Primary key ID")
    private String id;
	/**User ID*/
	@Excel(name = "User ID", width = 15)
    @ApiModelProperty(value = "User ID")
    private String userId;
	/**Tenant ID*/
	@Excel(name = "Tenant ID", width = 15)
    @ApiModelProperty(value = "Tenant ID")
    private Integer tenantId;
	/**State (1 NORMAL 2 FREEZE 3 To be reviewed 4 Refusal)*/
	@Excel(name = "State (1 NORMAL 2 FREEZE 3 To be reviewed 4 Refusal)", width = 15)
    @ApiModelProperty(value = "State (1 NORMAL 2 FREEZE 3 To be reviewed 4 Refusal)")
    private String status;
	/**Creator login name*/
    @ApiModelProperty(value = "Creator login name")
    private String createBy;
	/**Date of creation*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "Date of creation")
    private Date createTime;
	/**The login name of the person who updated it*/
    @ApiModelProperty(value = "The login name of the person who updated it")
    private String updateBy;
	/**Updated date*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "Updated date")
    private Date updateTime;
}