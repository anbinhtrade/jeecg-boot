package org.jeecg.modules.system.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: Department role personnel information
 * @Author: jeecg-boot
 * @Date:   2020-02-13
 * @Version: V1.0
 */
@Data
@TableName("sys_depart_role_user")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_depart_role_userOBJECT", description="Department role personnel information")
public class SysDepartRoleUser {
    
	/**Primary key ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "Primary key ID")
	private java.lang.String id;
	/**User ID*/
	@Excel(name = "User ID", width = 15)
    @ApiModelProperty(value = "User ID")
	private java.lang.String userId;
	/**Character ID*/
	@Excel(name = "Character ID", width = 15)
    @ApiModelProperty(value = "Character ID")
	private java.lang.String droleId;

	public SysDepartRoleUser() {

	}

	public SysDepartRoleUser(String userId, String droleId) {
		this.userId = userId;
		this.droleId = droleId;
	}
}
