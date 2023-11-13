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
 * @Description: 订单客户
 * @Author: jeecg-boot
 * @Date:  2019-02-15
 * @Version: V1.0
 */
@Data
@TableName("jeecg_order_customer")
public class JeecgOrderCustomer implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**Primary key*/
    @TableId(type = IdType.ASSIGN_ID)
	private java.lang.String id;
	/**Customer name*/
	@Excel(name="Customer's first name",width=15)
	private java.lang.String name;
	/**Gender*/
	private java.lang.String sex;
	/**Identification number*/
	@Excel(name="Identification number",width=15)
	private java.lang.String idcard;
	/**Scanned copy of ID card*/
	private java.lang.String idcardPic;
	/**Phone 1*/
	@Excel(name="PHONE",width=15)
	private java.lang.String telphone;
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
