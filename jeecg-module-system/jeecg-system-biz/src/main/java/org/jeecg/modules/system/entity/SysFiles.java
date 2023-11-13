package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
import java.util.Date;

/**
 * @Description: 知识库-文档管理
 * @Author: jeecg-boot
 * @Date:   2022-07-21
 * @Version: V1.0
 */
@Data
@TableName("sys_files")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_filesOBJECT", description="Knowledge Base - Document Management")
public class SysFiles {
    
	/**Primary key ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "Primary key ID")
	private String id;
	/**The name of the file*/
	@Excel(name = "The name of the file", width = 15)
    @ApiModelProperty(value = "The name of the file")
	private String fileName;
	/**File address*/
	@Excel(name = "File address", width = 15)
    @ApiModelProperty(value = "File address")
	private String url;
	/**Creator login name*/
	@Excel(name = "Creator login name", width = 15)
    @Dict(dicCode = "username",dicText = "realname",dictTable = "sys_user")
    @ApiModelProperty(value = "Creator login name")
	private String createBy;
	/**Date of creation*/
	@Excel(name = "Date of creation", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Date of creation")
	private Date createTime;
	/**The login name of the person who updated it*/
	@Excel(name = "The login name of the person who updated it", width = 15)
    @ApiModelProperty(value = "The login name of the person who updated it")
	private String updateBy;
	/**Updated date*/
	@Excel(name = "Updated date", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Updated date")
	private Date updateTime;
	/**Document type (folder: folder excel:excel doc:word pp:ppt image:IMAGE  archive:Additional Documentation video:VIDEO）*/
	@Excel(name = "Document type（folder:FOLDER excel:excel doc:word pp:ppt image:IMAGE  archive:Additional Documentation video:VIDEO）", width = 15)
    @ApiModelProperty(value = "Document type（folder:FOLDER excel:excel doc:word pp:ppt image:IMAGE  archive:Additional Documentation video:视频）")
	private String fileType;
	/**File Upload Type (temp/Local Upload (Temp) manage/KNOWLEDGE BASE comment)*/
	@Excel(name = "File Upload Type (temp/Local Upload (Temp) manage/KNOWLEDGE BASE common(Universal uploads))", width = 15)
    @ApiModelProperty(value = "File Upload Type (temp/Local Upload (Temp) manage/KNOWLEDGE BASE)")
	private String storeType;
	/**Parent ID*/
	@Excel(name = "Parent ID", width = 15)
    @ApiModelProperty(value = "Parent ID")
	private String parentId;
	/**Tenant ID*/
	@Excel(name = "Tenant ID", width = 15)
    @ApiModelProperty(value = "Tenant ID")
	private String tenantId;
	/**File size（kb）*/
	@Excel(name = "File size（kb）", width = 15)
    @ApiModelProperty(value = "File size（kb）")
	private Double fileSize;
	/**Whether or not to folders(1：BE  0：NOT)*/
	@Excel(name = "Whether the folder (1: Yes.)  0：NOT)", width = 15)
    @ApiModelProperty(value = "Whether the folder (1: Yes.)  0: No)")
	private String izFolder;
	/**Whether it is a Level 1 folder and allowed to be empty (1：BE )*/
	@Excel(name = "Whether it is a Level 1 folder and allowed to be empty (1: Yes.) )", width = 15)
    @ApiModelProperty(value = "Whether it is a Level 1 folder and allowed to be empty (1: Yes.) )")
	private String izRootFolder;
	/**Star (1: Yes.)  0：NOT)*/
	@Excel(name = "Star (1: Yes.)  0: No)", width = 15)
    @ApiModelProperty(value = "Star (1: Yes.)  0: No)")
	private String izStar;
	/**Number of downloads*/
	@Excel(name = "Number of downloads", width = 15)
    @ApiModelProperty(value = "Number of downloads")
	private Integer downCount;
	/**Readings*/
	@Excel(name = "Readings", width = 15)
    @ApiModelProperty(value = "Readings")
	private Integer readCount;
	/**Share the link*/
	@Excel(name = "Share the link", width = 15)
    @ApiModelProperty(value = "Share the link")
	private String shareUrl;
	/**Sharing permissions (1. Turn off sharing.) 2. Allow all contacts to view 3.Allow anyone to view it)*/
	@Excel(name = "Sharing permissions (1. Turn off sharing.) 2. Allow all contacts to view 3. Allow anyone to view)", width = 15)
    @ApiModelProperty(value = "Sharing permissions (1. Turn off sharing.) 2. Allow all contacts to view 3. Allow anyone to view)")
	private String sharePerms;
	/**Downloads are allowed or not (1: Yes.)  0: No)*/
	@Excel(name = "Downloads are allowed or not (1: Yes.)  0：NOT)", width = 15)
    @ApiModelProperty(value = "Downloads are allowed or not (1: Yes.)  0：NOT)")
	private String enableDown;
	/**Modification is allowed (1: Yes.)  0: No)*/
	@Excel(name = "Modification is allowed (1: Yes.)  0: No)", width = 15)
    @ApiModelProperty(value = "Modification is allowed (1: Yes.)  0: No)")
	private String enableUpdat;
	/**Deletion Status (0-Normal, 1-Delete to Recycle Bin)*/
	@Excel(name = "Deletion Status (0-Normal, 1-Delete to Recycle Bin)", width = 15)
    @ApiModelProperty(value = "Deletion Status (0-Normal, 1-Delete to Recycle Bin)")
	private String delFlag;

    /**
     * Field that does not exist in the file table: User data collection
     */
	@TableField(exist=false)
    private String userData;

    /**
     * Field that does not exist in the file table: the user's real name
     */
    @TableField(exist=false)
    private String realname;

    /**
     * Field that does not exist in the file table: Compressed name
     */
    @TableField(exist=false)
    private String zipName;
}
