package vn.abs.erp.notification.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import vn.abs.erp.dto.NotificationDto;
import vn.abs.erp.dto.NotificationResult;
import vn.abs.erp.external.ExtNotificationClient;
import vn.abs.erp.notification.entity.AbwNotification;
import vn.abs.erp.notification.mapper.AbwNotificationMapper;
import vn.abs.erp.notification.service.IAbwNotificationService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: Notification ABS
 * @Author: jeecg-boot
 * @Date:   2023-11-23
 * @Version: V1.0
 */
@Service
public class AbwNotificationServiceImpl extends ServiceImpl<AbwNotificationMapper, AbwNotification> implements IAbwNotificationService {
    @Autowired
    private ExtNotificationClient extNotificationClient;


    @Override
    @Transactional
    public void addNotification(AbwNotification abwNotification) {
        NotificationDto notificationDto = NotificationDto.builder()
                .userIds(abwNotification.getMsgUserIds().split(",")) // split user ids
                .title(abwNotification.getMsgTitle())
                .content(abwNotification.getMsgContent())
                .category(abwNotification.getMsgCategory())
                .bannerImage("sample_banner")
                .thumbnailImage("sample_thumbnail")
                .body(abwNotification.getMsgBody())
                .plan("sample_plan")
                .actions(null)
                .data(null)
                .build();
        NotificationResult notificationResult = extNotificationClient.sendNotification(notificationDto);
        // TODO - add status to notification and save
        save(abwNotification);
    }
}
