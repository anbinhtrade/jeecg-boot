package org.jeecg.modules.system.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
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
 * User table
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * Log in to your account
     */
    @Excel(name = "Log in to your account", width = 15)
    private String username;

    /**
     * Real name
     */
    @Excel(name = "Real name", width = 15)
    private String realname;

    /**
     * Password
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * Md 5 Cipher Salt
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String salt;

    /**
     * Avatar
     */
    @Excel(name = "Avatar", width = 15,type = 2)
    private String avatar;

    /**
     * Birthday
     */
    @Excel(name = "Birthday", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * Gender (1: Male 2: Woman)
     */
    @Excel(name = "Gender", width = 15,dicCode="sex")
    @Dict(dicCode = "sex")
    private Integer sex;

    /**
     * Email
     */
    @Excel(name = "Email", width = 15)
    private String email;

    /**
     * Phone
     */
    @Excel(name = "Phone", width = 15)
    private String phone;

    /**
     * Log in and select the department code
     */
    private String orgCode;
    /**
     * Log in and select the tenant ID
     */
    private Integer loginTenantId;

    /**Department name*/
    private transient String orgCodeTxt;

    /**
     * Status (1: Normal  2：Freeze ）
     */
    @Excel(name = "State", width = 15,dicCode="user_status")
    @Dict(dicCode = "user_status")
    private Integer status;

    /**
     * Deleted Status (0, Normal, 1 Deleted)
     */
    @Excel(name = "Delete the status", width = 15,dicCode="del_flag")
    @TableLogic
    private Integer delFlag;

    /**
     * Job number, unique key
     */
    @Excel(name = "Construction No.", width = 15)
    private String workNo;

    /**
     * Job title, associated job list
     */
    @Excel(name = "Office", width = 15)
    @Dict(dictTable ="sys_position",dicText = "name",dicCode = "id")
    @TableField(exist = false)
    private String post;

    /**
     * Landline number
     */
    @Excel(name = "Landline number", width = 15)
    private String telephone;

    /**
     * Created by
     */
    private String createBy;

    /**
     * Creation time
     */
    private Date createTime;

    /**
     * Updater
     */
    private String updateBy;

    /**
     * Updated
     */
    private Date updateTime;
    /**
     * Synchronization workflow engine 1 synchronization 0 is not synchronized
     */
    private Integer activitiSync;

    /**
     * Identity (0 Ordinary members 1 Superior)
     */
    @Excel(name="(1 ordinary member.) 2 Superior)",width = 15)
    private Integer userIdentity;

    /**
     * Responsible department
     */
    @Excel(name="Responsible department",width = 15,dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    @Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    private String departIds;

    /**
     * Multi-tenant IDS is used temporarily, but the database is not persisted (database fields do not exist)
     */
    @TableField(exist = false)
    private String relTenantIds;

    /**Device ID uniapp For Thrusting*/
    private String clientId;

    /**
     * Login home address
     */
    @TableField(exist = false)
    private String homePath;

    /**
     * Job Title:
     */
    @TableField(exist = false)
    private String postText;

    /**
     * Process status
     */
    private String bpmStatus;

    /**
     * Whether a third party has been bound
     */
    @TableField(exist = false)
    private boolean izBindThird;
}
