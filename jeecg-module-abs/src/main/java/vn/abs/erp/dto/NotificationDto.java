package vn.abs.erp.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Notification dto - Request
 */
@Data
@Builder
public class NotificationDto implements Serializable {
    private String[] userIds;
    private String category;
    private String title;
    private String body;
    private String content;
    private String plan;
    private NotificationData data;
    private NotificationAction[] actions;
    private String bannerImage;
    private String thumbnailImage;

    // Convert to json string
    public String toJsonString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}

