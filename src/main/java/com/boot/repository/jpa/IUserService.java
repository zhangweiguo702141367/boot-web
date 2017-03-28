package com.boot.repository.jpa;

import com.boot.entity.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Administrator on 2017/3/28.
 */
public interface IUserService extends JpaRepository<User, Integer> {
    User findById(Long id);
    User findByEmailAndPswd(String email,String pswd);
}
