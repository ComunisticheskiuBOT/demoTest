package org.example.demotest.repository;

import org.example.demotest.entities.Product;
import org.example.demotest.entities.Quality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface QualityRepository extends JpaRepository<Quality, Long> {
    Quality findQualityByQualityId(Long quality_id);
    Optional<Quality> findQualityByInspectionDate(Date inspectionDate);
}
