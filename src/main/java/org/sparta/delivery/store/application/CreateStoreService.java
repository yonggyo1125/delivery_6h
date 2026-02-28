package org.sparta.delivery.store.application;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.global.domain.service.AddressToCoords;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.store.application.dto.StoreServiceDto;
import org.sparta.delivery.store.domain.Store;
import org.sparta.delivery.store.domain.StoreRepository;
import org.sparta.delivery.store.domain.service.OwnerCheck;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateStoreService {
    private final StoreRepository repository;
    private final RoleCheck roleCheck;
    private final OwnerCheck ownerCheck;
    private final AddressToCoords addressToCoords;

    @Transactional
    public UUID create(StoreServiceDto.CreateStore dto) {

        Store store = Store.builder()
                .ownerId(dto.getOwnerId())
                .ownerName(dto.getOwnerName())
                .landline(dto.getLandline())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .categoryIds(dto.getCategoryId())
                .roleCheck(roleCheck)
                .ownerCheck(ownerCheck)
                .addressToCoords(addressToCoords)
                .build();

        repository.save(store);

        return store.getId().getId();
    }

}
