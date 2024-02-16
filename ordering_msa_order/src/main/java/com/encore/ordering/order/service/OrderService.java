package com.encore.ordering.order.service;

import com.encore.ordering.common.CommonResponse;
import com.encore.ordering.order.controller.TestController;
import com.encore.ordering.order.domain.*;
import com.encore.ordering.order.dto.OrderReqDto;
import com.encore.ordering.order.dto.OrderResDto;
import com.encore.ordering.order.repository.OrderRepository;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    private final String MEMBER_API = "http://member-service/";
    private final String ITEM_API = "http://item-service/";


    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    public Ordering create(List<OrderReqDto> orderReqDtos, String email) {
        List<ItemUpdateQuantityDto> itemUpdateQuantityDtos = new ArrayList<>();
        MemberDto member = restTemplate.getForObject(
                MEMBER_API + "member/findByEmail?email=" + email,
                MemberDto.class);
        System.out.println(member + "here");
        Ordering ordering = Ordering.builder().memberId(member.getId()).build();
        for (OrderReqDto dto : orderReqDtos) {
            OrderItem orderItem = OrderItem
                    .builder()
                    .quantity(dto.getCount())
                    .itemId(dto.getItemId())
                    .ordering(ordering)
                    .build();
            ordering.getOrderItems().add(orderItem);
            String url = ITEM_API + "item/" + dto.getItemId();
            ResponseEntity<ItemDto> itemResponseEntity = restTemplate.getForEntity(url, ItemDto.class);

            if (itemResponseEntity.getBody().getStockQuantity() - dto.getCount() < 0) {
                throw new IllegalArgumentException("out of stock!");
            }
            int newQuantity = itemResponseEntity.getBody().getStockQuantity() - dto.getCount();
            ItemUpdateQuantityDto itemUpdateQuantityDto = new ItemUpdateQuantityDto();
            itemUpdateQuantityDto.setId(dto.getItemId());
            itemUpdateQuantityDto.setStockQuantity(newQuantity);
            itemUpdateQuantityDtos.add(itemUpdateQuantityDto);
        }
        Ordering ordering1 = orderRepository.save(ordering);
//        orderRepository.save(ordering)을 먼저 함으로써 위 코드에서 에러 발생 시, item 서비스를 호출하지 않으므로,
//        트랜잭션 문제 발생하지 않음
        String itemPatchUrl = ITEM_API + "item/updateQuantity";
        HttpEntity<List<ItemUpdateQuantityDto>> entity = new HttpEntity<>(itemUpdateQuantityDtos);
        ResponseEntity<CommonResponse> response = restTemplate.exchange(itemPatchUrl, HttpMethod.POST,
                entity, CommonResponse.class);
//        만약에 위 update이후에 추가적인 로직이 존재할 경우, transaction 이슈는 여전히 발생가능함
//        해결책으로 에러 발생할 가능성이 있는 코드 전체를 try, catch로 예외처리 이후에 catch에서 updateRollbackQuantity 호출
        return ordering1;
    }

    //    Checked exception이면 transaction 안 탐
//    수정된 작업 (새롭게 추가된 게 아니라) .save() 하지 않아도 됨
//    public Ordering cancel(Long id) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//        Ordering ordering = orderRepository.findById(id).orElseThrow(()
//                -> new EntityNotFoundException("Order number is invalid."));
//        if (ordering.getOrderStatus() == OrderStatus.CANCELED) {
//            throw new IllegalArgumentException("The order is already canceled.");
//        }
//        if (authentication.getAuthorities().contains((new SimpleGrantedAuthority("ROLE_ADMIN")))
//                || ordering.getMember().getEmail().equals(email)) {
//            ordering.cancelOrder();
//            for (OrderItem o : ordering.getOrderItems()) {
//                o.getItem().increaseStockQuantity(o.getItem().getStockQuantity() + o.getQuantity());
//            }
//        } else {
//            throw new AccessDeniedException("You can only cancel your own order.");
//        }
//
//        return ordering;
//    }

//    public List<OrderResDto> findAll() {
//        List<Ordering> orderings = orderRepository.findAll();
//        return orderings.stream().map(o -> OrderResDto.toDto(o)).collect(Collectors.toList());
//    }
//
//    public List<OrderResDto> findByMember(Long id) {
//        List<Ordering> orderings = orderRepository.findByMemberId(id);
//        return orderings.stream().map(o -> OrderResDto.toDto(o)).collect(Collectors.toList());
//    }
//
//    public List<OrderResDto> findMyOrders(Long memberID) {
//        List<Ordering> orderings = orderRepository.findByMemberId(memberID);
//        return orderings.stream().map(o -> OrderResDto.toDto(o)).collect(Collectors.toList());
//    }
}
