package study.querydsl.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void basicTest(){
        Member mem =  new Member("member1",10);
        memberJpaRepository.save(mem);

        Member findMember = memberJpaRepository.findById(mem.getId()).get();

        assertThat(findMember).isEqualTo(mem);

        List<Member> result1 = memberJpaRepository.findAll();
        assertThat(result1).containsExactly(mem);

        List<Member> result2 = memberJpaRepository.findByUsername("member1");
        assertThat(result2).containsExactly(mem);
    }
    @Test
    public void basicTest_Querydsl(){
        Member mem =  new Member("member1",10);
        memberJpaRepository.save(mem);

        Member findMember = memberJpaRepository.findById(mem.getId()).get();

        assertThat(findMember).isEqualTo(mem);

        List<Member> result1 = memberJpaRepository.findAll_Querydsl();
        assertThat(result1).containsExactly(mem);

        List<Member> result2 = memberJpaRepository.findByUsername_Querydsl("member1");
        assertThat(result2).containsExactly(mem);
    }

    @Test
    public void searchTest(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1",10,teamA);
        Member member2 = new Member("member2",20,teamA);
        Member member3 = new Member("member3",30,teamB);
        Member member4 = new Member("member4",40,teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition memberSearchCondition = new MemberSearchCondition();
        memberSearchCondition.setAgeGoe(35);
        memberSearchCondition.setAgeLoe(45);
        memberSearchCondition.setTeamName("teamB");

        // 페이징 쿼리나 limit 이 들어가주는 것이 좋다. 장사 잘될 경우 조회 쿼리를 다 가져오기 때문이다.

        List<MemberTeamDto> memberTeamDtos = memberJpaRepository.searchByBuilder(memberSearchCondition);
        assertThat(memberTeamDtos).extracting("username").contains("member4");
    }

    @Test
    public void searchTestByWhere(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1",10,teamA);
        Member member2 = new Member("member2",20,teamA);
        Member member3 = new Member("member3",30,teamB);
        Member member4 = new Member("member4",40,teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition memberSearchCondition = new MemberSearchCondition();
        memberSearchCondition.setAgeGoe(35);
        memberSearchCondition.setAgeLoe(45);
        memberSearchCondition.setTeamName("teamB");

        // 페이징 쿼리나 limit 이 들어가주는 것이 좋다. 장사 잘될 경우 조회 쿼리를 다 가져오기 때문이다.

        List<MemberTeamDto> memberTeamDtos = memberJpaRepository.searchByWhereParam(memberSearchCondition);
        assertThat(memberTeamDtos).extracting("username").contains("member4");
    }
}