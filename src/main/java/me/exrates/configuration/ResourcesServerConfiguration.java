package me.exrates.configuration;

import me.exrates.model.enums.AdminAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import static me.exrates.model.enums.AdminAuthority.PROCESS_WITHDRAW;
import static org.springframework.http.HttpMethod.POST;

@EnableResourceServer
@Configuration
public class ResourcesServerConfiguration extends ResourceServerConfigurerAdapter {


    private final
    RedisConnectionFactory redisConnectionFactory;

    @Autowired
    public ResourcesServerConfiguration(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }


    public void configure(ResourceServerSecurityConfigurer resources) {
        TokenStore tokenStore = new RedisTokenStore(redisConnectionFactory);
        resources.resourceId("input_output_api").tokenStore(tokenStore);
    }

    public void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/api/**")
                .authorizeRequests()
                .antMatchers("/api/**").authenticated()
                .antMatchers(POST, "/withdraw/request/**").authenticated()
                .antMatchers("/withdrawal/request/accept", "/withdrawal/request/decline").hasAuthority(PROCESS_WITHDRAW.name())
                .antMatchers(POST, "/2a8fy7b07dxe44/bitcoinWallet/**").hasAuthority(AdminAuthority.MANAGE_BTC_CORE_WALLET.name())
                .antMatchers("/", "/index.jsp", "/client/**", "/dashboard/**", "/tradingview/**", "/ico_dashboard/**", "/registrationConfirm/**",
                        "/changePasswordConfirm/**", "/changePasswordConfirm/**", "/aboutUs", "/57163a9b3d1eafe27b8b456a.txt", "/newIpConfirm/**").permitAll()
                .antMatchers(POST, "/merchants/withdrawal/request/accept",
                        "/merchants/withdrawal/request/decline").hasAuthority(PROCESS_WITHDRAW.name())
                .antMatchers(POST, "/refill/request/**").authenticated()
                .antMatchers("/2a8fy7b07dxe44/withdrawal").hasAuthority(PROCESS_WITHDRAW.name())
                .antMatchers(POST, "/2a8fy7b07dxe44/withdraw/**").hasAuthority(PROCESS_WITHDRAW.name())
//                .antMatchers(HttpMethod.GET, "/**").access("#oauth2.hasScope('read')")
//                .antMatchers(HttpMethod.POST, "/**").access("#oauth2.hasScope('write')")
//                .antMatchers(HttpMethod.PATCH, "/**").access("#oauth2.hasScope('write')")
//                .antMatchers(HttpMethod.PUT, "/**").access("#oauth2.hasScope('write')")
//                .antMatchers(HttpMethod.DELETE, "/**").access("#oauth2.hasScope('write')")
                .and()

                .headers().addHeaderWriter((request, response) -> {
            response.addHeader("Access-Control-Allow-Origin", "*");
            if (request.getMethod().equals("OPTIONS")) {
                response.setHeader("Access-Control-Allow-Methods", request.getHeader("Access-Control-Request-Method"));
                response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
            }
        });
    }

}
