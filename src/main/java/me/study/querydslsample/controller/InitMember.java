package me.study.querydslsample.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitMember {

  /*
  private final InitMemberService initMemberService;

  @PostConstruct
  public void init() {
    initMemberService.init();
  }

  static class InitMemberService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void init() {
      Team teamA = new Team("teamA");
      Team teamB = new Team("teamB");
      em.persist(teamA);
      em.persist(teamB);

      for (int idx = 0; idx < 100; idx++) {
        Team selectedTeam = idx % 2 == 0 ? teamA : teamB;
        em.persist(new Member("member" + idx, idx, selectedTeam));
      }

    }
  }

   */

}
