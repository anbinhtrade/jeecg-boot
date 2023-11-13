package org.jeecg.modules.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: Tenant packages
 * @Author: jeecg-boot
 * @Date:   2022-12-31
 * @Version: V1.0
 */
@Data
@TableName("sys_tenant_pack")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sys_tenant_packOBJECT", description="Tenant packages")
public class SysTenantPack implements Serializable {
    private static final long serialVersionUID = 1L;

	/**Primary key ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "Primary key ID")
    private String id;
	/**Tenant ID*/
	@Excel(name = "Tenant ID", width = 15)
    @ApiModelProperty(value = "Tenant ID")
    private Integer tenantId;
	/**The package name*/
	@Excel(name = "The package name", width = 15)
    @ApiModelProperty(value = "The package name")
    private String packName;
	/**Open (0.) Not turned on 1 on)*/
	@Excel(name = "Open (0.) Not turned on 1 on)", width = 15)
    @ApiModelProperty(value = "Open (0.) Not turned on 1 on)")
    private String status;
	/**REMARK*/
	@Excel(name = "REMARK", width = 15)
    @ApiModelProperty(value = "REMARK")
    private String remarks;
	/**Created by*/
    @ApiModelProperty(value = "Created by")
    private String createBy;
	/**Creation time*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "Creation time")
    private Date createTime;
	/**UPDATER*/
    @ApiModelProperty(value = "UPDATER")
    private String updateBy;
	/**UPDATED*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "UPDATED")
    private Date updateTime;
    /**Package Type (default.) Default package custom Custom product packages)*/
    @Excel(name = "The type of package", width = 15)
    @ApiModelProperty(value = "The type of package")
	private String packType;
    
    /**Menu ID The temporary field is used to add the edit menu ID passing*/
    @TableField(exist = false)
    private String permissionIds;
    
    
    /**
     * ENCODE
     */
    private String packCode;
    
    public SysTenantPack(){
        
    }

    public SysTenantPack(Integer tenantId, String packName, String packCode){
        this.tenantId = tenantId;
        this.packCode = packCode;
        this.packName = packName;
        this.status = "1";
    }
}
