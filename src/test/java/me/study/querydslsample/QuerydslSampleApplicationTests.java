package me.study.querydslsample;

import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import me.study.querydslsample.damain.Account;
import me.study.querydslsample.damain.QAccount;
import me.study.querydslsample.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class QuerydslSampleApplicationTests {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    EntityManager em;

    @Test
    void contextLoads() {
        System.out.println();
        Account account = Account.builder().id(1L).name("TEST").build();
        accountRepository.save(account);

        JPAQueryFactory query = new JPAQueryFactory(em);
        QAccount qAccount = QAccount.account;
        Account result = query.selectFrom(qAccount)
                .where(qAccount.name.eq("TEST"))
                .fetchOne();

        Assertions.assertEquals(account.getId(), result.getId());
    }
}
