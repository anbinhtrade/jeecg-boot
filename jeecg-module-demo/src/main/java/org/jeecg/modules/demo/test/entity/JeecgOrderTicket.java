package org.jeecg.modules.demo.test.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: Book a ticket
 * @Author: jeecg-boot
 * @Date:  2019-02-15
 * @Version: V1.0
 */
@Data
@TableName("jeecg_order_ticket")
public class JeecgOrderTicket implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**Primary key*/
    @TableId(type = IdType.ASSIGN_ID)
	private java.lang.String id;
	/**Flight number*/
	@Excel(name="Flight number",width=15)
	private java.lang.String ticketCode;
	/**Flight time*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Excel(name="Flight time",width=15,format = "yyyy-MM-dd")
	private java.util.Date tickectDate;
	/**Foreign Key*/
	private java.lang.String orderId;
	/**Created by*/
	private java.lang.String createBy;
	/**Creation time*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date createTime;
	/**Modified by*/
	private java.lang.String updateBy;
	/**Modify time*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date updateTime;
}
