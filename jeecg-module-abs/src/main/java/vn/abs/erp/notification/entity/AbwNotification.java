package vn.abs.erp.notification.entity;

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
 * @Description: Notification ABS
 * @Author: jeecg-boot
 * @Date:   2023-11-23
 * @Version: V1.0
 */
@Data
@TableName("abw_notification")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="abw_notification object", description="Notification ABS")
public class AbwNotification implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**Người nhận*/
	@Excel(name = "Người nhận", width = 15)
    @ApiModelProperty(value = "Người nhận")
    private java.lang.String msgUserIds;
	/**Loại thông báo*/
	@Excel(name = "Loại thông báo", width = 15)
    @ApiModelProperty(value = "Loại thông báo")
    private java.lang.String msgCategory;
	/**Tiêu đề*/
	@Excel(name = "Tiêu đề", width = 15)
    @ApiModelProperty(value = "Tiêu đề")
    private java.lang.String msgTitle;
	/**Nội dung rút gọn*/
	@Excel(name = "Nội dung rút gọn", width = 15)
    @ApiModelProperty(value = "Nội dung rút gọn")
    private java.lang.String msgContent;
	/**Nội dung*/
	@Excel(name = "Nội dung", width = 15)
    @ApiModelProperty(value = "Nội dung")
    private java.lang.String msgBody;
	/**Thumbnail*/
	@Excel(name = "Thumbnail", width = 15)
    @ApiModelProperty(value = "Thumbnail")
    private java.lang.String msgThumbnailImage;
	/**Banner*/
	@Excel(name = "Banner", width = 15)
    @ApiModelProperty(value = "Banner")
    private java.lang.String msgBannerImage;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	/**Hẹn giờ*/
	@Excel(name = "Hẹn giờ", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "Hẹn giờ")
    private java.util.Date msgPlan;
}
