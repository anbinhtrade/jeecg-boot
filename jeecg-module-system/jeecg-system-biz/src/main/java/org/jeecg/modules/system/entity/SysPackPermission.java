package org.jeecg.modules.system.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: Package menu relationship table
 * @Author: jeecg-boot
 * @Date:   2022-12-31
 * @Version: V1.0
 */
@Data
@TableName("sys_tenant_pack_perms")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sys_tenant_pack_permsOBJECT", description="Package menu relationship table")
public class SysPackPermission implements Serializable {
    private static final long serialVersionUID = 1L;

	/**Primary key number*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "Primary key number")
    private String id;
	/**The name of the tenant package*/
	@Excel(name = "The name of the tenant package", width = 15)
    @ApiModelProperty(value = "The name of the tenant package")
    private String packId;
	/**Menu ID*/
	@Excel(name = "Menu ID", width = 15)
    @ApiModelProperty(value = "Menu ID")
    private String permissionId;
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
}
