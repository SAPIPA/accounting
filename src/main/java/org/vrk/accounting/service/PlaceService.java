package org.vrk.accounting.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.Place;
import org.vrk.accounting.domain.dto.PlaceDTO;
import org.vrk.accounting.repository.PlaceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository repo;

    private Place toEntity(PlaceDTO dto) {
        return Place.builder()
                .id(dto.getId())
                .objId(dto.getObjId())
                .sText(dto.getSText())
                .ztlc(dto.getZtlc())
                .regio(dto.getRegio())
                .ort01(dto.getOrt01())
                .stras(dto.getStras())
                .hausn(dto.getHausn())
                .build();
    }

    private PlaceDTO toDto(Place entity) {
        return PlaceDTO.builder()
                .id(entity.getId())
                .objId(entity.getObjId())
                .sText(entity.getSText())
                .ztlc(entity.getZtlc())
                .regio(entity.getRegio())
                .ort01(entity.getOrt01())
                .stras(entity.getStras())
                .hausn(entity.getHausn())
                .build();
    }

    /** Создать новое место работы */
    @Transactional
    public PlaceDTO createPlace(PlaceDTO dto) {
        Place saved = repo.save(toEntity(dto));
        return toDto(saved);
    }

    /** Получить место работы по ID */
    @Transactional
    public PlaceDTO getPlaceById(Long id) {
        Place place = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Place not found, id=" + id));
        return toDto(place);
    }

    /** Список всех мест работы */
    @Transactional
    public List<PlaceDTO> listPlaces() {
        return repo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** Обновить существующее место работы */
    @Transactional
    public PlaceDTO updatePlace(Long id, PlaceDTO dto) {
        Place existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Place not found, id=" + id));

        existing.setObjId(dto.getObjId());
        existing.setSText(dto.getSText());
        existing.setZtlc(dto.getZtlc());
        existing.setRegio(dto.getRegio());
        existing.setOrt01(dto.getOrt01());
        existing.setStras(dto.getStras());
        existing.setHausn(dto.getHausn());

        Place updated = repo.save(existing);
        return toDto(updated);
    }

    /** Удалить место работы */
    @Transactional
    public void deletePlace(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Place not found, id=" + id);
        }
        repo.deleteById(id);
    }
}
