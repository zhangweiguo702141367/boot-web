package com.boot.entity.jpa;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 70214 on 2017/3/25.
 */
@Entity
@Table(name = "user")
public class User implements Serializable{
    private static final long serialVersionUID = 1L;
    //0:禁止登录
    public static final Long _0 = new Long(0);
    //1:有效
    public static final Long _1 = new Long(1);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nickname;
    private String email;
    private String pswd;
    @Column(name="create_time")
    private Long createTime;
    @Column(name = "last_login_time")
    private Date lastLoginTime;
    private Integer status;
    @JoinTable(name="user_role",//中间表的名称
            joinColumns={@JoinColumn(name="userId",referencedColumnName="id")},//中间表PRODUCT_ID字段关联PRODUCT的ID
            inverseJoinColumns={@JoinColumn(name="roleId",referencedColumnName="id")})//中间表CATEGORY_ID字段关联CATEGORY的ID
    @ManyToMany
    private List<Role> roles = new ArrayList<Role>();

    public User(){
        super();
    }
    public User(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.pswd = user.getPswd();
        this.createTime = user.getCreateTime();
        this.lastLoginTime = user.getLastLoginTime();
        this.status = user.getStatus();
    }

    public User(String nickname, String email, String pswd, Long createTime, Date lastLoginTime, Integer status) {
        this.nickname = nickname;
        this.email = email;
        this.pswd = pswd;
        this.createTime = createTime;
        this.lastLoginTime = lastLoginTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", pswd='" + pswd + '\'' +
                ", createTime=" + createTime +
                ", lastLoginTime=" + lastLoginTime +
                ", status=" + status +
                ", roles=" + roles +
                '}';
    }
}
