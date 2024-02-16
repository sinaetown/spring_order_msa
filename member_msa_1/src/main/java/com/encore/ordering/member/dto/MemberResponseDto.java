package com.encore.ordering.member.dto;

import com.encore.ordering.member.domain.Address;
import com.encore.ordering.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponseDto {
    private Long id;
    private String name;
    private String email;
    private String city;
    private String street;
    private String zipcode;
    private int orderCount;

    public static MemberResponseDto toMemberResponseDto(Member member) {
        MemberResponseDtoBuilder memberResponseDtoBuilder = MemberResponseDto.builder();
        memberResponseDtoBuilder.id(member.getId());
        memberResponseDtoBuilder.name(member.getName());
        memberResponseDtoBuilder.email(member.getEmail());
//        memberResponseDtoBuilder.orderCount(member.getOrderings().size());
        Address address = member.getAddress();
        if (address != null) {
            memberResponseDtoBuilder.city(member.getAddress().getCity());
            memberResponseDtoBuilder.street(member.getAddress().getStreet());
            memberResponseDtoBuilder.zipcode(member.getAddress().getZipcode());
        }

        return memberResponseDtoBuilder.build();

    }
}
