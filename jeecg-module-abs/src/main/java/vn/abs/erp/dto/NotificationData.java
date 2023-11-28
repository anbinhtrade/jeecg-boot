package vn.abs.erp.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Data
public class NotificationData {
    private String isEnableBackground;
    private String isEnableInApp;
    private String notificationType;
    private String params;

    public String toJsonString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            return null;
        }
    }
}
