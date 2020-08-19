package me.study.querydslsample.damain;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Builder
    public Account (Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
