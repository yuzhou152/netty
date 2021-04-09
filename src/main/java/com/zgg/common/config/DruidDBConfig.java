package com.zgg.common.config;

import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.Data;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;

/**
 * @Description 数据源
 * @Author zy
 * @Date 2019-03-09 21:24:32
 */

@Configuration
@MapperScan(basePackages = {DruidDBConfig.daoPackage}, sqlSessionTemplateRef = "sqlSessionTemplate")
@ConfigurationProperties(prefix = "spring.datasource")
@Data
public class DruidDBConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DruidDBConfig.class);
    public static final String daoPackage = "com.zgg.**.dao";
    public static final String mapperPackage = "classpath*:/mapper/**/*.xml";


    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private int initialSize;
    private int maxActive;
    private int minIdle;
    private int maxWait;
    private boolean poolPreparedStatements;
    private int maxPoolPreparedStatementPerConnectionSize;
    private int timeBetweenEvictionRunsMillis;
    private int minEvictableIdleTimeMillis;
    private int maxEvictableIdleTimeMillis;
    private String validationQuery;
    private boolean testWhileIdle;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private String filters;
    private String connectionProperties;

    @Bean("dataSource")
    public DataSource dataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
        datasource.setValidationQuery(validationQuery);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        try {
            datasource.setFilters(filters);
        } catch (Exception e) {
            LOGGER.error("druid configuration initialization filter", e);
        }
        datasource.setConnectionProperties(connectionProperties);
        return datasource;
    }

    @Bean(name = "SqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource db2_dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(db2_dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperPackage));
        return factory.getObject();
    }

    @Bean(name = "transactionManager")
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("SqlSessionFactory") SqlSessionFactory SqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(SqlSessionFactory);
    }


    // 以下是事务
    /**
     * @Description 声明式事务
     * 1. DataSourceTransactionManager注入事务拦截器
     * 2. 通过定义Properties 自定义拦截方法的事务
     * 3. 返回事务拦截器 TransactionInterceptor
     * @Author zy
     * @Date 2019-03-10 10:05:04
     */
    @Bean(name = "transactionInterceptorCommon")
    public TransactionInterceptor transactionInterceptor(
        @Qualifier("transactionManager")DataSourceTransactionManager dataSourceTransactionManager) {
        TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
        transactionInterceptor.setTransactionManager(dataSourceTransactionManager);
        Properties transactionAttributes = new Properties();

        transactionAttributes.setProperty("insert*", "PROPAGATION_REQUIRED,-Throwable");
        transactionAttributes.setProperty("update*", "PROPAGATION_REQUIRED,-Throwable");
        transactionAttributes.setProperty("delete*", "PROPAGATION_REQUIRED,-Throwable");
        transactionAttributes.setProperty("remove*", "PROPAGATION_REQUIRED,-Throwable");
        transactionAttributes.setProperty("save*", "PROPAGATION_REQUIRED,-Throwable");
        transactionAttributes.setProperty("add*", "PROPAGATION_REQUIRED,-Throwable");
        transactionAttributes.setProperty("create*", "PROPAGATION_REQUIRED,-Throwable");
        transactionAttributes.setProperty("put*", "PROPAGATION_REQUIRED,-Throwable");
        transactionAttributes.setProperty("merge*", "PROPAGATION_REQUIRED,-Throwable");
        transactionAttributes.setProperty("submit*", "PROPAGATION_REQUIRED,-Throwable");

        transactionAttributes.setProperty("get*", "PROPAGATION_REQUIRED,-Throwable,readOnly");
        transactionAttributes.setProperty("select*", "PROPAGATION_REQUIRED,-Throwable,readOnly");
        transactionAttributes.setProperty("find*", "PROPAGATION_REQUIRED,-Throwable,readOnly");
        transactionAttributes.setProperty("count*", "PROPAGATION_REQUIRED,-Throwable,readOnly");
        transactionAttributes.setProperty("list*", "PROPAGATION_REQUIRED,-Throwable,readOnly");

        transactionInterceptor.setTransactionAttributes(transactionAttributes);
        return transactionInterceptor;
    }

    /**
     * @Description 通过代理注册事务拦截器Bean
     * @Author zy
     * @Date 2019-03-10 10:11:03
     */
    @Bean(name = "beanNameAutoProxyCreator")
    public BeanNameAutoProxyCreator transactionAutoProxy() {
        BeanNameAutoProxyCreator transactionAutoProxy = new BeanNameAutoProxyCreator();
        transactionAutoProxy.setProxyTargetClass(true);
        transactionAutoProxy.setBeanNames("*ServiceImpl");
        transactionAutoProxy.setInterceptorNames("transactionInterceptorCommon");
        return transactionAutoProxy;
    }

}
