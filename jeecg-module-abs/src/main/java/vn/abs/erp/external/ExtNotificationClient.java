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

    /**
     * Send notification to user, via notification service. No Auth required
     * @param notificationDto notificationDto
     * @return NotificationResult, Success, Failed, etc
     */
    public NotificationResult sendNotification(NotificationDto notificationDto) {
        OkHttpClient client = AbsHttpClient.getClient();
        MediaType mediaType = MediaType.parse("application/json");
        // create request body from notificationDto
        RequestBody body = RequestBody.create(notificationDto.toJsonString(), mediaType);
        Request request = new Request.Builder()
                .method("POST", body)
                .addHeader("User-Agent", userAgent)
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdG9ja0FjY291bnQiOm51bGwsInN1YkFjY291bnRzIjpudWxsLCJzdWIiOiJTVVBFUl9VU0VSIiwicm9sZXMiOiJST0xFX1NVUEVSX1VTRVIiLCJpZCI6IlNVUEVSX1VTRVIiLCJ0eXBlIjoiU1VQRVJfVVNFUiIsImV4cCI6MjY1NDU3NTc5NCwiaWF0IjoxNjU0NTc1Nzk0fQ.XBMZVlj-NbMoi5VdEudVsRr3cVD-Op-mByTJ-0NMiun5IPcPZhbrdBwtrePhdPtx5eprEZgZcWoqQIGUbn37pw")
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
