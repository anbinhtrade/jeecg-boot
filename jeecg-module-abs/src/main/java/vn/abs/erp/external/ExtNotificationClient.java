package vn.abs.erp.external;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.abs.erp.dto.NotificationDto;
import vn.abs.erp.dto.NotificationResult;

import java.io.IOException;

// http://admin-portal.abs.vn/api/v1/notification/send
// https://api-internal.abs.vn/v1/noti-business/plan/
@Component
public class ExtNotificationClient {
    @Value("${app.notification-service.url}")
    private String notificationUrl;

    @Value("${app.user-agent}")
    private String userAgent;

    @Value("${app.notification-service.access-token}")
    private String notiBearerAccessToken;

    /**
     * Send notification to user, via notification service. No Auth required
     * @param notificationDto notificationDto
     * @return NotificationResult, Success, Failed, etc
     */
    public NotificationResult sendNotification(NotificationDto notificationDto) {
        OkHttpClient client = AbsHttpClient.getClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(notificationDto.toJsonString(), mediaType);
        Request request = new Request.Builder()
                .method("POST", body)
                .addHeader("User-Agent", userAgent)
                .addHeader("Authorization", notiBearerAccessToken)
                .url(notificationUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return NotificationResult.builder().code(response.code()).message("Notification was sent: " + response.message()).build();
            } else {
                return NotificationResult.builder().code(response.code()).message(response.message()).build();
            }
        } catch (IOException e) {
            return NotificationResult.builder().code(500).message(e.getMessage()).build();
        }
    }
}
