package org.jeecg.modules.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * Dictionary table
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysDict implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * [reserved field, temporarily useless]
     * Dictionary type,0 string,1 number类型,2 boolean
     * Front-end JS pairs the Stirng type and the Number type boolean The type is sensitive and needs to be distinguished. It is used when the select tag is matched
     * The default is of the string type
     */
    private Integer type;
    
    /**
     * Dictionary name
     */
    private String dictName;

    /**
     * Dictionary encoding
     */
    private String dictCode;

    /**
     * Description
     */
    private String description;

    /**
     * Delete the status
     */
    @TableLogic
    private Integer delFlag;

    /**
     * Created by
     */
    private String createBy;

    /**
     * Creation time
     */
    private Date createTime;

    /**
     * UPDATER
     */
    private String updateBy;

    /**
     * UPDATED
     */
    private Date updateTime;

    /**Tenant ID*/
    private java.lang.Integer tenantId;
    
    /** The associated low-code app ID */
    private java.lang.String lowAppId;

}
