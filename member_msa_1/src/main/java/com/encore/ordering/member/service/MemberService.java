package com.encore.ordering.member.service;

import com.encore.ordering.member.domain.Address;
import com.encore.ordering.member.domain.Member;
import com.encore.ordering.member.domain.Role;
import com.encore.ordering.member.dto.LoginReqDto;
import com.encore.ordering.member.dto.MemberCreateReqDto;
import com.encore.ordering.member.dto.MemberResponseDto;
import com.encore.ordering.member.repository.MemberRepository;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.encore.ordering.member.dto.MemberResponseDto.toMemberResponseDto;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member create(MemberCreateReqDto memberCreateReqDto) {
        if(memberRepository.findByEmail(memberCreateReqDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("같은 이메일의 회원이 존재합니다!");
        }
        memberCreateReqDto.setPassword(passwordEncoder.encode(memberCreateReqDto.getPassword()));
        Member member = Member.toEntity(memberCreateReqDto);
        return memberRepository.save(member);
    }

    //    이메일과 패스워드만 체크 (DB조회해야함)
    public Member login(LoginReqDto loginReqDto) throws IllegalArgumentException {
//        #1. 이메일 존재 여부 체크
        Member member = memberRepository.findByEmail(loginReqDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User with the given email does not exist."));
//        #2. 패스워드 일치 여부 체크
        if (!passwordEncoder.matches(loginReqDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("Password does not match the given email.");
        }
        return member;
    }

    public MemberResponseDto findMyInfo(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("일치하는 이메일의 회원이 없어요!"));
        return toMemberResponseDto(member);
    }

    public List<MemberResponseDto> findAll() {
        List<Member> members = memberRepository.findAll();
        return members.stream().map(m -> MemberResponseDto.toMemberResponseDto(m)).collect(Collectors.toList());
    }

    public MemberResponseDto findById(Long id){
        Member member = memberRepository.findById(id) .orElseThrow(() -> new EntityNotFoundException("일치하는 ID의 회원이 없어요!"));
        return toMemberResponseDto(member);
    }

    public MemberResponseDto findByEmail(String email) {
        Member member = memberRepository.findByEmail(email) .orElseThrow(() -> new EntityNotFoundException("일치하는 이메일의 회원이 없어요!"));
        return toMemberResponseDto(member);
    }
}
