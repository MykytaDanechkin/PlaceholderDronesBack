package com.mykyda.placholderdrones.app.service;

import com.mykyda.placholderdrones.app.DTO.OrderCreateDTO;
import com.mykyda.placholderdrones.app.DTO.OrderStatsDTO;
import com.mykyda.placholderdrones.app.database.entity.KitType;
import com.mykyda.placholderdrones.app.database.entity.Order;
import com.mykyda.placholderdrones.app.database.entity.OrderStatus;
import com.mykyda.placholderdrones.app.database.repository.OrderRepository;
import com.mykyda.placholderdrones.app.database.specification.OrderSpecification;
import com.mykyda.placholderdrones.app.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final CountyGeoLoader countyGeoLoader;

    @Value("${placeholder-drones.search.filtered-search-page-size}")
    private int FILTERED_SEARCH_PAGE_SIZE;

    @Transactional
    public long save(OrderCreateDTO dto) {

        var county = countyGeoLoader.findCounty(dto.getLongitude().doubleValue(), dto.getLatitude().doubleValue());
        var tax = county.getTaxRate();

        var subtotal = dto.getKitType().getSubtotal();
        var taxAmount = subtotal * tax.doubleValue();

        Order order = Order.builder()
                .receiverEmail(dto.getEmail())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .kitType(dto.getKitType())
                .orderStatus(OrderStatus.WAITING_FOR_PAYMENT)
                .subtotal(subtotal)
                .compositeTax(tax.doubleValue())
                .taxAmount(taxAmount)
                .totalAmount(subtotal + taxAmount)
                .build();

        var savedOrder = orderRepository.save(order);
        return savedOrder.getId();
    }

    @Transactional(readOnly = true)
    public Page<Order> findAllFiltered(Map<String, String> filters) {
        Specification<Order> spec = Specification.allOf();

        if (filters.containsKey("before")) {
            spec = spec.and(OrderSpecification
                    .placeBefore(LocalDate.parse(filters.get("before"))));
        }
        if (filters.containsKey("after")) {
            spec = spec.and(OrderSpecification
                    .placedAfter(LocalDate.parse(filters.get("after"))));
        }
        if (filters.containsKey("orderStatus")) {
            spec = spec.and(OrderSpecification
                    .hasOrderStatus(OrderStatus.valueOf(filters.get("orderStatus").toUpperCase())));
        }
        if (filters.containsKey("email")) {
            spec = spec.and(OrderSpecification
                    .hasEmail(filters.get("email")));
        }
        if (filters.containsKey("kitType")) {
            spec = spec.and(OrderSpecification
                    .hasKitType(KitType.valueOf(filters.get("kitType").toUpperCase())));
        }

        var page = 0;
        if (filters.get("page") != null && !filters.get("page").isEmpty()) {
            page = Integer.parseInt(filters.get("page"));
        }

        return orderRepository.findAll(spec, PageRequest.of(page, FILTERED_SEARCH_PAGE_SIZE));
    }

    @Transactional(readOnly = true)
    public Order findById(long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No order with id " + id));
    }

    @Transactional
    public void deleteById(long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No order with id " + id));

        orderRepository.delete(order);
    }

    @Transactional
    public List<Long> parseAndSave(MultipartFile file) throws Exception {

        List<Order> result = new ArrayList<>();
        int skipped = 0;

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {

                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                try {

                    var columns = line.split(",");

                    var formatter =
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");

                    var ldt = LocalDateTime.parse(columns[3], formatter);
                    var timestamp = Timestamp.from(ldt.toInstant(ZoneOffset.UTC));

                    var subtotal = (int) Float.parseFloat(columns[4]);
                    var latitude = BigDecimal.valueOf(Double.parseDouble(columns[2]));
                    var longitude = BigDecimal.valueOf(Double.parseDouble(columns[1]));

                    var county = countyGeoLoader
                            .findCounty(longitude.doubleValue(), latitude.doubleValue());

                    var tax = county.getTaxRate();
                    var taxAmount = subtotal * tax.doubleValue();

                    Order order = Order.builder()
                            .longitude(longitude)
                            .latitude(latitude)
                            .timestamp(timestamp)
                            .subtotal(subtotal)
                            .compositeTax(tax.doubleValue())
                            .taxAmount(taxAmount)
                            .totalAmount(subtotal + taxAmount)
                            .kitType(KitType.fromSubtotal(subtotal))
                            .orderStatus(OrderStatus.ORDERED)
                            .build();

                    result.add(order);

                } catch (Exception e) {
                    skipped++;
                    log.debug("Skipping invalid row: {}", line);
                }
            }
        }

        if (!result.isEmpty()) {
            var saved = orderRepository.saveAll(result);
            log.info("Import finished. Saved: {}, Skipped: {}", saved.size(), skipped);
            return saved.stream().map(Order::getId).toList();
        }

        return List.of();
    }

    @Transactional(readOnly = true)
    public OrderStatsDTO getStats() {
        return OrderStatsDTO.builder()
                .totalOrders(orderRepository.count())
                .totalTax(orderRepository.sumTax())
                .totalPending(orderRepository.getSumOrdersByOrderStatus(OrderStatus.WAITING_FOR_PAYMENT))
                .build();
    }
}
