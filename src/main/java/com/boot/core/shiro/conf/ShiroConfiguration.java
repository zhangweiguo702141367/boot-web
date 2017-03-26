package com.boot.core.shiro.conf;

import com.boot.core.shiro.CustomShiroSessionDAO;
import com.boot.core.shiro.cache.JedisShiroSessionRepository;
import com.boot.core.shiro.cache.impl.CustomShiroCacheManager;
import com.boot.core.shiro.session.CustomSessionManager;
import com.boot.core.shiro.tooken.MyShiroRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.session.mgt.ExecutorServiceSessionValidationScheduler;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.SessionValidationScheduler;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by 70214 on 2017/3/25.
 */
@Configuration
public class ShiroConfiguration {
    private static Logger logger = LoggerFactory.getLogger(ShiroConfiguration.class);

    /**
     * 安全管理器
     * @return
     */
    @Bean
    public DefaultWebSecurityManager securityManager(){
        DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
        //设置realm.
        securityManager.setRealm(myShiroRealm());
        //注入session管理器
        securityManager.setSessionManager(sessionManager());
        //注入缓存管理器;
        securityManager.setCacheManager(cacheManager());//这个如果执行多次，也是同样的一个对象;
        //注入记住我管理器;
        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }
    @Bean
    public MyShiroRealm myShiroRealm(){
       MyShiroRealm myShiroRealm =  new MyShiroRealm();
       return myShiroRealm;
    }
    @Bean
    public SessionManager sessionManager(){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionValidationInterval(1800000L);
        sessionManager.setGlobalSessionTimeout(1800000L);
        sessionManager.setSessionDAO(customShiroSessionDAO());
        sessionManager.setSessionListeners();
        sessionManager.setSessionValidationScheduler(sessionValidationScheduler());
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionIdCookie(sessionIdCookie());
        return sessionManager;
    }
    @Bean
    public CacheManager cacheManager(){
        CustomShiroCacheManager cacheManager = new CustomShiroCacheManager();
        return cacheManager;
    }
    @Bean
    public RememberMeManager rememberMeManager(){
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        rememberMeManager.setCipherKey();
        rememberMeManager.setCookie(simpleCookie());
    }
    //Shiro生命周期处理器
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        LifecycleBeanPostProcessor lifecycleBeanPostProcessor =  new LifecycleBeanPostProcessor();
        return lifecycleBeanPostProcessor;
    }
    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean(){
        MethodInvokingFactoryBean methodInvokingFactoryBean =  new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setArguments(securityManager());
        methodInvokingFactoryBean.setStaticMethod(SecurityUtils.setSecurityManager());
    }
    @Bean
    public SimpleCookie simpleCookie(){
        SimpleCookie rememberCookie =  new SimpleCookie("v_v-re-baidu");
        rememberCookie.setHttpOnly(true);
        rememberCookie.setMaxAge(2592000);
        return rememberCookie;
    }
    @Bean
    public SessionIdGenerator sessionIdGenerator(){
        JavaUuidSessionIdGenerator sessionIdGenerator =   new JavaUuidSessionIdGenerator();
        return sessionIdGenerator;
    }
    @Bean
    public SimpleCookie sessionIdCookie(){
        SimpleCookie sessionIdCookie =  new SimpleCookie("v_v-s-baidu");
        sessionIdCookie.setHttpOnly(true);
        sessionIdCookie.setMaxAge(-1);
        return sessionIdCookie;
    }
    @Bean
    public CustomShiroSessionDAO customShiroSessionDAO(){
        CustomShiroSessionDAO customShiroSessionDAO = new CustomShiroSessionDAO();
        customShiroSessionDAO.setShiroSessionRepository(jedisShiroSessionRepository());
        customShiroSessionDAO.setSessionIdGenerator(sessionIdGenerator());
        return customShiroSessionDAO;
    }
    //手动操作Session管理Session
    @Bean
    public CustomSessionManager customSessionManager(){
        CustomSessionManager customSessionManager = new CustomSessionManager();
        customSessionManager.setCustomShiroSessionDAO(customShiroSessionDAO());
        customSessionManager.setShiroSessionRepository(jedisShiroSessionRepository());
        return customSessionManager;
    }
    @Bean
    public JedisShiroSessionRepository jedisShiroSessionRepository(){
        JedisShiroSessionRepository jedisShiroSessionRepository  = new JedisShiroSessionRepository();
        jedisShiroSessionRepository.setJedisManager();
        return jedisShiroSessionRepository;
    }
    @Bean
    public SessionValidationScheduler sessionValidationScheduler(){
        ExecutorServiceSessionValidationScheduler sessionValidationScheduler = new ExecutorServiceSessionValidationScheduler();
        sessionValidationScheduler.setInterval(18000000);
        sessionValidationScheduler.setSessionManager(sessionManager());
        return sessionValidationScheduler;
    }
}
