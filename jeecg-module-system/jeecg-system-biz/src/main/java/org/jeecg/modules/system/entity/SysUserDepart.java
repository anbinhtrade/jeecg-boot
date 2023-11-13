package org.jeecg.modules.system.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * @Description: User department
 * @author: jeecg-boot
 */
@Data
@TableName("sys_user_depart")
public class SysUserDepart implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**Primary key ID*/
    @TableId(type = IdType.ASSIGN_ID)
	private String id;
	/**User ID*/
	private String userId;
	/**Department ID*/
	private String depId;
	public SysUserDepart(String id, String userId, String depId) {
		super();
		this.id = id;
		this.userId = userId;
		this.depId = depId;
	}

	public SysUserDepart(String id, String departId) {
		this.userId = id;
		this.depId = departId;
	}
}
