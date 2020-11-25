package study.querydsl.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;

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
}