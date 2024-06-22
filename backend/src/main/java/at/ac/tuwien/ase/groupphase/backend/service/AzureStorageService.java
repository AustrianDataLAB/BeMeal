package at.ac.tuwien.ase.groupphase.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AzureStorageService {
    private final ResourceLoader resourceLoader;
    private final String containerName;

    public AzureStorageService(ResourceLoader resourceLoader,
            @Value("${spring.cloud.azure.storage.container-name}") String containerName) {
        this.resourceLoader = resourceLoader;
        this.containerName = containerName;
    }

    public Resource getFile(String filename) {
        Resource file = resourceLoader.getResource(String.format("azure-blob://%s/%s.jpg", containerName, filename));
        return file.exists() ? file : null;
    }
}
