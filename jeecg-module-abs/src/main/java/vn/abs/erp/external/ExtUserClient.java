package vn.abs.erp.external;


import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.abs.erp.dto.UserSearchDto;

import java.io.IOException;
import java.util.List;

/**
 * Use okHttp to load external API
 */
@Component
public class ExtUserClient {
    @Value("${app.user-service.url}")
    private String userServiceBaseUrl;

    @Value("${app.user-agent}")
    private String userAgent;

    @Value("${app.internal.secret-key}")
    private String internalSecretKey;

    private final ObjectMapper objectMapper;

    public ExtUserClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<List<UserSearchDto>> extSearch(final String searchTerm, final String ids) throws IOException {
        OkHttpClient client = AbsHttpClient.getClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "");
        String requestUrl = userServiceBaseUrl + "/i/user/search?searchTerm=" + searchTerm+"&ids="+ids;
        Request request = new Request.Builder()
                .url(requestUrl)
                .method("GET", null)
                .addHeader("x-api-secret", internalSecretKey)
                .addHeader("User-Agent", userAgent)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                List<UserSearchDto> userSearchDtos = objectMapper.readValue(response.body().string(), List.class);
                return ResponseEntity.ok(userSearchDtos);
            } else {
               return ResponseEntity.status(response.code()).build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
