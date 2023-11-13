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
 * @Description: Third-party configuration tables
 * @Author: jeecg-boot
 * @Date:   2023-02-03
 * @Version: V1.0
 */
@Data
@TableName("sys_third_app_config")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_third_app_config对象", description="第三方配置表")
public class SysThirdAppConfig {

    /**Numbering*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "Numbering")
    private String id;

    /**Tenant ID*/
    @Excel(name = "Tenant ID", width = 15)
    @ApiModelProperty(value = "Tenant ID")
    private Integer tenantId;

    /**DingTalk/WeCom third-party enterprise application logo*/
    @Excel(name = "DingTalk/WeCom third-party enterprise application logo", width = 15)
    @ApiModelProperty(value = "DingTalk/WeCom third-party enterprise application logo")
    private String agentId;

    /**DingTalk/WeChat App ID*/
    @Excel(name = "DingTalk/WeChat App ID", width = 15)
    @ApiModelProperty(value = "DingTalk/WeChat App ID")
    private String clientId;

    /**The key corresponding to the DingTalk/WeCom application ID*/
    @Excel(name = "The key corresponding to the DingTalk/WeCom application ID", width = 15)
    @ApiModelProperty(value = "The key corresponding to the DingTalk/WeCom application ID")
    private String clientSecret;

    /**WeCom's self-built application secret*/
    @Excel(name = "WeCom's self-built application secret", width = 15)
    @ApiModelProperty(value = "WeCom's self-built application secret")
    private String agentAppSecret;

    /**Third-party categories (dingtalk DINGTALK wechat_enterprise WeCom)*/
    @Excel(name = "Third-party categories (dingtalk 钉钉 wechat_enterprise WeCom)", width = 15)
    @ApiModelProperty(value = "Third-party categories (dingtalk 钉钉 wechat_enterprise WeCom)")
    private String thirdType;

    /**Enabled or not (0-no, 1-yes)*/
    @Excel(name = "Enabled or not (0-no, 1-yes)", width = 15)
    @ApiModelProperty(value = "Enabled or not (0-no, 1-yes)")
    private Integer status;

    /**Date of creation*/
    @Excel(name = "Date of creation", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**Date modified*/
    @Excel(name = "Date modified", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
