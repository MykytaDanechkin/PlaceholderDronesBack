package com.mykyda.placholderdrones.app.service;

import com.mykyda.placholderdrones.app.DTO.OrderCreateDTO;
import com.mykyda.placholderdrones.app.database.entity.KitType;
import com.mykyda.placholderdrones.app.database.entity.Order;
import com.mykyda.placholderdrones.app.database.entity.OrderStatus;
import com.mykyda.placholderdrones.app.database.entity.PaymentStatus;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final MailService mailService;

    private final GeoService geoService;

    @Value("${placeholder-drones.search.filtered-search-page-size}")
    private int FILTERED_SEARCH_PAGE_SIZE;

    @Transactional
    public void save(OrderCreateDTO dto) {

        if (!geoService.validateNewYork(
                dto.getLatitude().doubleValue(),
                dto.getLongitude().doubleValue())) {

            throw new IllegalArgumentException("Location must be inside New York state");
        }

        double distance = geoService.calculateDistance(
                dto.getLatitude().doubleValue(),
                dto.getLongitude().doubleValue()
        );

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .receiverEmail(dto.getEmail())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .kitType(dto.getKitType())
                .orderStatus(OrderStatus.ORDERED)
                .paymentStatus(PaymentStatus.NOT_PAID)
                .distance(BigDecimal.valueOf(distance))
                .build();

        orderRepository.save(order);
        mailService.sendOrderConfirmationEmail(order.getId(), dto.getEmail());
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
//        if (filters.containsKey("paymentStatus")) {
//            spec = spec.and(OrderSpecification
//                    .hasPaymentStatus(PaymentStatus.valueOf(filters.get("paymentStatus").toUpperCase())));
//        }
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
    public List<Order> findAllByReceiverEmail(String email) {
        return orderRepository.findAllByReceiverEmail(email);
    }

    @Transactional(readOnly = true)
    public Order findById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No order with id " + id));
    }

    @Transactional
    public void deleteById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No order with id " + id));

        orderRepository.delete(order);
    }

    @Transactional
    public void setPaid(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No order with id " + id));
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setOrderStatus(OrderStatus.ORDERED);
        mailService.sendPaymentSuccessConfirmation(
                order.getId(),
                order.getReceiverEmail()
        );
    }
}
