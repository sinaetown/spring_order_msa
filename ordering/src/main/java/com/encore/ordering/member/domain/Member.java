package com.encore.ordering.member.domain;

import com.encore.ordering.member.domain.Address;
import com.encore.ordering.member.dto.MemberCreateReqDto;
import com.encore.ordering.member.dto.MemberResponseDto;
import com.encore.ordering.order.domain.Ordering;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Embedded //@Embeddable 어노테이션이 있는 객체를 embed 받는다 -> 장점 : null처리가 용이
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "member")
    @Setter
    private List<Ordering> orderings;

    @CreationTimestamp
    private LocalDateTime createdTime;

    @UpdateTimestamp
    private LocalDateTime updatedTime;

    public static Member toEntity(MemberCreateReqDto memberCreateReqDto) {
        Address address = new Address(memberCreateReqDto.getCity(),
                memberCreateReqDto.getStreet(),
                memberCreateReqDto.getZipcode());

        Member member = Member.builder()
                .name(memberCreateReqDto.getName())
                .email(memberCreateReqDto.getEmail())
                .address(address)
                .password(memberCreateReqDto.getPassword())
                .role(Role.USER).build();
        
        return member;
    }
}
