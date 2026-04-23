package com.swimeodi.service;

import com.swimeodi.dto.PoolPriceDto;
import com.swimeodi.dto.PoolReportDto;
import com.swimeodi.dto.PoolTimetableDto;
import com.swimeodi.entity.*;
import com.swimeodi.entity.PoolReport.ReportStatus;
import com.swimeodi.entity.PoolReport.ReportType;
import com.swimeodi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PoolReportService {

    private final PoolReportRepository reportRepository;
    private final PoolReportTimetableRepository reportTimetableRepository;
    private final PoolReportPriceRepository reportPriceRepository;
    private final PoolRepository poolRepository;
    private final PoolTimetableRepository timetableRepository;
    private final PoolPriceRepository priceRepository;

    @Transactional
    public PoolReportDto create(PoolReportDto dto) {
        PoolReport report = new PoolReport();
        applyDtoToReport(report, dto);

        if (dto.getPoolId() != null) {
            poolRepository.findById(dto.getPoolId()).ifPresent(report::setPool);
        }
        if (dto.getReportType() != null) {
            report.setReportType(dto.getReportType());
        } else {
            report.setReportType(dto.getPoolId() != null ? ReportType.CORRECTION : ReportType.NEW);
        }

        PoolReport saved = reportRepository.save(report);
        saveTimetable(saved, dto.getTimetable());
        savePrices(saved, dto.getPrices());
        return toDto(saved);
    }

    public List<PoolReportDto> getPending() {
        return reportRepository.findByStatusOrderByCreatedAtDesc(ReportStatus.PENDING)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<PoolReportDto> getAll() {
        return reportRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public PoolReportDto approve(Long id, String adminComment) {
        PoolReport report = findReport(id);

        Long existingPoolId = report.getPool() != null ? report.getPool().getId() : null;

        // 숨기기 요청 승인
        if (report.getReportType() == ReportType.HIDE) {
            if (existingPoolId == null) throw new RuntimeException("숨기기 요청에 연결된 수영장이 없습니다.");
            Pool pool = poolRepository.findById(existingPoolId)
                    .orElseThrow(() -> new RuntimeException("Pool not found: " + existingPoolId));
            pool.setIsDeleted(true);
            pool.setAdminComment(report.getReporterNote());
            poolRepository.save(pool);
            report.setStatus(ReportStatus.DONE);
            if (adminComment != null) report.setAdminComment(adminComment);
            return toDto(reportRepository.save(report));
        }

        Pool pool = (report.getReportType() == ReportType.CORRECTION && existingPoolId != null)
                ? poolRepository.findById(existingPoolId)
                        .orElseThrow(() -> new RuntimeException("Pool not found: " + existingPoolId))
                : new Pool();

        applyReportToPool(pool, report);
        Pool savedPool = poolRepository.save(pool);

        timetableRepository.deleteByPoolId(savedPool.getId());
        reportTimetableRepository.findByReportId(id).forEach(rt -> {
            PoolTimetable t = new PoolTimetable();
            t.setPool(savedPool);
            t.setDayOfWeek(rt.getDayOfWeek());
            t.setSessionNo(rt.getSessionNo());
            t.setStartTime(rt.getStartTime());
            t.setEndTime(rt.getEndTime());
            timetableRepository.save(t);
        });

        priceRepository.deleteByPoolId(savedPool.getId());
        reportPriceRepository.findByReportId(id).forEach(rp -> {
            PoolPrice p = new PoolPrice();
            p.setPool(savedPool);
            p.setTicketType(rp.getTicketType());
            p.setDayType(rp.getDayType());
            p.setAgeGroup(rp.getAgeGroup());
            p.setPrice(rp.getPrice());
            p.setNote(rp.getNote());
            priceRepository.save(p);
        });

        report.setStatus(ReportStatus.DONE);
        if (adminComment != null) report.setAdminComment(adminComment);
        return toDto(reportRepository.save(report));
    }

    @Transactional
    public PoolReportDto markDone(Long id) {
        PoolReport report = findReport(id);
        report.setStatus(ReportStatus.DONE);
        return toDto(reportRepository.save(report));
    }

    @Transactional
    public PoolReportDto reject(Long id, String adminComment) {
        PoolReport report = findReport(id);
        report.setStatus(ReportStatus.REJECTED);
        if (adminComment != null) report.setAdminComment(adminComment);
        return toDto(reportRepository.save(report));
    }

    private PoolReport findReport(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found: " + id));
    }

    private void saveTimetable(PoolReport report, List<PoolTimetableDto> timetable) {
        if (timetable == null || timetable.isEmpty()) return;
        timetable.forEach(dto -> {
            PoolReportTimetable t = new PoolReportTimetable();
            t.setReport(report);
            t.setDayOfWeek(dto.getDayOfWeek());
            t.setSessionNo(dto.getSessionNo());
            t.setStartTime(dto.getStartTime());
            t.setEndTime(dto.getEndTime());
            reportTimetableRepository.save(t);
        });
    }

    private void savePrices(PoolReport report, List<PoolPriceDto> prices) {
        if (prices == null || prices.isEmpty()) return;
        prices.forEach(dto -> {
            PoolReportPrice p = new PoolReportPrice();
            p.setReport(report);
            p.setTicketType(dto.getTicketType());
            p.setDayType(dto.getDayType());
            p.setAgeGroup(dto.getAgeGroup());
            p.setPrice(dto.getPrice());
            p.setNote(dto.getNote());
            reportPriceRepository.save(p);
        });
    }

    private PoolReportDto toDto(PoolReport r) {
        PoolReportDto dto = new PoolReportDto();
        dto.setId(r.getId());
        dto.setReportType(r.getReportType());
        dto.setPoolId(r.getPool() != null ? r.getPool().getId() : null);
        dto.setName(r.getName());
        dto.setAddress(r.getAddress());
        dto.setLat(r.getLat());
        dto.setLng(r.getLng());
        dto.setPhone(r.getPhone());
        dto.setWebsite(r.getWebsite());
        dto.setDescription(r.getDescription());
        dto.setOperatingHours(r.getOperatingHours());
        dto.setOperatingHoursNote(r.getOperatingHoursNote());
        dto.setFreeSwimmingAvailable(r.getFreeSwimmingAvailable());
        dto.setPriceNoteFlag(r.getPriceNoteFlag());
        dto.setPriceNote(r.getPriceNote());
        dto.setTimetableNoteFlag(r.getTimetableNoteFlag());
        dto.setTimetableNote(r.getTimetableNote());
        dto.setPoolType(r.getPoolType());
        dto.setFacilityType(r.getFacilityType());
        dto.setLocationType(r.getLocationType());
        dto.setLaneLength(r.getLaneLength());
        dto.setLaneCount(r.getLaneCount());
        dto.setDepth(r.getDepth());
        dto.setCapacity(r.getCapacity());
        dto.setHasBabyPool(r.getHasBabyPool());
        dto.setEquipmentAvailable(r.getEquipmentAvailable());
        dto.setHasTowelService(r.getHasTowelService());
        dto.setHasHairDryer(r.getHasHairDryer());
        dto.setHairDryerFee(r.getHairDryerFee());
        dto.setParkingAvailable(r.getParkingAvailable());
        dto.setParkingFee(r.getParkingFee());
        dto.setShowerCleanliness(r.getShowerCleanliness());
        dto.setReporterNote(r.getReporterNote());
        dto.setStatus(r.getStatus());
        dto.setAdminComment(r.getAdminComment());
        dto.setCreatedAt(r.getCreatedAt());

        dto.setTimetable(reportTimetableRepository.findByReportId(r.getId()).stream().map(t -> {
            PoolTimetableDto td = new PoolTimetableDto();
            td.setDayOfWeek(t.getDayOfWeek());
            td.setSessionNo(t.getSessionNo());
            td.setStartTime(t.getStartTime());
            td.setEndTime(t.getEndTime());
            return td;
        }).collect(Collectors.toList()));

        dto.setPrices(reportPriceRepository.findByReportId(r.getId()).stream().map(p -> {
            PoolPriceDto pd = new PoolPriceDto();
            pd.setTicketType(p.getTicketType());
            pd.setDayType(p.getDayType());
            pd.setAgeGroup(p.getAgeGroup());
            pd.setPrice(p.getPrice());
            pd.setNote(p.getNote());
            return pd;
        }).collect(Collectors.toList()));

        return dto;
    }

    private void applyDtoToReport(PoolReport r, PoolReportDto dto) {
        r.setName(dto.getName());
        r.setAddress(dto.getAddress());
        r.setLat(dto.getLat());
        r.setLng(dto.getLng());
        r.setPhone(dto.getPhone());
        r.setWebsite(dto.getWebsite());
        r.setDescription(dto.getDescription());
        r.setOperatingHours(dto.getOperatingHours());
        r.setOperatingHoursNote(dto.getOperatingHoursNote());
        r.setFreeSwimmingAvailable(dto.getFreeSwimmingAvailable());
        r.setPriceNoteFlag(dto.getPriceNoteFlag());
        r.setPriceNote(dto.getPriceNote());
        r.setTimetableNoteFlag(dto.getTimetableNoteFlag());
        r.setTimetableNote(dto.getTimetableNote());
        r.setPoolType(dto.getPoolType());
        r.setFacilityType(dto.getFacilityType());
        r.setLocationType(dto.getLocationType());
        r.setLaneLength(dto.getLaneLength());
        r.setLaneCount(dto.getLaneCount());
        r.setDepth(dto.getDepth());
        r.setCapacity(dto.getCapacity());
        r.setHasBabyPool(dto.getHasBabyPool());
        r.setEquipmentAvailable(dto.getEquipmentAvailable());
        r.setHasTowelService(dto.getHasTowelService());
        r.setHasHairDryer(dto.getHasHairDryer());
        r.setHairDryerFee(dto.getHairDryerFee());
        r.setParkingAvailable(dto.getParkingAvailable());
        r.setParkingFee(dto.getParkingFee());
        r.setShowerCleanliness(dto.getShowerCleanliness());
        r.setReporterNote(dto.getReporterNote());
    }

    private void applyReportToPool(Pool pool, PoolReport r) {
        if (r.getName() != null) pool.setName(r.getName());
        if (r.getAddress() != null) pool.setAddress(r.getAddress());
        if (r.getLat() != null) pool.setLat(r.getLat());
        if (r.getLng() != null) pool.setLng(r.getLng());
        if (r.getPhone() != null) pool.setPhone(r.getPhone());
        if (r.getWebsite() != null) pool.setWebsite(r.getWebsite());
        if (r.getDescription() != null) pool.setDescription(r.getDescription());
        if (r.getOperatingHours() != null) pool.setOperatingHours(r.getOperatingHours());
        if (r.getOperatingHoursNote() != null) pool.setOperatingHoursNote(r.getOperatingHoursNote());
        if (r.getFreeSwimmingAvailable() != null) pool.setFreeSwimmingAvailable(r.getFreeSwimmingAvailable());
        if (r.getPriceNoteFlag() != null) pool.setPriceNoteFlag(r.getPriceNoteFlag());
        if (r.getPriceNote() != null) pool.setPriceNote(r.getPriceNote());
        if (r.getTimetableNoteFlag() != null) pool.setTimetableNoteFlag(r.getTimetableNoteFlag());
        if (r.getTimetableNote() != null) pool.setTimetableNote(r.getTimetableNote());
        if (r.getPoolType() != null) pool.setPoolType(r.getPoolType());
        if (r.getFacilityType() != null) pool.setFacilityType(r.getFacilityType());
        if (r.getLocationType() != null) pool.setLocationType(r.getLocationType());
        if (r.getLaneLength() != null) pool.setLaneLength(r.getLaneLength());
        if (r.getLaneCount() != null) pool.setLaneCount(r.getLaneCount());
        if (r.getDepth() != null) pool.setDepth(r.getDepth());
        if (r.getCapacity() != null) pool.setCapacity(r.getCapacity());
        if (r.getHasBabyPool() != null) pool.setHasBabyPool(r.getHasBabyPool());
        if (r.getEquipmentAvailable() != null) pool.setEquipmentAvailable(r.getEquipmentAvailable());
        if (r.getHasTowelService() != null) pool.setHasTowelService(r.getHasTowelService());
        if (r.getHasHairDryer() != null) pool.setHasHairDryer(r.getHasHairDryer());
        if (r.getHairDryerFee() != null) pool.setHairDryerFee(r.getHairDryerFee());
        if (r.getParkingAvailable() != null) pool.setParkingAvailable(r.getParkingAvailable());
        if (r.getParkingFee() != null) pool.setParkingFee(r.getParkingFee());
        if (r.getShowerCleanliness() != null) pool.setShowerCleanliness(r.getShowerCleanliness());
        pool.setIsDeleted(false);
    }
}
