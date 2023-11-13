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
 * @Description: Filling rules
 * @Author: jeecg-boot
 * @Date: 2019-11-07
 * @Version: V1.0
 */
@Data
@TableName("sys_fill_rule")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "sys_fill_rule对象", description = "Filling rules")
public class SysFillRule {

    /**
     * Primary key ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
    /**
     * The name of the rule
     */
    @Excel(name = "The name of the rule", width = 15)
    @ApiModelProperty(value = "The name of the rule")
    private java.lang.String ruleName;
    /**
     * Rule Code
     */
    @Excel(name = "Rule Code", width = 15)
    @ApiModelProperty(value = "Rule Code")
    private java.lang.String ruleCode;
    /**
     * Rule implementation classes
     */
    @Excel(name = "Rule implementation classes", width = 15)
    @ApiModelProperty(value = "Rule implementation classes")
    private java.lang.String ruleClass;
    /**
     * Rule parameters
     */
    @Excel(name = "Rule parameters", width = 15)
    @ApiModelProperty(value = "Rule parameters")
    private java.lang.String ruleParams;
    /**
     * Modified by
     */
    @Excel(name = "Modified by", width = 15)
    @ApiModelProperty(value = "Modified by")
    private java.lang.String updateBy;
    /**
     * Modify time
     */
    @Excel(name = "Modify time", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Modify time")
    private java.util.Date updateTime;
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
}
