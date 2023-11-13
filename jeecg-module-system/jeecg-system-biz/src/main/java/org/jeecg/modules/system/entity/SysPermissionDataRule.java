package org.jeecg.modules.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * Menu permission rule table
 * </p>
 *
 * @Author huangzhilin
 * @since 2019-03-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysPermissionDataRule implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	
	/**
	 * The corresponding menu ID
	 */
	private String permissionId;
	
	/**
	 * The name of the rule
	 */
	private String ruleName;
	
	/**
	 * FIELD
	 */
	private String ruleColumn;
	
	/**
	 * CONDITION
	 */
	private String ruleConditions;
	
	/**
	 * Rule value
	 */
	private String ruleValue;
	
	/**
	 * Status value 1 Effective 0 invalid
	 */
	private String status;
	
	/**
	 * Creation time
	 */
	private Date createTime;
	
	/**
	 * Created by
	 */
	private String createBy;
	
	/**
	 * Modify time
	 */
	private Date updateTime;
	
	/**
	 * Modified by
	 */
	private String updateBy;
}
