package vn.abs.erp.external;

import okhttp3.OkHttpClient;

public class AbsHttpClient {

    private AbsHttpClient() {
    }

    private static final class ClientHolder {
        static final OkHttpClient client = new OkHttpClient.Builder()
                .build();
    }

    public static OkHttpClient getClient() {
        return ClientHolder.client;
    }
}
