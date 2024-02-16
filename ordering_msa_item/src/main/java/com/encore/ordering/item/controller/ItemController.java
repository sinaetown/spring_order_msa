package com.encore.ordering.item.controller;

import com.encore.ordering.common.CommonResponse;
import com.encore.ordering.item.domain.Item;
import com.encore.ordering.item.dto.ItemReqDto;
import com.encore.ordering.item.dto.ItemResDto;
import com.encore.ordering.item.dto.ItemSearchDto;
import com.encore.ordering.item.dto.ItemUpdateQuantityDto;
import com.encore.ordering.item.service.ItemService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.core.io.Resource;

import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    //    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/item/create")
    public ResponseEntity<CommonResponse> itemCreate(ItemReqDto itemReqDto) {
        Item item = itemService.create(itemReqDto);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.CREATED,
                "Item successfully created!", item.getId()), HttpStatus.CREATED);
    }

    @GetMapping("/items")
    public ResponseEntity<List<ItemResDto>> items(ItemSearchDto itemSearchDto, Pageable pageable) {
        List<ItemResDto> itemResDtos = itemService.findAll(itemSearchDto, pageable);
        return new ResponseEntity<>(itemResDtos, HttpStatus.OK);
    }

    @GetMapping("/item/{id}/image")
    public ResponseEntity<Resource> getImage(@PathVariable Long id) {
        Resource resource = itemService.getImage(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    //    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/item/{id}/update")
    public ResponseEntity<CommonResponse> itemUpdate(@PathVariable Long id, ItemReqDto itemReqDto) {
        Item item = itemService.update(id, itemReqDto);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK,
                "item successfully updated", item.getId()), HttpStatus.OK);
    }

    //    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/item/{id}/delete")
    public ResponseEntity<CommonResponse> itemDelete(@PathVariable Long id, ItemReqDto itemReqDto) {
        Item item = itemService.delete(id);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK,
                "item successfully deleted!", item.getId()),
                HttpStatus.OK);
    }

    @PostMapping("/item/updateQuantity")
    public ResponseEntity<CommonResponse> itemQuantityUpdate(@RequestBody List<ItemUpdateQuantityDto> itemUpdateQuantityDtos) {
        itemService.updateQuantity(itemUpdateQuantityDtos);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK,
                "item quantity updated", null), HttpStatus.OK);
    }

    @GetMapping("item/{id}")
    public ResponseEntity<ItemResDto> itemFindById(@PathVariable Long id) {
        ItemResDto itemResDto = itemService.findById(id);
        return new ResponseEntity<>(itemResDto, HttpStatus.OK);
    }
}
