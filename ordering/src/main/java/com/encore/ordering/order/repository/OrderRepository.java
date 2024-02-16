package com.encore.ordering.order.repository;

import com.encore.ordering.member.domain.Member;
import com.encore.ordering.order.domain.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Ordering, Long> {
    List<Ordering> findByMemberId(Long id);

}
