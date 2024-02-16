package com.encore.ordering.member.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Embeddable;

@Embeddable //해당 객체를 다른 객체에 embed (삽입)할 수 있다라는 의미
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Address {
    private String city;
    private String street;
    private String zipcode;
}
