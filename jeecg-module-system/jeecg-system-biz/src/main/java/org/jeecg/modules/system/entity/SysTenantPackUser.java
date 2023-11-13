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

import java.io.Serializable;

/**
 * @Description: Tenant package user relationship table
 * @Author: jeecg-boot
 * @Date:   2023-02-16
 * @Version: V1.0
 */
@Data
@TableName("sys_tenant_pack_user")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sys_tenant_pack_userOBJECT", description="Tenant package user relationship table")
public class SysTenantPackUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /**id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private java.lang.String id;
    /**Tenant package ID*/
    @Excel(name = "Tenant package ID", width = 15)
    @ApiModelProperty(value = "Tenant package ID")
    private java.lang.String packId;
    /**User ID*/
    @Excel(name = "User ID", width = 15)
    @ApiModelProperty(value = "User ID")
    private java.lang.String userId;
    /**Tenant ID*/
    @Excel(name = "Tenant ID", width = 15)
    @ApiModelProperty(value = "Tenant ID")
    private java.lang.Integer tenantId;
    /**Created by*/
    @ApiModelProperty(value = "Created by")
    private java.lang.String createBy;
    /**Creation time*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "Creation time")
    private java.util.Date createTime;
    /**UPDATER*/
    @ApiModelProperty(value = "UPDATER")
    private java.lang.String updateBy;
    /**UPDATED*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "UPDATED")
    private java.util.Date updateTime;

    private transient String realname;

    private transient String packName;

    private transient String packCode;

    /**
     * State (1 NORMAL 2 DIMISSION 3 To be reviewed 4 REFUSE 5 Invitation to join)
     */
    private Integer status;

    public SysTenantPackUser(){
        
    }
    public SysTenantPackUser(Integer tenantId, String packId, String userId) {
        this.packId = packId;
        this.userId = userId;
        this.tenantId = tenantId;
        this.status = 1;
    }

    public SysTenantPackUser(SysTenantPackUser param, String userId, String realname) {
        this.userId = userId;
        this.realname = realname;
        this.packId = param.getPackId();
        this.tenantId = param.getTenantId();
        this.packName = param.getPackName();
        this.status = 1;
    }
}
