package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: System data log
 * @author: jeecg-boot
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Slf4j
public class SysDataLog implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@TableId(type = IdType.ASSIGN_ID)
    /**
     * id
     */
	private String id;

    /**
     * Creator login name
     */
	private String createBy;

    /**
     * Creator’s real name
     */
	private String createName;

    /**
     * Creation date
     */
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;

    /**
     * Updater login name
     */
	private String updateBy;
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")

    /**
     * Updated
     */
    private Date updateTime;

    /**
     * Table Name
     */
    private String dataTable;

    /**
     * Data ID
     */
    private String dataId;

    /**
     * Data content
     */
    private String dataContent;

    /**
     * Version Number
     */
    private String dataVersion;


    //update-begin-author:taoyan date:2022-7-26 for: Used to log form comments Distinguish data
    /**
     * Stamp
     */
    private String type;
    //update-end-author:taoyan date:2022-7-26 for: Used to log form comments Distinguish data

    /**
     * By Way Of Login User Put Up Create Name
     */
    public void autoSetCreateName() {
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            this.setCreateName(sysUser.getRealname());
        } catch (Exception e) {
            log.warn("SecurityUtils.getSubject() Exception in obtaining user information:" + e.getMessage());
        }
    }

}
