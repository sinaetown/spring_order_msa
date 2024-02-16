package com.encore.ordering.order.dto;

import lombok.Data;

@Data

public class OrderReqDto {
//    #방법 1
//    private List<Long> itemIds;
//    private List<Long> counts;
//    private Long memberId;

    //    #방법 2
    private Long itemId;
    private int count;
}

//방법 1 예시 데이터
/*
 * {
 * "itemIds" : [1,2],
 * "counts" : [10,20]
 * }
 * */

//방법 2 예시데이터
//[
//     {"itemId":1,"count":1},
//     {"itemId":2,"count":2}
//]
