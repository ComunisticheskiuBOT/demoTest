package org.example.demotest.web_controllers;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestStorage;
import org.example.demotest.entities.Storage;
import org.example.demotest.services.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StorageController {
    private final StorageService storageService;

    @GetMapping("/storage-api/v1/storages")
    public List<Storage> findAllStorages(){
        return storageService.findAllStorages();
    }

    @GetMapping("/storage-api/v1/storages/{id}")
    public Storage getStorageById(@PathVariable Long id) {
        return storageService.findStorageById(id);
    }

    @GetMapping("/storage-api/v1/arrival-dates/storages/{arrivalDate}")
    public ResponseEntity<Storage> getQualityByInspectionDate(@PathVariable Date arrivalDate) {
        return storageService.findByArrivalDate(arrivalDate)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/storage-api/v1/storages")
    public ResponseEntity<Storage> processRequest(@RequestBody ServiceRequestStorage serviceRequestStorage) {
        boolean isNew = serviceRequestStorage.getStorageId() == null ||
                !storageService.existsById(serviceRequestStorage.getStorageId());

        Storage storage = storageService.createOrUpdateStorage(serviceRequestStorage);

        HttpStatus status = isNew ? HttpStatus.CREATED : HttpStatus.OK;

        return new ResponseEntity<>(storage, status);
    }


    @DeleteMapping("/storage-api/v1/storages/{id}")
    public ResponseEntity<Void> deleteStorage(@PathVariable Long id) {
        storageService.deleteStorage(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/storage-api/v1/storages/{id}")
    public ResponseEntity<Storage> updateStorage(@PathVariable Long id, @RequestBody ServiceRequestStorage updatedStorage) {
        return storageService.updatedStorage(id, updatedStorage)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
