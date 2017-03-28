package com.boot.core.shiro.tooken;

import com.boot.core.shiro.tooken.manager.TokenManager;
import com.boot.entity.jpa.Permission;
import com.boot.entity.jpa.Role;
import com.boot.entity.jpa.User;
import com.boot.repository.jpa.IUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by 70214 on 2017/3/25.
 */
public class MyShiroRealm extends AuthorizingRealm {
    @Autowired
    IUserService userService;
    public MyShiroRealm(){
        super();
    }

    /**
     *  认证信息，主要针对用户登录，
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection authcToken) {
        System.out.print("enter doGetAuthorizationInfo");
        Long userId = TokenManager.getUserId();
        SimpleAuthorizationInfo info =  new SimpleAuthorizationInfo();
        //根据用户ID查询角色（role），放入到Authorization里。
        User user = userService.findById(userId);
        List<Role> roles = user.getRoles();
        Set<String> addroles = new HashSet<String>();
        Set<String> permissions = new HashSet<String>();
        for (Role role:roles) {
            addroles.add(role.getType());
            List<Permission> permissions1 = role.getPermissions();
            for (Permission permission:permissions1) {
                permissions.add(permission.getUrl());
            }
        }
        info.setRoles(addroles);
        //根据用户ID查询权限（permission），放入到Authorization里。
//        Set<String> permissions = permissionService.findPermissionByUserId(userId);
        info.setStringPermissions(permissions);
        return info;
    }


    /**
     * 授权
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        System.out.print("enter doGetAuthenticationInfo");
        ShiroToken token = (ShiroToken) authcToken;
        User user = userService.findByEmailAndPswd(token.getUsername(),token.getPswd());
        if(null == user){
            throw new AccountException("帐号或密码不正确！");
            /**
             * 如果用户的status为禁用。那么就抛出<code>DisabledAccountException</code>
             */
        }else if(User._0.equals(user.getStatus())){
            throw new DisabledAccountException("帐号已经禁止登录！");
        }else{
            //更新登录时间 last login time
            user.setLastLoginTime(new Date());
//            userService.updateByPrimaryKeySelective(user);
        }
        return new SimpleAuthenticationInfo(user,user.getPswd(), getName());
    }
    /**
     * 清空当前用户权限信息
     */
    public  void clearCachedAuthorizationInfo() {
        PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
        SimplePrincipalCollection principals = new SimplePrincipalCollection(
                principalCollection, getName());
        super.clearCachedAuthorizationInfo(principals);
    }
    /**
     * 指定principalCollection 清除
     */
    public void clearCachedAuthorizationInfo(PrincipalCollection principalCollection) {
        SimplePrincipalCollection principals = new SimplePrincipalCollection(
                principalCollection, getName());
        super.clearCachedAuthorizationInfo(principals);
    }
}
