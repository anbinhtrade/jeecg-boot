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
 * @Description: System comment response form
 * @Author: jeecg-boot
 * @Date:   2022-07-19
 * @Version: V1.0
 */
@Data
@TableName("sys_comment")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sys_comment OBJECT", description="System comment response form")
public class SysComment implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private String id;
	/**表名*/
	@Excel(name = "Table name", width = 15)
    @ApiModelProperty(value = "Table name")
    private String tableName;
	/**数据id*/
	@Excel(name = "Data ID", width = 15)
    @ApiModelProperty(value = "Data ID")
    private String tableDataId;
	/**来源用户id*/
	@Excel(name = "Source user ID", width = 15)
    @ApiModelProperty(value = "Source user ID")
    @Dict(dictTable = "sys_user", dicCode = "id", dicText = "realname")
    private String fromUserId;
	/**发送给用户id(允许为空)*/
	@Excel(name = "Send User ID (Allowed to be empty)", width = 15)
    @ApiModelProperty(value = "Send User ID (Allowed to be empty)")
    @Dict(dictTable = "sys_user", dicCode = "id", dicText = "realname")
    private String toUserId;
	/**Comment ID (allowed to be empty, not empty, then reply)*/
	@Excel(name = "Comment ID (allowed to be empty, not empty, then reply)", width = 15)
    @ApiModelProperty(value = "Comment ID (allowed to be empty, not empty, then reply)")
    @Dict(dictTable = "sys_comment", dicCode = "id", dicText = "comment_content")
    private String commentId;
	/**Reply to the content*/
	@Excel(name = "Reply to the content", width = 15)
    @ApiModelProperty(value = "Reply to the content")
    private String commentContent;
	/**Created by*/
    @ApiModelProperty(value = "Created by")
    private String createBy;
	/**Date of creation*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Date of creation")
    private Date createTime;
	/**UPDATER*/
    @ApiModelProperty(value = "UPDATER")
    private String updateBy;
	/**Updated date*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Updated date")
    private Date updateTime;
}
