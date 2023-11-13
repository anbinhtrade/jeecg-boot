package org.jeecg.modules.demo.test.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: ORDERS
 * @Author: jeecg-boot
 * @Date:  2019-02-15
 * @Version: V1.0
 */
@Data
@TableName("jeecg_order_main")
public class JeecgOrderMain implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**Primary key*/
    @TableId(type = IdType.ASSIGN_ID)
	private java.lang.String id;
	/**Order number*/
	private java.lang.String orderCode;
	/**The type of order*/
	private java.lang.String ctype;
	/**The date of the order*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date orderDate;
	/**The amount of the order*/
	private java.lang.Double orderMoney;
	/**Order notes*/
	private java.lang.String content;
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

	private String bpmStatus;
}
