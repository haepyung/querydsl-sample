package me.study.querydslsample.repository;

import java.util.List;
import me.study.querydslsample.dto.MemberSearchCondition;
import me.study.querydslsample.dto.MemberTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

  List<MemberTeamDto> search(MemberSearchCondition condition);

  Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable);

  Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable);
}
