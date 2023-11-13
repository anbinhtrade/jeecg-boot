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
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: Department roles
 * @Author: jeecg-boot
 * @Date:   2020-02-12
 * @Version: V1.0
 */
@Data
@TableName("sys_depart_role")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_depart_roleObject", description="Department roles")
public class SysDepartRole {
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private java.lang.String id;
	/**Department ID*/
	@Excel(name = "Department ID", width = 15)
	@ApiModelProperty(value = "Department ID")
	@Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
	private java.lang.String departId;
	/**The name of the department role*/
	@Excel(name = "The name of the department role", width = 15)
    @ApiModelProperty(value = "The name of the department role")
	private java.lang.String roleName;
	/**Department role code*/
	@Excel(name = "Department role code", width = 15)
    @ApiModelProperty(value = "Department role code")
	private java.lang.String roleCode;
	/**Description*/
	@Excel(name = "Description", width = 15)
    @ApiModelProperty(value = "Description")
	private java.lang.String description;
	/**Created by*/
	@Excel(name = "Created by", width = 15)
    @ApiModelProperty(value = "Created by")
	private java.lang.String createBy;
	/**Creation time*/
	@Excel(name = "Creation time", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Creation time")
	private java.util.Date createTime;
	/**Updater*/
	@Excel(name = "Updater", width = 15)
    @ApiModelProperty(value = "Updater")
	private java.lang.String updateBy;
	/**Updated*/
	@Excel(name = "Updated", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Updated")
	private java.util.Date updateTime;


}
