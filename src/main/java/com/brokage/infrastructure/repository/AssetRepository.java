package com.brokage.infrastructure.repository;

import com.brokage.domain.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByCustomerId(Long customerId);

    @Query("SELECT a FROM Asset a WHERE a.customerId = :customerId AND a.assetSymbol.symbol = :assetName")
    Optional<Asset> findByCustomerIdAndAssetNameForUpdate(@Param("customerId") Long customerId,
                                                          @Param("assetName") String assetName);
}
