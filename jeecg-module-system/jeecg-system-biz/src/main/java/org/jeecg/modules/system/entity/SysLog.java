package org.jeecg.modules.system.entity;

import java.util.Date;

import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * Syslog table
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysLog implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**
	 * Created by
	 */
	private String createBy;

	/**
	 * Creation time
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**
	 * UPDATER
	 */
	private String updateBy;

	/**
	 * UPDATED
	 */
	private Date updateTime;

	/**
	 * TAKE
	 */
	private Long costTime;

	/**
	 * IP
	 */
	private String ip;

	/**
	 * Request parameters
	 */
	private String requestParam;

	/**
	 * The type of request
	 */
	private String requestType;

	/**
	 * The path of the request
	 */
	private String requestUrl;
	/**
	 * Request method
	 */
	private String method;

	/**
	 * The name of the operator's user
	 */
	private String username;
	/**
	 * Operator user account
	 */
	private String userid;
	/**
	 * Detailed logs of operations
	 */
	private String logContent;

	/**
	 * Log Type (1 Login Log, 2 Operation Logs)
	 */
	@Dict(dicCode = "log_type")
	private Integer logType;

	/**
	 * Operation Type (1 Query, 2 Add, 3 Modify, 4 Delete, 5 Import, 6 Export)
	 */
	@Dict(dicCode = "operate_type")
	private Integer operateType;

	/**
	 * Tenant ID
	 */
	private Integer tenantId;

}
