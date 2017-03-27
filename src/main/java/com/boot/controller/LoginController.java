package com.boot.controller;

import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 70214 on 2017/3/27.
 */
@RestController
public class LoginController {

    @GetMapping("/user")
    public String user(){
        return "hello";
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }
    @GetMapping("/success")
    public String success(){
        return "success";
    }
    @GetMapping("/unauthorized")
    public String unauthorized(){
        return "unauthorized";
    }
}
