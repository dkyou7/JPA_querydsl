package study.querydsl.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;  // shift + f6하면 전체 다 바꿔준다.

    @Test
    public void basicTest(){
        Member mem =  new Member("member1",10);
        memberRepository.save(mem);

        Member findMember = memberRepository.findById(mem.getId()).get();

        assertThat(findMember).isEqualTo(mem);

        List<Member> result1 = memberRepository.findAll();
        assertThat(result1).containsExactly(mem);

        List<Member> result2 = memberRepository.findByUsername("member1");
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

        List<MemberTeamDto> memberTeamDtos = memberRepository.searchByWhereParam(memberSearchCondition);
        assertThat(memberTeamDtos).extracting("username").contains("member4");
    }
    @Test
    public void searchSimplePageTest(){
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
        PageRequest pageRequest = PageRequest.of(0, 3);

        // 페이징 쿼리나 limit 이 들어가주는 것이 좋다. 장사 잘될 경우 조회 쿼리를 다 가져오기 때문이다.

        Page<MemberTeamDto> memberTeamDtos = memberRepository.searchPageSimple(memberSearchCondition,pageRequest);

        assertThat(memberTeamDtos.getSize()).isEqualTo(3);
        assertThat(memberTeamDtos.getContent()).extracting("username").containsExactly("member1","member2","member3");
    }

    @Test
    public void querydslPredicateExecutorTest(){
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

        Iterable<Member> result = memberRepository.findAll(member.age.between(10, 40).and(member.username.eq("member1")));
        for (Member findMember: result){
            System.out.println("findMember = " + findMember);
        }
        // join이 안됨.
        // client가 querydsl 에 의존한다.
        // 실무에 쓰기에 적합하지 않다.
    }
}