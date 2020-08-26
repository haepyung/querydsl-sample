package me.study.querydslsample;

import static me.study.querydslsample.damain.QMember.member;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import me.study.querydslsample.damain.Member;
import me.study.querydslsample.damain.QMember;
import me.study.querydslsample.damain.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

  @Autowired
  EntityManager em;

  JPAQueryFactory queryFactory;

  @BeforeEach
  void before() {

    queryFactory = new JPAQueryFactory(em);

    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    em.persist(teamA);
    em.persist(teamB);

    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 20, teamA);

    Member member3 = new Member("member3", 30, teamB);
    Member member4 = new Member("member4", 40, teamB);

    em.persist(member1);
    em.persist(member2);
    em.persist(member3);
    em.persist(member4);

    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("QueryDsl 시")
  void start() {
    //given
    Member findMember = em
        .createQuery("select m from Member m where m.username = :username", Member.class)
        .setParameter("username", "member1")
        .getSingleResult();

    //when, then JPQL
    assertEquals(findMember.getUsername(), "member1");

    //when, then querydsl
    Member findMember2 =
        queryFactory
            .selectFrom(member)
            .where(member.username.eq("member1")).fetchOne();

    assertEquals(findMember2.getUsername(), "member1");
  }

  @Test
  @DisplayName("검색")
  void searchOne() {
    //given
    //TYPE_1
    Member findMember = queryFactory.selectFrom(member)
        .where(member.username.eq("member1").and(member.age.eq(10)))
        .fetchOne();

    //TYPE_2
    Member findMember2 = queryFactory.selectFrom(member)
        .where(
            member.username.eq("member1"),
            member.age.eq(10))
        .fetchOne();

    //when &then
    assertEquals(findMember.getUsername(), "member1");

    /**
     *member.username.eq("member1") // username = 'member1'
     * member.username.ne("member1") //username != 'member1'
     * member.username.eq("member1").not() // username != 'member1'
     *
     * member.username.isNotNull() //이름이 is not null
     * member.age.in(10, 20) // age in (10,20)
     * member.age.notIn(10, 20) // age not in (10, 20)
     * member.age.between(10,30) //between 10, 30
     *
     * member.age.goe(30) // age >= 30
     * member.age.gt(30) // age > 30
     * member.age.loe(30) // age <= 30
     * member.age.lt(30) // age < 30
     *
     * member.username.like("member%") //like 검색
     * member.username.contains("member") // like ‘%member%’ 검색
     * member.username.startsWith("member") //like ‘member%’ 검색
     *
     */
  }

  @Test
  @DisplayName("검색")
  void resultFetch() {
    /**
     *
     * fetch() : 리스트 조회, 데이터 없으면 빈 리스트 반환
     * fetchOne() : 단 건 조회
     * 결과가 없으면 : null
     * 결과가 둘 이상이면 : com.querydsl.core.NonUniqueResultException
     * fetchFirst() : limit(1).fetchOne()
     * fetchResults() : 페이징 정보 포함, total count 쿼리 추가 실행
     * fetchCount() : count 쿼리로 변경해서 count 수 조회
     */

    List<Member> f = queryFactory.selectFrom(member).fetch();
    Member fetchOne = queryFactory.selectFrom(member).fetchOne();
    Member fetchFirst = queryFactory.selectFrom(QMember.member).fetchFirst();

    QueryResults<Member> results = queryFactory.selectFrom(member).fetchResults();

    long total = results.getTotal();
    List<Member> content = results.getResults();

    long totalCnt = queryFactory.selectFrom(member).fetchCount();
  }

  @Test
  void sort() {
    em.persist(new Member(null, 100));
    em.persist(new Member("member5", 100));
    em.persist(new Member("member6", 100));
    //given
    List<Member> result = queryFactory.selectFrom(member)
        .orderBy(member.age.desc(), member.username.asc().nullsLast())
        .fetch();

    //when
    Member member5 = result.get(0);
    Member member6 = result.get(1);
    Member memberNull = result.get(2);

    //then
    assertEquals(member5.getUsername(), "member5");
    assertEquals(member6.getUsername(), "member6");
    assertNull(memberNull.getUsername());
  }

  @Test
  void paging_1() {
    //given & when
    List<Member> fetch = queryFactory.selectFrom(member)
        .orderBy(member.username.desc())
        .offset(1)
        .limit(2)
        .fetch();

    //then
    assertEquals(fetch.size(), 2);
  }

  @Test
  void paging_2() {
    //given & when
    QueryResults<Member> results = queryFactory.selectFrom(member)
        .orderBy(member.username.desc())
        .offset(1)
        .limit(2)
        .fetchResults();

    //then
    assertEquals(results.getTotal(), 4);
    assertEquals(results.getLimit(), 2);
    assertEquals(results.getOffset(), 1);
    assertEquals(results.getResults().size(), 2);
  }

  @Test
  void aggregation() {
    //given
    List<Tuple> result = queryFactory.select(member.count(), member.age.sum()
        , member.age.avg(), member.age.max(), member.age.min())
        .from(member).fetch();

    //when
    Tuple tuple = result.get(0);

    //then
    assertEquals(tuple.get(member.count()), 4);
    assertEquals(tuple.get(member.age.sum()), 100);
    assertEquals(tuple.get(member.age.avg()), 25);
    assertEquals(tuple.get(member.age.max()), 40);
    assertEquals(tuple.get(member.age.min()), 10);
  }
}
