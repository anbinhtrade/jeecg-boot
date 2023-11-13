package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: System Notification Table
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
@Data
@TableName("sys_announcement")
public class SysAnnouncement implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private java.lang.String id;
    /**
     * Title
     */
    @Excel(name = "Title", width = 15)
    private java.lang.String titile;
    /**
     * Content
     */
    @Excel(name = "Content", width = 30)
    private java.lang.String msgContent;
    /**
     * Start time
     */
    @Excel(name = "Start time", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date startTime;
    /**
     * End time
     */
    @Excel(name = "End time", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date endTime;
    /**
     * Sender
     */
    @Excel(name = "Sender", width = 15)
    private java.lang.String sender;
    /**
     * Priority (Low L, Medium M, High H)
     */
    @Excel(name = "Priority", width = 15, dicCode = "priority")
    @Dict(dicCode = "priority")
    private java.lang.String priority;
    
    /**
     * Message Type 1: Notification Announcement 2: System Message
     */
    @Excel(name = "Message type", width = 15, dicCode = "msg_category")
    @Dict(dicCode = "msg_category")
    private java.lang.String msgCategory;
    /**
     * Notification object type (USER: specified user, ALL: all users)
     */
    @Excel(name = "Notification object type", width = 15, dicCode = "msg_type")
    @Dict(dicCode = "msg_type")
    private java.lang.String msgType;
    /**
     * Release status (0 not released, 1 released, 2 withdrawn)
     */
    @Excel(name = "Post status", width = 15, dicCode = "send_status")
    @Dict(dicCode = "send_status")
    private java.lang.String sendStatus;
    /**
     * Release Time
     */
    @Excel(name = "Release Time", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date sendTime;
    /**
     * Undo time
     */
    @Excel(name = "Undo time", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date cancelTime;
    /**
     * Deletion status (0, normal, 1 deleted)
     */
    private java.lang.String delFlag;
    /**
     * Founder
     */
    private java.lang.String createBy;
    /**
     * Creation Time
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date createTime;
    /**
     * Updater
     */
    private java.lang.String updateBy;
    /**
     * Update time
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date updateTime;
    /**
     * Specify user
     **/
    private java.lang.String userIds;
    /**
     * Business type (email: mail bpm: process tenant invite: tenant invite)
     */
    private java.lang.String busType;
    /**
     * Business Id
     */
    private java.lang.String busId;
    /**
     * Open method Component: component Route: url
     */
    private java.lang.String openType;
    /**
     * component/routeAddress
     */
    private java.lang.String openPage;
    /**
     * Summary/extended business parameters
     * 
     * Example:
     * 1 Summary Value
     *  Holiday arrangements
     * 2 Parameter value of jump process
     * {"taskDetail":true,"procInsId":"1706547306004377602","taskId":"task630958764530507776"}
     */
    private java.lang.String msgAbstract;
    /**
     * DingTalk task id, used to withdraw messages
     */
    private String dtTaskId;

    /**
     * Reading Status 1 Means Read
     */
    private transient String readFlag;

    /**
     * Star Status 1 Means Star
     */
    private transient String starFlag;

    /**
     * Send record ID
     */
    private transient String sendId;

    /**Tenant ID*/
    private java.lang.Integer tenantId;
}
