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

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: Gateway route management
 * @Author: jeecg-boot
 * @Date:   2020-05-26
 * @Version: V1.0
 */
@Data
@TableName("sys_gateway_route")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sys_gateway_route对象", description="Gateway route management")
public class SysGatewayRoute implements Serializable {
    private static final long serialVersionUID = 1L;

    /**Primary key*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "Primary key")
    private String id;

    /**routerKEy*/
    @ApiModelProperty(value = "Route ID")
    private String routerId;

    /**Service name*/
    @Excel(name = "Service name", width = 15)
    @ApiModelProperty(value = "Service name")
    private String name;

    /**Service address*/
    @Excel(name = "Service address", width = 15)
    @ApiModelProperty(value = "Service address")
    private String uri;

    /**
     * Assertion configuration
     */
    private String predicates;

    /**
     * Filtering configuration
     */
    private String filters;

    /**Whether to ignore the prefix 0-no 1 - Yes*/
    @Excel(name = "Ignore prefixes", width = 15)
    @ApiModelProperty(value = "Ignore prefixes")
    @Dict(dicCode = "yn")
    private Integer stripPrefix;

    /**Retry 0-No 1 - Yes*/
    @Excel(name = "Whether to try again", width = 15)
    @ApiModelProperty(value = "Whether to try again")
    @Dict(dicCode = "yn")
    private Integer retryable;

    /**Retained data: 0-No 1-是*/
    @Excel(name = "Retention of data", width = 15)
    @ApiModelProperty(value = "Retention of data")
    @Dict(dicCode = "yn")
    private Integer persistable;

    /**Whether it is displayed in the interface documentation: 0-No 1 - Yes*/
    @Excel(name = "This is shown in the API documentation", width = 15)
    @ApiModelProperty(value = "This is shown in the API documentation")
    @Dict(dicCode = "yn")
    private Integer showApi;

    /**STATE 1 Effective 0 invalid*/
    @Excel(name = "STATE", width = 15)
    @ApiModelProperty(value = "STATE")
    @Dict(dicCode = "yn")
    private Integer status;

    /**Created by*/
    @ApiModelProperty(value = "Created by")
    private String createBy;
    /**Date of creation*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Date of creation")
    private Date createTime;
    /*    *//**UPDATER*//*
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    *//**Updated date*//*
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
    *//**AFFILIATION*//*
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;*/
}
