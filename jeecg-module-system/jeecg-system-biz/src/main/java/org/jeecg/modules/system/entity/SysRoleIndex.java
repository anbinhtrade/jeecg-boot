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
 * @Description: Character home page configuration
 * @Author: liusq
 * @Date:   2022-03-25
 * @Version: V1.0
 */
@Data
@TableName("sys_role_index")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_role_indexOBJECT", description="Character home page configuration")
public class SysRoleIndex {
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private java.lang.String id;
	/**Role coding*/
	@Excel(name = "Role coding", width = 15)
    @ApiModelProperty(value = "Role coding")
	private java.lang.String roleCode;
	/**Routing address*/
	@Excel(name = "Routing address", width = 15)
    @ApiModelProperty(value = "Routing address")
	private java.lang.String url;
	/**Routing address*/
	@Excel(name = "Routing address", width = 15)
    @ApiModelProperty(value = "SUBASSEMBLY")
	private java.lang.String component;
	/**
	 * Whether to route the menu: 0: No  1: Yes (default value 1)
	 */
	@Excel(name = "Whether to route the menu", width = 15)
	@ApiModelProperty(value = "Whether to route the menu")
	@TableField(value="is_route")
	private boolean route;
	/**PRIORITY*/
	@Excel(name = "PRIORITY", width = 15)
    @ApiModelProperty(value = "PRIORITY")
	private java.lang.Integer priority;
	/**Routing address*/
	@Excel(name = "STATE", width = 15)
	@ApiModelProperty(value = "STATE")
	private java.lang.String status;
	/**Creator login name*/
	@Excel(name = "Creator login name", width = 15)
    @ApiModelProperty(value = "Creator login name")
	private java.lang.String createBy;
	/**Date of creation*/
	@Excel(name = "Date of creation", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Date of creation")
	private java.util.Date createTime;
	/**The login name of the person who updated it*/
	@Excel(name = "The login name of the person who updated it", width = 15)
    @ApiModelProperty(value = "The login name of the person who updated it")
	private java.lang.String updateBy;
	/**Updated date*/
	@Excel(name = "Updated date", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Updated date")
	private java.util.Date updateTime;
	/**AFFILIATION*/
	@Excel(name = "AFFILIATION", width = 15)
    @ApiModelProperty(value = "AFFILIATION")
	private java.lang.String sysOrgCode;


	public SysRoleIndex() {

	}
	public SysRoleIndex(String componentUrl){
		this.component = componentUrl;
	}
}
