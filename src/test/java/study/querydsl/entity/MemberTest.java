package study.querydsl.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Commit
class MemberTest {

    @Autowired
    EntityManager em;

    @Test
    public void MemberTest(){
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
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

        em.flush(); // 영속성컨택스트에 존재했던 것들을 쿼리화해서 날림.
        em.clear(); // 영속성컨텍스트 데이터 캐쉬를 다 날리기 때문에 깔끔해진다.

        List<Member> members = em.createQuery("select m from Member m",Member.class).getResultList();

        for(Member member : members){
            Assertions.assertThat(member1.getTeam()).isEqualTo(teamA);
        }
    }




}