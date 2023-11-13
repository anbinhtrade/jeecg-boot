package org.jeecg.config.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.IRedisManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisClusterManager;
import org.crazycake.shiro.RedisManager;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.JeecgBaseConfig;
import org.jeecg.config.shiro.filters.CustomShiroFilterFactoryBean;
import org.jeecg.config.shiro.filters.JwtFilter;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.*;

/**
 * @author: Scott
 * @date: 2018/2/7
 * @description: shiro Configure the class
 */

@Slf4j
@Configuration
public class ShiroConfig {

    @Resource
    private LettuceConnectionFactory lettuceConnectionFactory;
    @Autowired
    private Environment env;
    @Resource
    private JeecgBaseConfig jeecgBaseConfig;

    /**
     * Filter Chain definition
     *
     * 1. Multiple filters can be configured for a URL, separated by commas
     * 2. When multiple filters are set, all of them are verified before they are deemed to have passed
     * 3. Some filters can specify parameters, such as perms and roles
     */
    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        CustomShiroFilterFactoryBean shiroFilterFactoryBean = new CustomShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // INTERCEPTOR
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();

        // YML mode is supported, and interception and exclusion are configured
        if(jeecgBaseConfig!=null && jeecgBaseConfig.getShiro()!=null){
            String shiroExcludeUrls = jeecgBaseConfig.getShiro().getExcludeUrls();
            if(oConvertUtils.isNotEmpty(shiroExcludeUrls)){
                String[] permissionUrl = shiroExcludeUrls.split(",");
                for(String url : permissionUrl){
                    filterChainDefinitionMap.put(url,"anon");
                }
            }
        }
        // Configure the order of links that will not be blocked
        filterChainDefinitionMap.put("/sys/cas/client/validateLogin", "anon"); //CAS authentication login
        filterChainDefinitionMap.put("/sys/randomImage/**", "anon"); //The login verification code interface is excluded
        filterChainDefinitionMap.put("/sys/checkCaptcha", "anon"); //The login verification code interface is excluded
        filterChainDefinitionMap.put("/sys/login", "anon"); //Login API exclusion
        filterChainDefinitionMap.put("/sys/mLogin", "anon"); //Login API exclusion
        filterChainDefinitionMap.put("/sys/logout", "anon"); //Logout interface exclusion
        filterChainDefinitionMap.put("/sys/thirdLogin/**", "anon"); //Third-party logins
        filterChainDefinitionMap.put("/sys/getEncryptedString", "anon"); //Get the encrypted string
        filterChainDefinitionMap.put("/sys/sms", "anon");//SMS verification code
        filterChainDefinitionMap.put("/sys/phoneLogin", "anon");//Mobile phone login
        filterChainDefinitionMap.put("/sys/user/checkOnlyUser", "anon");//Verify whether the user exists
        filterChainDefinitionMap.put("/sys/user/register", "anon");//User Registration
        filterChainDefinitionMap.put("/sys/user/phoneVerification", "anon");//The user forgot the password to verify the mobile phone number
        filterChainDefinitionMap.put("/sys/user/passwordChange", "anon");//The user changes the password
        filterChainDefinitionMap.put("/auth/2step-code", "anon");//Login verification code
        filterChainDefinitionMap.put("/sys/common/static/**", "anon");//There is no restriction on the token of the image preview > download file
        filterChainDefinitionMap.put("/sys/common/pdf/**", "anon");//PDF preview
        filterChainDefinitionMap.put("/generic/**", "anon");//A file is required for PDF preview

        filterChainDefinitionMap.put("/sys/getLoginQrcode/**", "anon"); //Login QR code
        filterChainDefinitionMap.put("/sys/getQrcodeToken/**", "anon"); //Listen and scan the code
        filterChainDefinitionMap.put("/sys/checkAuth", "anon"); //Authorization interface exclusions


        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/doc.html", "anon");
        filterChainDefinitionMap.put("/**/*.js", "anon");
        filterChainDefinitionMap.put("/**/*.css", "anon");
        filterChainDefinitionMap.put("/**/*.html", "anon");
        filterChainDefinitionMap.put("/**/*.svg", "anon");
        filterChainDefinitionMap.put("/**/*.pdf", "anon");
        filterChainDefinitionMap.put("/**/*.jpg", "anon");
        filterChainDefinitionMap.put("/**/*.png", "anon");
        filterChainDefinitionMap.put("/**/*.gif", "anon");
        filterChainDefinitionMap.put("/**/*.ico", "anon");
        filterChainDefinitionMap.put("/**/*.ttf", "anon");
        filterChainDefinitionMap.put("/**/*.woff", "anon");
        filterChainDefinitionMap.put("/**/*.woff2", "anon");

        filterChainDefinitionMap.put("/druid/**", "anon");
        filterChainDefinitionMap.put("/swagger-ui.html", "anon");
        filterChainDefinitionMap.put("/swagger**/**", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/v2/**", "anon");
        
        filterChainDefinitionMap.put("/sys/annountCement/show/**", "anon");

        //Blocks report exclusions
        filterChainDefinitionMap.put("/jmreport/**", "anon");
        filterChainDefinitionMap.put("/**/*.js.map", "anon");
        filterChainDefinitionMap.put("/**/*.css.map", "anon");
        
        //Drag and drop the dashboard designer to exclude
        filterChainDefinitionMap.put("/drag/view", "anon");
        filterChainDefinitionMap.put("/drag/page/queryById", "anon");
        filterChainDefinitionMap.put("/drag/onlDragDatasetHead/getAllChartData", "anon");
        filterChainDefinitionMap.put("/drag/onlDragDatasetHead/getTotalData", "anon");
        filterChainDefinitionMap.put("/drag/mock/json/**", "anon");
        //Example of a large-screen template
        filterChainDefinitionMap.put("/test/bigScreen/**", "anon");
        filterChainDefinitionMap.put("/bigscreen/template1/**", "anon");
        filterChainDefinitionMap.put("/bigscreen/template1/**", "anon");
        //filterChainDefinitionMap.put("/test/jeecgDemo/rabbitMqClientTest/**", "anon"); //MQ测试
        //filterChainDefinitionMap.put("/test/jeecgDemo/html", "anon"); //模板页面
        //filterChainDefinitionMap.put("/test/jeecgDemo/redis/**", "anon"); //redis测试

        //WebSocket Exclusion
        filterChainDefinitionMap.put("/websocket/**", "anon");//System notifications and announcements
        filterChainDefinitionMap.put("/newsWebsocket/**", "anon");//CMS模块
        filterChainDefinitionMap.put("/vxeSocket/**", "anon");//JVxeTable无痕刷新示例

        //Performance Monitoring - Vulnerability Leak TOEKN (DAID Connection Pool also has)
        //filterChainDefinitionMap.put("/actuator/**", "anon");
        //Test module exclusions
        filterChainDefinitionMap.put("/test/seata/**", "anon");

        // update-begin--author:liusq Date:20230522 for：[issues/4829]When you access a URL that does not exist, you will be prompted that the token is invalid, so please log in again
        // Error path exclusion
        filterChainDefinitionMap.put("/error", "anon");
        // update-end--author:liusq Date:20230522 for：[issues/4829]When you access a URL that does not exist, you will be prompted that the token is invalid, so please log in again

        // Add your own filter and name it jwt
        Map<String, Filter> filterMap = new HashMap<String, Filter>(1);
        //If the cloudServer is empty, it means that it is a monolithic and needs to load the cross-domain configuration [microservice cross-domain switching]
        Object cloudServer = env.getProperty(CommonConstant.CLOUD_SERVER_KEY);
        filterMap.put("jwt", new JwtFilter(cloudServer==null));
        shiroFilterFactoryBean.setFilters(filterMap);
        // <!-- The filter chain definition is executed in order from top to bottom, with /** at the bottom
        filterChainDefinitionMap.put("/**", "jwt");

        // The unauthorized interface returns JSON
        shiroFilterFactoryBean.setUnauthorizedUrl("/sys/common/403");
        shiroFilterFactoryBean.setLoginUrl("/sys/common/403");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager(ShiroRealm myRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myRealm);

        /*
         * 关闭shiro自带的session，详情见文档
         * http://shiro.apache.org/session-management.html#SessionManagement-
         * StatelessApplications%28Sessionless%29
         */
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        //Custom caching implementation, using Redis
        securityManager.setCacheManager(redisCacheManager());
        return securityManager;
    }

    /**
     * The code below is to add annotation support
     * @return
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        /**
         * Resolve duplicate proxy issues github#994
         * Add a prefix to determine that it does not match any Advisor
         */
        defaultAdvisorAutoProxyCreator.setUsePrefix(true);
        defaultAdvisorAutoProxyCreator.setAdvisorBeanNamePrefix("_no_advisor");
        return defaultAdvisorAutoProxyCreator;
    }

    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    /**
     * cacheManager CACHE REDIS IMPLEMENTATION
     * The shiro-redis open-source plugin is used
     *
     * @return
     */
    public RedisCacheManager redisCacheManager() {
        log.info("===============(1)Create a cache manager RedisCacheManager");
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        //Redis caches for different users (the ID here needs to correspond to the ID field in the user entity for unique identification)
        redisCacheManager.setPrincipalIdFieldName("id");
        //用户权限信息缓存时间
        redisCacheManager.setExpire(200000);
        return redisCacheManager;
    }

    /**
     * Configure shiro redisManager
     * It is a shiro that is used-Redis open-source plugin
     *
     * @return
     */
    @Bean
    public IRedisManager redisManager() {
        log.info("===============(2)Create a Redis Manager and connect to ApsaraDB for Redis..");
        IRedisManager manager;
        // redis Stand-alone support, used when the cluster is empty or the cluster does not have machines add by jzyadmin@163.com
        if (lettuceConnectionFactory.getClusterConfiguration() == null || lettuceConnectionFactory.getClusterConfiguration().getClusterNodes().isEmpty()) {
            RedisManager redisManager = new RedisManager();
            redisManager.setHost(lettuceConnectionFactory.getHostName() + ":" + lettuceConnectionFactory.getPort());
            //(lettuceConnectionFactory.getPort());
            redisManager.setDatabase(lettuceConnectionFactory.getDatabase());
            redisManager.setTimeout(0);
            if (!StringUtils.isEmpty(lettuceConnectionFactory.getPassword())) {
                redisManager.setPassword(lettuceConnectionFactory.getPassword());
            }
            manager = redisManager;
        }else{
            // Redis clusters are supported, and cluster configurations are preferred
            RedisClusterManager redisManager = new RedisClusterManager();
            Set<HostAndPort> portSet = new HashSet<>();
            lettuceConnectionFactory.getClusterConfiguration().getClusterNodes().forEach(node -> portSet.add(new HostAndPort(node.getHost() , node.getPort())));
            //update-begin--Author:scott Date:20210531 for：修改集群模式下未设置redis密码的bug issues/I3QNIC
            if (oConvertUtils.isNotEmpty(lettuceConnectionFactory.getPassword())) {
                JedisCluster jedisCluster = new JedisCluster(portSet, 2000, 2000, 5,
                    lettuceConnectionFactory.getPassword(), new GenericObjectPoolConfig());
                redisManager.setPassword(lettuceConnectionFactory.getPassword());
                redisManager.setJedisCluster(jedisCluster);
            } else {
                JedisCluster jedisCluster = new JedisCluster(portSet);
                redisManager.setJedisCluster(jedisCluster);
            }
            //update-end--Author:scott Date:20210531 for：修改集群模式下未设置redis密码的bug issues/I3QNIC
            manager = redisManager;
        }
        return manager;
    }

}
