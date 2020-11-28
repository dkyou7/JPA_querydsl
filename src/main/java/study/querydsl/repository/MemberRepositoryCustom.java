package study.querydsl.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import java.util.List;
import org.springframework.data.domain.Page;

@Repository
public interface MemberRepositoryCustom {
    List<MemberTeamDto> searchByWhereParam(MemberSearchCondition condition);
    Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable);
    Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable);
    Page<MemberTeamDto> searchPageComplex_count(MemberSearchCondition condition, Pageable pageable);
}
