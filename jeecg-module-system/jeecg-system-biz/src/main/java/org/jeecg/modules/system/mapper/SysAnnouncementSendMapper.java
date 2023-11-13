package org.jeecg.modules.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysAnnouncementSend;
import org.jeecg.modules.system.model.AnnouncementSendModel;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Description: User Advertisement Read Tag Table
 * @Author: jeecg-boot
 * @Date:  2019-02-21
 * @Version: V1.0
 */
public interface SysAnnouncementSendMapper extends BaseMapper<SysAnnouncementSend> {

	/**
	 * Get my messages
	 * @param announcementSendModel
	 * @param page
	 * @return
	 */
	public List<AnnouncementSendModel> getMyAnnouncementSendList(Page<AnnouncementSendModel> page,@Param("announcementSendModel") AnnouncementSendModel announcementSendModel);

	/**
	 * Get a record
	 * @param sendId
	 * @return
	 */
	AnnouncementSendModel getOne(@Param("sendId") String sendId);


	/**
	 * Modified to Read Message
	 */
	void updateReaded(@Param("userId") String userId, @Param("annoceIdList") List<String> annoceIdList);

	/**
	 * Clear all unread messages
	 * @param userId
	 */
	void clearAllUnReadMessage(@Param("userId") String userId);
}
