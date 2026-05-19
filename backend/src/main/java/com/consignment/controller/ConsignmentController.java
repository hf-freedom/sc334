package com.consignment.controller;

import com.consignment.common.ItemStatus;
import com.consignment.dto.IdentifyRequest;
import com.consignment.dto.OrderRequest;
import com.consignment.dto.SubmitItemRequest;
import com.consignment.entity.ConsignmentItem;
import com.consignment.entity.ReturnTask;
import com.consignment.entity.SettlementRecord;
import com.consignment.service.ConsignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ConsignmentController {
    private final ConsignmentService consignmentService;

    public ConsignmentController(ConsignmentService consignmentService) {
        this.consignmentService = consignmentService;
    }

    @PostMapping("/items")
    public ResponseEntity<?> submitItem(@RequestBody SubmitItemRequest request) {
        try {
            ConsignmentItem item = consignmentService.submitItem(request);
            return success(item);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @PostMapping("/items/identify")
    public ResponseEntity<?> identifyItem(@RequestBody IdentifyRequest request) {
        try {
            ConsignmentItem item = consignmentService.identifyItem(request);
            return success(item);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @PostMapping("/items/order")
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest request) {
        try {
            ConsignmentItem item = consignmentService.placeOrder(request);
            return success(item);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @PostMapping("/items/{id}/pay")
    public ResponseEntity<?> payOrder(@PathVariable String id) {
        try {
            ConsignmentItem item = consignmentService.payOrder(id);
            return success(item);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @PostMapping("/items/{id}/release")
    public ResponseEntity<?> releaseItem(@PathVariable String id) {
        try {
            ConsignmentItem item = consignmentService.releaseItem(id);
            return success(item);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @GetMapping("/items")
    public ResponseEntity<?> getItems(@RequestParam(required = false) String status,
                                      @RequestParam(required = false) String userId) {
        List<ConsignmentItem> items;
        if (status != null) {
            items = consignmentService.getItemsByStatus(ItemStatus.valueOf(status));
        } else if (userId != null) {
            items = consignmentService.getItemsByUserId(userId);
        } else {
            items = consignmentService.getAllItems();
        }
        return success(items);
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<?> getItemById(@PathVariable String id) {
        ConsignmentItem item = consignmentService.getItemById(id);
        if (item == null) {
            return error("商品不存在");
        }
        return success(item);
    }

    @GetMapping("/settlements")
    public ResponseEntity<?> getSettlementRecords(@RequestParam(required = false) String userId) {
        List<SettlementRecord> records;
        if (userId != null) {
            records = consignmentService.getSettlementRecordsByUserId(userId);
        } else {
            records = consignmentService.getSettlementRecords();
        }
        return success(records);
    }

    @GetMapping("/return-tasks")
    public ResponseEntity<?> getReturnTasks() {
        List<ReturnTask> tasks = consignmentService.getReturnTasks();
        return success(tasks);
    }

    @PostMapping("/return-tasks/{id}/complete")
    public ResponseEntity<?> completeReturnTask(@PathVariable String id) {
        try {
            ReturnTask task = consignmentService.completeReturnTask(id);
            return success(task);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    private ResponseEntity<Map<String, Object>> success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return ResponseEntity.ok(result);
    }

    private ResponseEntity<Map<String, Object>> error(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", message);
        result.put("data", null);
        return ResponseEntity.ok(result);
    }
}
