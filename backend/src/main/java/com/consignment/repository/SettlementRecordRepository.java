package com.consignment.repository;

import com.consignment.entity.SettlementRecord;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class SettlementRecordRepository {
    private final Map<String, SettlementRecord> recordMap = new ConcurrentHashMap<>();

    public SettlementRecord save(SettlementRecord record) {
        recordMap.put(record.getId(), record);
        return record;
    }

    public SettlementRecord findById(String id) {
        return recordMap.get(id);
    }

    public List<SettlementRecord> findAll() {
        return new ArrayList<>(recordMap.values());
    }

    public List<SettlementRecord> findByUserId(String userId) {
        return recordMap.values().stream()
                .filter(record -> userId.equals(record.getUserId()))
                .collect(Collectors.toList());
    }
}
