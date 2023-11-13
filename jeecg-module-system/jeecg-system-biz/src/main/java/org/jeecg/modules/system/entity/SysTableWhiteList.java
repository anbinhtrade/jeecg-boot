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
 * @Description: 系统表白名单
 * @Author: jeecg-boot
 * @Date: 2023-09-12
 * @Version: V1.0
 */
@Data
@TableName("sys_table_white_list")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "sys_table_white_listOBJECT", description = "The system whitelist")
public class SysTableWhiteList {

    /**
     * Primary key ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "Primary key ID")
    private java.lang.String id;
    /**
     * Allowed table names
     */
    @Excel(name = "Allowed table names", width = 15)
    @ApiModelProperty(value = "Allowed table names")
    private java.lang.String tableName;
    /**
     * Allowed field names, multiple separated by a comma
     */
    @Excel(name = "Allowed field names", width = 15)
    @ApiModelProperty(value = "Allowed field names")
    private java.lang.String fieldName;
    /**
     * Status, 1=Enabled, 0=Disabled
     */
    @Excel(name = "STATE", width = 15)
    @ApiModelProperty(value = "STATE")
    private java.lang.String status;
    /**
     * Created by
     */
    @Excel(name = "Created by", width = 15)
    @ApiModelProperty(value = "Created by")
    private java.lang.String createBy;
    /**
     * Creation time
     */
    @Excel(name = "Creation time", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Creation time")
    private java.util.Date createTime;
    /**
     * UPDATER
     */
    @Excel(name = "UPDATER", width = 15)
    @ApiModelProperty(value = "UPDATER")
    private java.lang.String updateBy;
    /**
     * UPDATED
     */
    @Excel(name = "UPDATED", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "UPDATED")
    private java.util.Date updateTime;
}
