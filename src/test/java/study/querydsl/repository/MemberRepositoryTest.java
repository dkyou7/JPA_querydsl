package study.querydsl.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

}