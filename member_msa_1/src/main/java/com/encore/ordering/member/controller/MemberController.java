package com.encore.ordering.member.controller;

import com.encore.ordering.common.CommonResponse;
import com.encore.ordering.member.domain.Member;
import com.encore.ordering.member.dto.LoginReqDto;
import com.encore.ordering.member.dto.MemberCreateReqDto;
import com.encore.ordering.member.dto.MemberResponseDto;
import com.encore.ordering.member.service.MemberService;
import com.encore.ordering.securities.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MemberController {
    private final MemberService memberService;
    //    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberController(MemberService memberService,
                            JwtTokenProvider jwtTokenProvider
    ) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
//        this.orderService = orderService;
    }

    //    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/members")
    public List<MemberResponseDto> members() {
        return memberService.findAll();
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/member/{id}/orders")
//    public List<OrderResDto> orderByMem(@PathVariable Long id) {
//        return orderService.findByMember(id);
//    }
//

//    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.username")
//   => 다수에게 권한 부여할 수도 있다!

    //    @GetMapping("/member/myorders")
//    public List<OrderResDto> myOrders() {
//        return orderService.findMyOrders();
//    }
    @GetMapping("/member/{id}") //Authentication 객체에서 바로 회원 관련 정보를 빼내겠다는
    public MemberResponseDto findById(@PathVariable Long id) {
        return memberService.findById(id);
    }

    @GetMapping("/member/findByEmail") //Authentication 객체에서 바로 회원 관련 정보를 빼내겠다는
    public MemberResponseDto findByEmail(@RequestParam String email) {
        return memberService.findByEmail(email);
    }

    @GetMapping("/member/myInfo") //Authentication 객체에서 바로 회원 관련 정보를 빼내겠다는
    public MemberResponseDto findMyInfo(@RequestHeader("myEmail") String email) {
        return memberService.findMyInfo(email);
    }

    @PostMapping("/member/create")
    public ResponseEntity<CommonResponse> memberCreate(@Valid @RequestBody MemberCreateReqDto memberCreateReqDto) {
        Member member = memberService.create(memberCreateReqDto);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.CREATED,
                "member successfully created!",
                member.getId()), HttpStatus.CREATED);
    }

    @PostMapping("/doLogin") //token을 return 해야함!
    public ResponseEntity<CommonResponse> memberLogin(@Valid @RequestBody LoginReqDto loginReqDto) {
        Member member = memberService.login(loginReqDto);
//        토큰 생성 로직
        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
        Map<String, Object> member_info = new HashMap<>();
        member_info.put("id", member.getId());
        member_info.put("token", jwtToken);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK,
                "login success!",
                member_info), HttpStatus.OK);
    }


}
