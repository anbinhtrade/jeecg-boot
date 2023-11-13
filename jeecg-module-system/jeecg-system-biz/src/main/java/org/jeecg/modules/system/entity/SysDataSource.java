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
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: Multi-data source management
 * @Author: jeecg-boot
 * @Date: 2019-12-25
 * @Version: V1.0
 */
@Data
@TableName("sys_data_source")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Sys Data Source Object", description = "Multi-data source management")
public class SysDataSource {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private java.lang.String id;
    /**
     * Data source encoding
     */
    @Excel(name = "Data source encoding", width = 15)
    @ApiModelProperty(value = "Data source encoding")
    private java.lang.String code;
    /**
     * Data source name
     */
    @Excel(name = "Data source name", width = 15)
    @ApiModelProperty(value = "Data source name")
    private java.lang.String name;
    /**
     * Bewrite
     */
    @Excel(name = "Remark", width = 15)
    @ApiModelProperty(value = "Remark")
    private java.lang.String remark;
    /**
     * Database type
     */
    @Dict(dicCode = "database_type")
    @Excel(name = "Database type", width = 15, dicCode = "database_type")
    @ApiModelProperty(value = "Database type")
    private java.lang.String dbType;
    /**
     * Driver class
     */
    @Excel(name = "Driver class", width = 15)
    @ApiModelProperty(value = "Driver class")
    private java.lang.String dbDriver;
    /**
     * Data source address
     */
    @Excel(name = "Data source address", width = 15)
    @ApiModelProperty(value = "Data source address")
    private java.lang.String dbUrl;
    /**
     * Name database
     */
    @Excel(name = "Name database", width = 15)
    @ApiModelProperty(value = "Name database")
    private java.lang.String dbName;
    /**
     * User Name
     */
    @Excel(name = "User Name", width = 15)
    @ApiModelProperty(value = "User Name")
    private java.lang.String dbUsername;
    /**
     * Cipher Code
     */
    @Excel(name = "Cipher Code", width = 15)
    @ApiModelProperty(value = "Cipher Code")
    private java.lang.String dbPassword;
    /**
     * Founder
     */
    @ApiModelProperty(value = "Founder")
    private java.lang.String createBy;
    /**
     * Creation date
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Creation date")
    private java.util.Date createTime;
    /**
     * Updater
     */
    @ApiModelProperty(value = "Updater")
    private java.lang.String updateBy;
    /**
     * Updated
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Updated")
    private java.util.Date updateTime;
    /**
     * Department
     */
    @Excel(name = "Department", width = 15)
    @ApiModelProperty(value = "Department")
    private java.lang.String sysOrgCode;

    /**Tenant ID*/
    @ApiModelProperty(value = "Tenant ID")
    private java.lang.Integer tenantId;
}
