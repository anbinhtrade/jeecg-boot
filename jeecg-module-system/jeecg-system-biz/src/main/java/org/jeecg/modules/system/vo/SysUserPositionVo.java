package org.jeecg.modules.system.vo;

import lombok.Data;

/**
* @Description: 用户职位实体类
*
* @author: wangshuai
* @date: 2023/6/14 16:41
*/
@Data
public class SysUserPositionVo {
    
    /**Job ID*/
    private String id;

    /**Job title*/
    private String name;
    
    /**User ID*/
    private String userId;
}
