package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before(){
        queryFactory = new JPAQueryFactory(em);
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
    }

    @Test
    public void startJPQL(){
        String qlString = "select m" +
                " from Member m" +
                " where m.username = :username";

        Member findMember = em.createQuery(qlString,Member.class)
                .setParameter("username","member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQueryDsl(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember member = QMember.member;

        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQueryDsl_v2(){
        // 전역 필드로 뺴주기.
        // JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQueryDsl_v3(){
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))    // and 조건 이용
                .fetchOne();

        Member findMember2 = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member2"),
                        member.age.eq(20))    // 쉼표 이용
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
        assertThat(findMember2.getUsername()).isEqualTo("member2");
        assertThat(findMember.getAge()).isEqualTo(10);
        assertThat(findMember2.getAge()).isEqualTo(20);
    }

    @Test
    public void fetchTestQuery(){
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        // 단건 조회
        Member findMember1 = queryFactory
                .selectFrom(member)
                .fetchOne();

        // 리스트여도 처음 하나만 조회
        Member findMember2 = queryFactory
                .selectFrom(member)
                .fetchFirst();

        // 페이징에서 사용
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();

        long total = results.getTotal();
        List<Member> members = results.getResults();

        // 갯수 조회
        Long count = queryFactory
                .selectFrom(member)
                .fetchCount();
    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순 (desc)
     * 2. 회원 이름 올림차순 (asc)
     * 단, 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     */
    @Test
    public void sortTest(){
        em.persist(new Member(null,100));
        em.persist(new Member("member5",100));
        em.persist(new Member("member6",100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(
                        member.age.eq(100)
                )
                .orderBy(
                        member.age.desc(),
                        member.username.asc().nullsLast()
                ).fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);
        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    @Test
    public void pagingTest(){
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.asc())
                .offset(1)
                .limit(2)
                .fetch();
        assertThat(result.get(0).getUsername()).isEqualTo("member2");
    }

    @Test
    public void pagingTest_2(){
        QueryResults<Member> queryResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.asc())
                .offset(1)
                .limit(2)
                .fetchResults();
        List<Member> result = queryResults.getResults();
        assertThat(result.get(0).getUsername()).isEqualTo("member2");
        assertThat(queryResults.getTotal()).isEqualTo(4);
        assertThat(queryResults.getLimit()).isEqualTo(2);
        assertThat(queryResults.getOffset()).isEqualTo(1);
    }

    @Test
    public void aggregations(){
        List<Tuple> fetch = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetch();
        Tuple tuple = fetch.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }
    @Test
    public void groubByTest(){
        List<Tuple> result = queryFactory
                .select(team.name, member.age.sum())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();
        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("TeamA");
        assertThat(teamA.get(member.age.sum())).isEqualTo(30);

        assertThat(teamB.get(team.name)).isEqualTo("TeamB");
        assertThat(teamB.get(member.age.sum())).isEqualTo(70);
    }

    @Test
    public void join(){
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("TeamA"))
                .fetch();

        assertThat(result).extracting("username")
                .containsExactly("member1","member2");
    }

    @Test
    public void join_on_filtering(){
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("TeamA"))
                // 동일.
//                .join(member.team,team)
//                .where(team.name.eq("TeamA"))
                .fetch();

        for(Tuple tuple: result){
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void join_on_no_relation(){
        em.persist(new Member("TeamA"));
        em.persist(new Member("TeamB"));
        em.persist(new Member("TeamC"));
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team)
                .on(member.username.eq(team.name))
                .fetch();

        for(Tuple tuple: result){
            System.out.println("tuple = " + tuple);
        }
    }
}
