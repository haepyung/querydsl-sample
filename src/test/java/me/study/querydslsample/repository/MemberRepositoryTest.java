package me.study.querydslsample.repository;

import static me.study.querydslsample.damain.QMember.member;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.persistence.EntityManager;
import me.study.querydslsample.damain.Member;
import me.study.querydslsample.damain.Team;
import me.study.querydslsample.dto.MemberSearchCondition;
import me.study.querydslsample.dto.MemberTeamDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

  @Autowired
  EntityManager em;

  @Autowired
  MemberRepository memberRepository;

  @Test
  void searchSimple() {
    //given
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

    //when
    MemberSearchCondition condition = new MemberSearchCondition();
    PageRequest pageRequest = PageRequest.of(0, 3);
    Page<MemberTeamDto> result = memberRepository.searchPageSimple(condition, pageRequest);

    //then
    assertEquals(result.getSize(), 3);
  }

  @Test
  void querydslPredicateExecutorTest() {
    //given
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

    //given
    Iterable<Member> result = memberRepository.findAll(member.age.between(10, 40)
        .and(member.username.eq("member1")));

    //when & then
    for (Member member : result) {
      System.out.println("member:: " + member);
    }
  }

}