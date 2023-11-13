package org.jeecg.modules.system.mapper;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysAnnouncement;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Description: System Notification Table
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
public interface SysAnnouncementMapper extends BaseMapper<SysAnnouncement> {

    /**
     * Get system announcements by message type and user ID
     * @param page
     * @param userId User ID
     * @param msgCategory The type of message
     * @return
     */
	List<SysAnnouncement> querySysCementListByUserId(Page<SysAnnouncement> page, @Param("userId")String userId,@Param("msgCategory")String msgCategory);

    /**
     * Paginate the list of all messages
     * @param page
     * @param userId
     * @param fromUser
     * @param beginDate
     * @param endDate
     * @return
     */
	List<SysAnnouncement> queryAllMessageList(Page<SysAnnouncement> page, @Param("userId")String userId, @Param("fromUser")String fromUser, @Param("starFlag")String starFlag, @Param("beginDate")Date beginDate, @Param("endDate")Date endDate);
   
    /**
     * Query notifications that have not been read by the user
     * @param currDate
     * @param userId
     * @return
     */
    List<String> getNotSendedAnnouncementlist(@Param("currDate") Date currDate, @Param("userId")String userId);
}
