package me.exrates.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import me.exrates.util.SSMGetter;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@Log4j2
public class DatabaseConfiguration {

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.jdbc-url}")
    private String jdbcUrl;

    @Value("${spring.datasource.hikari.connection-test-query}")
    private String connectionTestQuery;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

//    @Value("${spring.datasource.hikari.maximum-pool-size}") TODO
//    private int maximumPoolSize;
//
//    @Value("${spring.datasource.hikari.minimum-idle}")
//    private int minimumidle;

    @Value("${spring.datasource.ssm-path}")
    private String password;

    @Autowired
    SSMGetter ssmGetter;

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {

        String lookup = ssmGetter.lookup(password);
        HikariDataSource hikariDataSource = new HikariDataSource();
//        hikariDataSource.setPassword("vLHjSkPJjB6JLVcA");
        hikariDataSource.setPassword(lookup);
        hikariDataSource.setUsername(dbUsername);
        hikariDataSource.setJdbcUrl(jdbcUrl);
        hikariDataSource.setConnectionTestQuery(connectionTestQuery);
        hikariDataSource.setDriverClassName(driverClassName);
//        hikariDataSource.setMaximumPoolSize(maximumPoolSize); TODO
//        hikariDataSource.setMinimumIdle(minimumidle);

        return hikariDataSource;
    }

    @Autowired
    @Bean(name = "entityManagerFactory")
    public SessionFactory getSessionFactory(DataSource dataSource) throws Exception {
        Properties properties = new Properties();
        properties.put("spring.jpa.database", "mysql");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
        factoryBean.setPackagesToScan(new String[]{"com.exrates.me"});
        factoryBean.setDataSource(dataSource);
        factoryBean.setHibernateProperties(properties);
        factoryBean.afterPropertiesSet();
        SessionFactory sf = factoryBean.getObject();
        return sf;
    }

    @Autowired
    @Bean(name = "transactionManager")
    public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);
        return transactionManager;
    }

    @Bean(name = "slaveHikariDataSource")
    public DataSource slaveHikariDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(dbUsername);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(50);
        hikariConfig.setReadOnly(true);
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "masterHikariDataSource")
    public DataSource masterHikariDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(dbUsername);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(50);
        DataSource dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
    }

    @DependsOn("slaveHikariDataSource")
    @Bean(name = "slaveTemplate")
    public NamedParameterJdbcTemplate slaveNamedParameterJdbcTemplate(@Qualifier("slaveHikariDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Primary
    @DependsOn("masterHikariDataSource")
    @Bean(name = "masterTemplate")
    public NamedParameterJdbcTemplate masterNamedParameterJdbcTemplate(@Qualifier("masterHikariDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
