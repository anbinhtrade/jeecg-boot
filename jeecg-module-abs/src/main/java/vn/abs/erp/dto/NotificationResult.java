package vn.abs.erp.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class NotificationResult implements Serializable {
    private int code;
    private String message;
}
