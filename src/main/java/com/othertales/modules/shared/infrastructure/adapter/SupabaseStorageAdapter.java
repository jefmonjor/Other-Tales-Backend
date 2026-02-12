package com.othertales.modules.shared.infrastructure.adapter;

import com.othertales.modules.shared.application.port.StoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SupabaseStorageAdapter implements StoragePort {

    private final RestClient restClient;
    private final String bucketName;
    private final String supabaseUrl;
    private final String supabaseKey;

    public SupabaseStorageAdapter(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.key}") String supabaseKey,
            @Value("${supabase.bucket:project-images}") String bucketName) {
        this.supabaseUrl = supabaseUrl;
        this.supabaseKey = supabaseKey;
        this.bucketName = bucketName;

        this.restClient = RestClient.builder()
                .baseUrl(supabaseUrl + "/storage/v1")
                .defaultHeader("Authorization", "Bearer " + supabaseKey)
                .defaultHeader("apikey", supabaseKey)
                .build();
    }

    @Override
    public String upload(String path, byte[] content, String contentType) {
        // Supabase Storage Upload API: POST /object/{bucket}/{path}
        // Note: Supabase sometimes treats updates vs creates differently (POST vs PUT)
        // or requires 'x-upsert' header.
        // We will use POST with x-upsert: true

        restClient.post()
                .uri("/object/{bucket}/{path}", bucketName, path)
                .contentType(MediaType.parseMediaType(contentType))
                .header("x-upsert", "true")
                .body(content)
                .retrieve()
                .toBodilessEntity();

        // Construct Public URL
        return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + path;
    }

    @Override
    public void delete(String path) {
        restClient.delete()
                .uri("/object/{bucket}/{path}", bucketName, path)
                .retrieve()
                .toBodilessEntity();
    }
}
