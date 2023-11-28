package vn.abs.erp.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Data
public class NotificationAction {
    private String color;
    private String label;
    private String params;
    private String path;
    private String type;

    public String toJsonString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            return null;
        }
    }
}
