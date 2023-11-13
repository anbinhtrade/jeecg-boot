package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.util.Date;

/**
 * Tenant information
 * @author: jeecg-boot
 */
@Data
@TableName("sys_tenant")
public class SysTenant implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ENCODE
     */
    private Integer id;
    
    /**
     * NAME
     */
    private String name;
    

    /**
     * Created by
     */
    @Dict(dictTable ="sys_user",dicText = "realname",dicCode = "username")
    private String createBy;

    /**
     * Creation time
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * Start time
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date beginDate;

    /**
     * End time
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    /**
     * STATE 1 Normal 0 freezing
     */
    @Dict(dicCode = "tenant_status")
    private Integer status;

    /**
     * INDUSTRY
     */
    @Dict(dicCode = "trade")
    private String trade;

    /**
     * The size of the company
     */
    @Dict(dicCode = "company_size")
    private String companySize;

    /**
     * Company address
     */
    private String companyAddress;

    /**
     * Company logo
     */
    private String companyLogo;

    /**
     * NUMBER
     */
    private String houseNumber;

    /**
     * Place of work
     */
    private String workPlace;

    /**
     * Second-level domain name (temporarily unused, reserved field)
     */
    private String secondaryDomain;

    /**
     * Login background image (temporarily useless, reserved field)
     */
    private String loginBkgdImg;

    /**
     * RANK
     */
    @Dict(dicCode = "company_rank")
    private String position;

    /**
     * DEPARTMENT
     */
    @Dict(dicCode = "company_department")
    private String department;
    
    @TableLogic
    private Integer delFlag;

    /**The login name of the person who updated it*/
    private String updateBy;
    
    /**Updated date*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * Allow application for administrators 1 Allowed 0 is not allowed
     */
    private Integer applyStatus;
    
}
