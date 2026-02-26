package com.mykyda.placholderdrones.app.api;

import com.mykyda.placholderdrones.app.DTO.OrderCreateDTO;
import com.mykyda.placholderdrones.app.DTO.OrderStatsDTO;
import com.mykyda.placholderdrones.app.database.entity.Order;
import com.mykyda.placholderdrones.app.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public Order getById(@PathVariable long id) {
        return orderService.findById(id);
    }

    @GetMapping
    public List<Order> getAllFiltered(@RequestParam Map<String, String> filters) {
        return orderService.findAllFiltered(filters).getContent();
    }

    @PostMapping
    public ResponseEntity<String> postOrder(@RequestBody OrderCreateDTO orderCreateDTO) {
        var id = orderService.save(orderCreateDTO);
        return new ResponseEntity<>(String.valueOf(id), HttpStatus.CREATED);
    }

    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public ResponseEntity<String> importOrders(@RequestParam("file") MultipartFile file) throws Exception {
        var ids = orderService.parseAndSave(file);
        return new ResponseEntity<>(ids.toString(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable long id) {
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public OrderStatsDTO getStats() {
        return orderService.getStats();
    }
}
