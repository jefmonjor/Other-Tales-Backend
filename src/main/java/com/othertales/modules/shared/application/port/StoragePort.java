package com.othertales.modules.shared.application.port;

public interface StoragePort {
    /**
     * Uploads the content to the storage provider.
     * 
     * @param path        The full path including filename within the
     *                    bucket/container.
     * @param content     The file content as byte array.
     * @param contentType The MIME type of the file.
     * @return The public URL of the uploaded file.
     */
    String upload(String path, byte[] content, String contentType);

    /**
     * Deletes a file from the storage provider.
     * 
     * @param path The full path including filename within the bucket/container.
     */
    void delete(String path);
}
