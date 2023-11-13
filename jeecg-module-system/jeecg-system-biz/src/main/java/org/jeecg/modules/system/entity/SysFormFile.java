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
 * @Description: Form comment file
 * @Author: jeecg-boot
 * @Date:   2022-07-21
 * @Version: V1.0
 */
@Data
@TableName("sys_form_file")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_form_file对象", description="表单评论文件")
public class SysFormFile {
    
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
	/**The ID of the associated file*/
	@Excel(name = "The ID of the associated file", width = 15)
    @ApiModelProperty(value = "The ID of the associated file")
	private String fileId;
	/**Document type (folder: folder excel:excel doc:word pp:ppt image: an image  archive:Additional Documentation video:VIDEO）*/
	@Excel(name = "Document type (folder: folder excel:excel doc:word pp:ppt image: an image  archive: other documents video: video)", width = 15)
	@ApiModelProperty(value = "Document type (folder: folder excel:excel doc:word pp:ppt image: an image  archive:其他文档 video: video)")
	private String fileType;
	/**Creator login name*/
	@Excel(name = "Creator login name", width = 15)
    @ApiModelProperty(value = "Creator login name")
	private String createBy;
	/**Date of creation*/
	@Excel(name = "Date of creation", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Date of creation")
	private Date createTime;
}
