package com.boot.core.shiro.conf;

import com.boot.core.shiro.CustomShiroSessionDAO;
import com.boot.core.shiro.cache.JedisShiroSessionRepository;
import com.boot.core.shiro.cache.impl.CustomShiroCacheManager;
import com.boot.core.shiro.filter.*;
import com.boot.core.shiro.listener.CustomSessionListener;
import com.boot.core.shiro.session.CustomSessionManager;
import com.boot.core.shiro.tooken.MyShiroRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.ExecutorServiceSessionValidationScheduler;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.SessionValidationScheduler;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.*;

/**
 * Created by 70214 on 2017/3/25.
 */
@Configuration
public class ShiroConfiguration {
    private static Logger logger = LoggerFactory.getLogger(ShiroConfiguration.class);

    @Bean
    public ShiroFilterFactoryBean shiroFilter(){
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager());
        shiroFilter.setLoginUrl();
        shiroFilter.setSuccessUrl();
        shiroFilter.setUnauthorizedUrl();
        Map<String,Filter> filters = new HashMap<String,Filter>();
        filters.put("login",loginFilter());
        filters.put("role",roleFilter());
        filters.put("simple",simpleAuthFilter());
        filters.put("permission",permissionFilter());
        filters.put("kickout",kickoutSessionFilter());
        shiroFilter.setFilters(filters);
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<String,String>();
        //配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了
        filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/login", "authc");
        shiroFilter.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilter;
    }
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
        List<SessionListener> listenerList = new ArrayList<SessionListener>();
        listenerList.add(customSessionListener());
        sessionManager.setSessionListeners(listenerList);
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
        methodInvokingFactoryBean.setArguments();
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
        sessionValidationScheduler.setSessionManager();
        return sessionValidationScheduler;
    }
    @Bean
    public CustomSessionListener customSessionListener(){
        CustomSessionListener customSessionListener = new CustomSessionListener();
        customSessionListener.setShiroSessionRepository(jedisShiroSessionRepository());
        return customSessionListener;
    }
    //拦截器
    @Bean
    public LoginFilter loginFilter(){
        return new LoginFilter();
    }
    @Bean
    public RoleFilter roleFilter(){
        return new RoleFilter();
    }
    @Bean
    public PermissionFilter permissionFilter(){
        return new PermissionFilter();
    }
    @Bean
    public SimpleAuthFilter simpleAuthFilter(){
        return new SimpleAuthFilter();
    }
    @Bean
    public KickoutSessionFilter kickoutSessionFilter(){
        KickoutSessionFilter kickoutSessionFilter = new KickoutSessionFilter();
        kickoutSessionFilter.setLoginUrl();
        return kickoutSessionFilter;
    }
}
