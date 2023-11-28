package vn.abs.erp.notification.service;

import vn.abs.erp.notification.entity.AbwNotification;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: Notification ABS
 * @Author: jeecg-boot
 * @Date:   2023-11-28
 * @Version: V1.0
 */
public interface IAbwNotificationService extends IService<AbwNotification> {
    void addNotification(AbwNotification abwNotification);
}
