package com.consignment.repository;

import com.consignment.entity.ReturnTask;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ReturnTaskRepository {
    private final Map<String, ReturnTask> taskMap = new ConcurrentHashMap<>();

    public ReturnTask save(ReturnTask task) {
        taskMap.put(task.getId(), task);
        return task;
    }

    public ReturnTask findById(String id) {
        return taskMap.get(id);
    }

    public List<ReturnTask> findAll() {
        return new ArrayList<>(taskMap.values());
    }

    public List<ReturnTask> findByUserId(String userId) {
        return taskMap.values().stream()
                .filter(task -> userId.equals(task.getUserId()))
                .collect(Collectors.toList());
    }

    public boolean existsByItemId(String itemId) {
        return taskMap.values().stream()
                .anyMatch(task -> itemId.equals(task.getItemId()));
    }
}
