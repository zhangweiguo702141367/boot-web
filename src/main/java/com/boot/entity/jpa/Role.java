package com.boot.entity.jpa;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 70214 on 2017/3/25.
 */
@Entity
@Table(name = "role")
public class Role  implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String type;
    @ManyToMany(mappedBy="roles")
    private List<User> users = new ArrayList<User>();
    @JoinTable(name="role_permission",//中间表的名称
            joinColumns={@JoinColumn(name="roleId",referencedColumnName="id")},//中间表PRODUCT_ID字段关联PRODUCT的ID
            inverseJoinColumns={@JoinColumn(name="permissionId",referencedColumnName="id")})//中间表CATEGORY_ID字段关联CATEGORY的ID
    @ManyToMany
    private List<Permission> permissions = new ArrayList<Permission>();

    public Role(){
        super();
    }

    public Role(String name, String type, List<User> users, List<Permission> permissions) {
        this.name = name;
        this.type = type;
        this.users = users;
        this.permissions = permissions;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", users=" + users +
                ", permissions=" + permissions +
                '}';
    }
}
