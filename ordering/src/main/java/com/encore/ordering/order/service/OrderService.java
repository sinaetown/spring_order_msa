package com.encore.ordering.order.service;

import com.encore.ordering.item.domain.Item;
import com.encore.ordering.item.repository.ItemRepository;
import com.encore.ordering.member.domain.Member;
import com.encore.ordering.member.domain.Role;
import com.encore.ordering.member.repository.MemberRepository;
import com.encore.ordering.order.domain.OrderStatus;
import com.encore.ordering.order.domain.Ordering;
import com.encore.ordering.order.dto.OrderReqDto;
import com.encore.ordering.order.dto.OrderResDto;
import com.encore.ordering.order.repository.OrderRepository;
import com.encore.ordering.order_item.domain.OrderItem;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    public OrderService(OrderRepository orderRepository, MemberRepository memberRepository, ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.itemRepository = itemRepository;
    }

    public Ordering create(List<OrderReqDto> orderReqDtos) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()
                -> new EntityNotFoundException("Member email is invalid."));
        Ordering ordering = Ordering.builder().member(member).build();
//        Ordering 객체가 생성될 때 OrderingItem 객체도 함께 생성 : cascading
        for (OrderReqDto dto : orderReqDtos) {
            Item item = itemRepository.findById(dto.getItemId()).orElseThrow(()
                    -> new EntityNotFoundException("invalid item ID"));
            OrderItem orderItem = OrderItem
                    .builder()
                    .quantity(dto.getCount())
                    .item(item)
                    .ordering(ordering)
                    .build();
            ordering.getOrderItems().add(orderItem);
            if (item.getStockQuantity() - dto.getCount() < 0) {
                throw new IllegalArgumentException("out of stock!");
            }
            orderItem.getItem().updateStockQuantity(item.getStockQuantity() - dto.getCount()); //dirty checking
        }
        return orderRepository.save(ordering);
    }

    //    Checked exception이면 transaction 안 탐
//    수정된 작업 (새롭게 추가된 게 아니라) .save() 하지 않아도 됨
    public Ordering cancel(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Ordering ordering = orderRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Order number is invalid."));
        if (ordering.getOrderStatus() == OrderStatus.CANCELED) {
            throw new IllegalArgumentException("The order is already canceled.");
        }
        if (authentication.getAuthorities().contains((new SimpleGrantedAuthority("ROLE_ADMIN")))
                || ordering.getMember().getEmail().equals(email)) {
            ordering.cancelOrder();
            for (OrderItem o : ordering.getOrderItems()) {
                o.getItem().increaseStockQuantity(o.getItem().getStockQuantity() + o.getQuantity());
            }
        } else {
            throw new AccessDeniedException("You can only cancel your own order.");
        }

        return ordering;
    }

    public List<OrderResDto> findAll() {
        List<Ordering> orderings = orderRepository.findAll();
        return orderings.stream().map(o -> OrderResDto.toDto(o)).collect(Collectors.toList());
    }

    public List<OrderResDto> findByMember(Long id) {
        List<Ordering> orderings = orderRepository.findByMemberId(id);
        return orderings.stream().map(o -> OrderResDto.toDto(o)).collect(Collectors.toList());
    }

    public List<OrderResDto> findMyOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()
                -> new EntityNotFoundException("Member email is invalid."));
        List<Ordering> orderings = orderRepository.findByMemberId(member.getId());
        return orderings.stream().map(o -> OrderResDto.toDto(o)).collect(Collectors.toList());
    }
}
