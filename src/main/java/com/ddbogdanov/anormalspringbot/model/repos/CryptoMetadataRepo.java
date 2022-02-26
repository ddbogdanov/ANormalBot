package com.ddbogdanov.anormalspringbot.model.repos;

import com.ddbogdanov.anormalspringbot.model.CryptoMetadata;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface CryptoMetadataRepo extends CrudRepository<CryptoMetadata, UUID> {
    List<CryptoMetadata> findBySymbol(String symbol);
    boolean existsBySymbol(String symbol);
}
