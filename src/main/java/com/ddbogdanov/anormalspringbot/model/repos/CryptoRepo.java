package com.ddbogdanov.anormalspringbot.model.repos;

import com.ddbogdanov.anormalspringbot.model.Crypto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface CryptoRepo extends JpaRepository<Crypto, UUID> {
    List<Crypto> findAllBySymbol(String symbol);
    List<Crypto> findBySymbolOrderByDatetimeAsc(String symbol);
    List<Crypto> findByDatetimeBetween(String start, String end);
}
