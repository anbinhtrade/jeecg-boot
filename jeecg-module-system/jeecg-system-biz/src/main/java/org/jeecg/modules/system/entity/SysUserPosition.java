package org.jeecg.modules.system.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: User Position Relationship Table
 * @Author: jeecg-boot
 * @Date:   2023-02-14
 * @Version: V1.0
 */
@ApiModel(value="sys_user_positionOBJECT", description="User Position Relationship Table")
@Data
@TableName("sys_user_position")
public class SysUserPosition implements Serializable {
    private static final long serialVersionUID = 1L;

	/**Primary key*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "Primary key")
    private String id;
	/**User ID*/
	@Excel(name = "User ID", width = 15)
    @ApiModelProperty(value = "User ID")
    private String userId;
	/**Job ID*/
    @ApiModelProperty(value = "Job ID")
    private String positionId;
	/**Created by*/
    @ApiModelProperty(value = "Created by")
    private String createBy;
	/**Creation time*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "Creation time")
    private Date createTime;
	/**Modified by*/
    @ApiModelProperty(value = "Modified by")
    private String updateBy;
	/**Modify time*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "Modify time")
    private Date updateTime;
}
