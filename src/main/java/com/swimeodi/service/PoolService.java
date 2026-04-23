package com.swimeodi.service;

import com.swimeodi.dto.PoolDto;
import com.swimeodi.dto.PoolPriceDto;
import com.swimeodi.dto.PoolTimetableDto;
import com.swimeodi.entity.Pool;
import com.swimeodi.entity.PoolPrice;
import com.swimeodi.entity.PoolTimetable;
import com.swimeodi.repository.PoolPriceRepository;
import com.swimeodi.repository.PoolRepository;
import com.swimeodi.repository.PoolTimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PoolService {

    private final PoolRepository poolRepository;
    private final PoolTimetableRepository timetableRepository;
    private final PoolPriceRepository priceRepository;

    public List<PoolDto> getAllPools() {
        return toDtoListWithTimetable(poolRepository.findByIsDeletedFalse());
    }

    public List<PoolDto> searchByName(String name) {
        String[] terms = name.trim().split("\\s+");
        List<Pool> pools = poolRepository.findByIsDeletedFalse();
        List<Pool> filtered = pools.stream()
                .filter(p -> {
                    String poolName = p.getName().toLowerCase();
                    return Arrays.stream(terms).allMatch(t -> poolName.contains(t.toLowerCase()));
                })
                .collect(Collectors.toList());
        return toDtoListWithTimetable(filtered);
    }

    public List<PoolDto> searchByAddress(String address) {
        return toDtoListWithTimetable(
                poolRepository.findByIsDeletedFalseAndAddressContainingIgnoreCase(address));
    }

    private List<PoolDto> toDtoListWithTimetable(List<Pool> pools) {
        if (pools.isEmpty()) return Collections.emptyList();
        List<Long> ids = pools.stream().map(Pool::getId).collect(Collectors.toList());

        Map<Long, List<PoolTimetableDto>> timetableMap = timetableRepository.findByPoolIdIn(ids)
                .stream()
                .collect(Collectors.groupingBy(
                        PoolTimetable::getPoolId,
                        Collectors.mapping(t -> {
                            PoolTimetableDto dto = new PoolTimetableDto();
                            dto.setId(t.getId());
                            dto.setDayOfWeek(t.getDayOfWeek());
                            dto.setSessionNo(t.getSessionNo());
                            dto.setStartTime(t.getStartTime());
                            dto.setEndTime(t.getEndTime());
                            return dto;
                        }, Collectors.toList())
                ));

        Map<Long, List<PoolPriceDto>> priceMap = priceRepository.findByPoolIdIn(ids)
                .stream()
                .collect(Collectors.groupingBy(
                        PoolPrice::getPoolId,
                        Collectors.mapping(p -> {
                            PoolPriceDto dto = new PoolPriceDto();
                            dto.setId(p.getId());
                            dto.setTicketType(p.getTicketType());
                            dto.setDayType(p.getDayType());
                            dto.setAgeGroup(p.getAgeGroup());
                            dto.setPrice(p.getPrice());
                            dto.setNote(p.getNote());
                            return dto;
                        }, Collectors.toList())
                ));

        return pools.stream().map(pool -> {
            PoolDto dto = toDto(pool);
            dto.setTimetable(timetableMap.getOrDefault(pool.getId(), Collections.emptyList()));
            dto.setPrices(priceMap.getOrDefault(pool.getId(), Collections.emptyList()));
            return dto;
        }).collect(Collectors.toList());
    }

    public PoolDto getPool(Long id) {
        Pool pool = poolRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Pool not found with id: " + id));
        return toDtoWithDetail(pool);
    }

    @Transactional
    public PoolDto createPool(PoolDto dto) {
        Pool pool = poolRepository.save(toEntity(dto));
        saveTimetable(pool, dto.getTimetable());
        savePrices(pool, dto.getPrices());
        return toDtoWithDetail(pool);
    }

    @Transactional
    public PoolDto updatePool(Long id, PoolDto dto) {
        Pool pool = poolRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Pool not found with id: " + id));
        applyDtoToPool(pool, dto);
        pool.setAdminComment(dto.getAdminComment());
        poolRepository.save(pool);
        timetableRepository.deleteByPoolId(id);
        saveTimetable(pool, dto.getTimetable());
        priceRepository.deleteByPoolId(id);
        savePrices(pool, dto.getPrices());
        return toDtoWithDetail(pool);
    }

    @Transactional
    public void deletePool(Long id, String comment) {
        Pool pool = poolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pool not found with id: " + id));
        pool.setIsDeleted(true);
        pool.setAdminComment(comment);
        poolRepository.save(pool);
    }

    private void savePrices(Pool pool, List<PoolPriceDto> prices) {
        if (prices == null || prices.isEmpty()) return;
        List<PoolPrice> entities = prices.stream().map(dto -> {
            PoolPrice p = new PoolPrice();
            p.setPool(pool);
            p.setTicketType(dto.getTicketType());
            p.setDayType(dto.getDayType());
            p.setAgeGroup(dto.getAgeGroup());
            p.setPrice(dto.getPrice());
            p.setNote(dto.getNote());
            return p;
        }).collect(Collectors.toList());
        priceRepository.saveAll(entities);
    }

    private void saveTimetable(Pool pool, List<PoolTimetableDto> timetable) {
        if (timetable == null || timetable.isEmpty()) return;
        List<PoolTimetable> entities = timetable.stream().map(dto -> {
            PoolTimetable t = new PoolTimetable();
            t.setPool(pool);
            t.setDayOfWeek(dto.getDayOfWeek());
            t.setSessionNo(dto.getSessionNo());
            t.setStartTime(dto.getStartTime());
            t.setEndTime(dto.getEndTime());
            return t;
        }).collect(Collectors.toList());
        timetableRepository.saveAll(entities);
    }

    private PoolDto toDto(Pool pool) {
        PoolDto dto = new PoolDto();
        applyPoolToDto(pool, dto);
        return dto;
    }

    private PoolDto toDtoWithDetail(Pool pool) {
        PoolDto dto = new PoolDto();
        applyPoolToDto(pool, dto);
        dto.setTimetable(timetableRepository
                .findByPoolIdOrderByDayOfWeekAscSessionNoAsc(pool.getId())
                .stream().map(t -> {
                    PoolTimetableDto tdto = new PoolTimetableDto();
                    tdto.setId(t.getId());
                    tdto.setDayOfWeek(t.getDayOfWeek());
                    tdto.setSessionNo(t.getSessionNo());
                    tdto.setStartTime(t.getStartTime());
                    tdto.setEndTime(t.getEndTime());
                    return tdto;
                }).collect(Collectors.toList()));
        dto.setPrices(priceRepository.findByPoolId(pool.getId())
                .stream().map(p -> {
                    PoolPriceDto pdto = new PoolPriceDto();
                    pdto.setId(p.getId());
                    pdto.setTicketType(p.getTicketType());
                    pdto.setDayType(p.getDayType());
                    pdto.setAgeGroup(p.getAgeGroup());
                    pdto.setPrice(p.getPrice());
                    pdto.setNote(p.getNote());
                    return pdto;
                }).collect(Collectors.toList()));
        return dto;
    }

    private void applyPoolToDto(Pool pool, PoolDto dto) {
        dto.setId(pool.getId());
        dto.setName(pool.getName());
        dto.setAddress(pool.getAddress());
        dto.setLat(pool.getLat());
        dto.setLng(pool.getLng());
        dto.setPhone(pool.getPhone());
        dto.setOperatingHours(pool.getOperatingHours());
        dto.setOperatingHoursNote(pool.getOperatingHoursNote());
        dto.setPrice(pool.getPrice());
        dto.setDescription(pool.getDescription());
        dto.setLocationType(pool.getLocationType());
        dto.setLaneLength(pool.getLaneLength());
        dto.setLaneCount(pool.getLaneCount());
        dto.setHasBabyPool(pool.getHasBabyPool());
        dto.setShowerCleanliness(pool.getShowerCleanliness());
        dto.setHasTowelService(pool.getHasTowelService());
        dto.setHasHairDryer(pool.getHasHairDryer());
        dto.setHairDryerFee(pool.getHairDryerFee());
        dto.setParkingAvailable(pool.getParkingAvailable());
        dto.setParkingFee(pool.getParkingFee());
        dto.setDepth(pool.getDepth());
        dto.setCapacity(pool.getCapacity());
        dto.setFreeSwimmingAvailable(pool.getFreeSwimmingAvailable());
        dto.setPoolType(pool.getPoolType());
        dto.setFacilityType(pool.getFacilityType());
        dto.setEquipmentAvailable(pool.getEquipmentAvailable());
        dto.setPriceNoteFlag(pool.getPriceNoteFlag());
        dto.setPriceNote(pool.getPriceNote());
        dto.setTimetableNoteFlag(pool.getTimetableNoteFlag());
        dto.setTimetableNote(pool.getTimetableNote());
        dto.setWebsite(pool.getWebsite());
        dto.setAdminComment(pool.getAdminComment());
    }

    private Pool toEntity(PoolDto dto) {
        Pool pool = new Pool();
        applyDtoToPool(pool, dto);
        return pool;
    }

    private void applyDtoToPool(Pool pool, PoolDto dto) {
        pool.setName(dto.getName());
        pool.setAddress(dto.getAddress());
        pool.setLat(dto.getLat());
        pool.setLng(dto.getLng());
        pool.setPhone(dto.getPhone());
        pool.setOperatingHours(dto.getOperatingHours());
        pool.setOperatingHoursNote(dto.getOperatingHoursNote());
        pool.setPrice(dto.getPrice());
        pool.setDescription(dto.getDescription());
        pool.setLocationType(dto.getLocationType());
        pool.setLaneLength(dto.getLaneLength());
        pool.setLaneCount(dto.getLaneCount());
        pool.setHasBabyPool(dto.getHasBabyPool());
        pool.setShowerCleanliness(dto.getShowerCleanliness());
        pool.setHasTowelService(dto.getHasTowelService());
        pool.setHasHairDryer(dto.getHasHairDryer());
        pool.setHairDryerFee(dto.getHairDryerFee());
        pool.setParkingAvailable(dto.getParkingAvailable());
        pool.setParkingFee(dto.getParkingFee());
        pool.setDepth(dto.getDepth());
        pool.setCapacity(dto.getCapacity());
        pool.setFreeSwimmingAvailable(dto.getFreeSwimmingAvailable());
        pool.setPoolType(dto.getPoolType());
        pool.setFacilityType(dto.getFacilityType());
        pool.setEquipmentAvailable(dto.getEquipmentAvailable());
        pool.setPriceNoteFlag(dto.getPriceNoteFlag());
        pool.setPriceNote(dto.getPriceNote());
        pool.setTimetableNoteFlag(dto.getTimetableNoteFlag());
        pool.setTimetableNote(dto.getTimetableNote());
        pool.setWebsite(dto.getWebsite());
    }
}
