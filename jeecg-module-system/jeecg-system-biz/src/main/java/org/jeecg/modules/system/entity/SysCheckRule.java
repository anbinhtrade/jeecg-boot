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

import java.util.Date;

/**
 * @Description: 编码校验规则
 * @Author: jeecg-boot
 * @Date: 2020-02-04
 * @Version: V1.0
 */
@Data
@TableName("sys_check_rule")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "sys_check_rule对象", description = "Coding verification rules")
public class SysCheckRule {

    /**
     * Primary Key Id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "Primary Key Id")
    private String id;
    /**
     * Rule name
     */
    @Excel(name = "Rule name", width = 15)
    @ApiModelProperty(value = "Rule name")
    private String ruleName;
    /**
     * Rule Code
     */
    @Excel(name = "Rule Code", width = 15)
    @ApiModelProperty(value = "Rule Code")
    private String ruleCode;
    /**
     * Rulesjson
     */
    @Excel(name = "Rulesjson", width = 15)
    @ApiModelProperty(value = "Rulesjson")
    private String ruleJson;
    /**
     * Rule description
     */
    @Excel(name = "Rule description", width = 15)
    @ApiModelProperty(value = "Rule description")
    private String ruleDescription;
    /**
     * Updater
     */
    @Excel(name = "Updater", width = 15)
    @ApiModelProperty(value = "Updater")
    private String updateBy;
    /**
     * Update time
     */
    @Excel(name = "Update time", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Update time")
    private Date updateTime;
    /**
     * Founder
     */
    @Excel(name = "Founder", width = 15)
    @ApiModelProperty(value = "Founder")
    private String createBy;
    /**
     * Creation Time
     */
    @Excel(name = "Creation Time", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Creation Time")
    private Date createTime;
}
