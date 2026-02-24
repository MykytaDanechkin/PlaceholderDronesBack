package com.mykyda.placholderdrones.api;

import com.mykyda.placholderdrones.DTO.OrderCreateDTO;
import com.mykyda.placholderdrones.database.entity.Order;
import com.mykyda.placholderdrones.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public Order getById(@PathVariable UUID id) {
        return orderService.findById(id);
    }

    @GetMapping
    public List<Order> getAllFiltered(@RequestParam Map<String, String> filters) {
        return orderService.findAllFiltered(filters).getContent();
    }

    @PostMapping
    public ResponseEntity<String> postOrder(@RequestBody OrderCreateDTO orderCreateDTO) {
        orderService.save(orderCreateDTO);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> setPayed(@PathVariable UUID id) {
        orderService.setPaid(id);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable UUID id) {
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
