package org.jeecg.modules.system.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: Department Permission Table
 * @Author: jeecg-boot
 * @Date:   2020-02-11
 * @Version: V1.0
 */
@Data
@TableName("sys_depart_permission")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_depart_permissionObject", description="Department Permission Table")
public class SysDepartPermission {
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private java.lang.String id;
	/**Department ID*/
	@Excel(name = "Department ID", width = 15)
    @ApiModelProperty(value = "Department ID")
	private java.lang.String departId;
	/**Permission ID*/
	@Excel(name = "Permission ID", width = 15)
    @ApiModelProperty(value = "Permission ID")
	private java.lang.String permissionId;
	/**Data rule ID*/
	@ApiModelProperty(value = "Data rule ID")
	private java.lang.String dataRuleIds;

	public SysDepartPermission() {

	}

	public SysDepartPermission(String departId, String permissionId) {
		this.departId = departId;
		this.permissionId = permissionId;
	}
}
