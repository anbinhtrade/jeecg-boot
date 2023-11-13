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
 * @Description: Job Title
 * @Author: jeecg-boot
 * @Date: 2019-09-19
 * @Version: V1.0
 */
@Data
@TableName("sys_position")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "sys_position对象", description = "职务表")
public class SysPosition {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private java.lang.String id;
    /**
     * Job title code
     */
    @Excel(name = "Job title code", width = 15)
    @ApiModelProperty(value = "Job title code")
    private java.lang.String code;
    /**
     * Job title
     */
    @Excel(name = "Job title", width = 15)
    @ApiModelProperty(value = "Job title")
    private java.lang.String name;
    /**
     * RANK
     */
    @Excel(name = "RANK", width = 15,dicCode ="position_rank")
    @ApiModelProperty(value = "RANK")
    @Dict(dicCode = "position_rank")
    private java.lang.String postRank;
    /**
     * Company ID
     */
    @ApiModelProperty(value = "Company ID")
    private java.lang.String companyId;
    /**
     * Created by
     */
    @ApiModelProperty(value = "Created by")
    private java.lang.String createBy;
    /**
     * Creation time
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Creation time")
    private java.util.Date createTime;
    /**
     * Modified by
     */
    @ApiModelProperty(value = "Modified by")
    private java.lang.String updateBy;
    /**
     * Modify time
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Modify time")
    private java.util.Date updateTime;
    /**
     * Organization code
     */
    @ApiModelProperty(value = "Organization code")
    private java.lang.String sysOrgCode;

    /**Tenant ID*/
    @ApiModelProperty(value = "Tenant ID")
    private java.lang.Integer tenantId;
}
