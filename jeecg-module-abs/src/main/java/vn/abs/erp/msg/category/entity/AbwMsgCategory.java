package vn.abs.erp.msg.category.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: Message Category
 * @Author: jeecg-boot
 * @Date:   2023-11-23
 * @Version: V1.0
 */
@Data
@TableName("abw_msg_category")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="abw_msg_category object", description="Message Category")
public class AbwMsgCategory implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**Code*/
	@Excel(name = "Code", width = 15)
    @ApiModelProperty(value = "Code")
    private java.lang.String msgCatCode;
	/**Text*/
	@Excel(name = "Text", width = 15)
    @ApiModelProperty(value = "Text")
    private java.lang.String msgCatText;
	/**Created By*/
    @ApiModelProperty(value = "Created By")
    private java.lang.String createBy;
	/**Created At*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Created At")
    private java.util.Date createTime;
	/**Updated By*/
    @ApiModelProperty(value = "Updated By")
    private java.lang.String updateBy;
	/**Updated At*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Updated At")
    private java.util.Date updateTime;
	/**Org*/
    @ApiModelProperty(value = "Org")
    private java.lang.String sysOrgCode;
}
