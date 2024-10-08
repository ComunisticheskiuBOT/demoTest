package org.example.demotest.services;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestStorage;
import org.example.demotest.entities.Product;
import org.example.demotest.entities.Storage;
import org.example.demotest.repository.ProductRepository;
import org.example.demotest.repository.StorageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final StorageRepository storageRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Storage> findAllStorages(){
        return storageRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Storage createOrUpdateStorage(ServiceRequestStorage serviceRequestStorage) {
        try {
            // Проверка существования склада с указанным ID
            Optional<Storage> existingStorage = storageRepository.findById(serviceRequestStorage.getStorageId());

            if (existingStorage.isPresent()) {
                // Если склад с таким ID уже существует, обновляем его данные
                Storage storageToUpdate = existingStorage.get();
                storageToUpdate.setProduct(serviceRequestStorage.getProduct());
                storageToUpdate.setQuantity(serviceRequestStorage.getQuantity());
                storageToUpdate.setArrivalDate(serviceRequestStorage.getArrivalDate());
                return storageRepository.save(storageToUpdate);
            } else {
                // Если склада с таким ID нет, создаем новый
                return storageRepository.save(Storage.builder()
                        .product(serviceRequestStorage.getProduct())
                        .quantity(serviceRequestStorage.getQuantity())
                        .arrivalDate(serviceRequestStorage.getArrivalDate())
                        .build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании или обновлении склада", e);
        }
    }

    public boolean existsById(Long storageId) {
        return storageRepository.existsById(storageId);
    }

    @Transactional(readOnly = true)
    public Storage findStorageById(Long storage_id) {
        return storageRepository.findStorageByStorageId(storage_id);
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Продукт с ID '" + productId + "' не найден"));
    }

    @Transactional(readOnly = true)
    public Optional<Storage> findByArrivalDate(Date arrivalDate) {
        return storageRepository.findStorageByArrivalDate(arrivalDate);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteStorage(Long id) {
        storageRepository.deleteById(id);
    }

    public boolean productExists(Long productId) {
        return productRepository.findById(productId).isPresent();
    }

    public Optional<Storage> updatedStorage(Long id, ServiceRequestStorage updatedStorage) {
        return storageRepository.findById(id).map(storage -> {
            if (updatedStorage.getProduct() != null) {
                storage.setProduct(updatedStorage.getProduct());
            }
            if (updatedStorage.getQuantity() != null) {
                storage.setQuantity(updatedStorage.getQuantity());
            }
            if (updatedStorage.getArrivalDate() != null) {
                storage.setArrivalDate(updatedStorage.getArrivalDate());
            }

            return storageRepository.save(storage);
        });
    }
}
