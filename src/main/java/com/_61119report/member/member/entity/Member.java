package com._61119report.member.member.entity;

import com._61119report.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * 간이 회원. (인증/암호화 없는 간이 형식)
 */
@Entity
public class Member extends BaseEntity {

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, length = 30)
    private String password;

    @Column(nullable = false, length = 30)
    private String name;

    protected Member() {
    }

    public Member(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}
