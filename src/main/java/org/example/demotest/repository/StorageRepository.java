package org.example.demotest.repository;

import org.example.demotest.entities.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {
    Storage findStorageByStorageId(Long quality_id);
    Optional<Storage> findStorageByArrivalDate(Date arrivalDate);
}
