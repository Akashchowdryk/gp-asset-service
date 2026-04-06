package com.gp.asset.repository;

import com.gp.asset.entity.GpAsset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GpAssetRepository extends JpaRepository<GpAsset, Long> {
}