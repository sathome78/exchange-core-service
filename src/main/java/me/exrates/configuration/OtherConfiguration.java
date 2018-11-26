package me.exrates.configuration;

import me.exrates.model.ChatComponent;
import me.exrates.model.enums.ChatLang;
import me.exrates.util.geetest.GeetestLib;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;

import java.util.EnumMap;
import java.util.Locale;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Configuration
public class OtherConfiguration {


    @Value("${geetest.captchaId}")
    private String gtCaptchaId;
    @Value("${geetest.privateKey}")
    private String gtPrivateKey;
    @Value("${geetest.newFailback}")
    private String gtNewFailback;


    @Value("${twitter.appId}")
    private String twitterConsumerKey;
    @Value("${twitter.appSecret}")
    private String twitterConsumerSecret;
    @Value("${twitter.accessToken}")
    private String twitterAccessToken;
    @Value("${twitter.accessTokenSecret}")
    private String twitterAccessTokenSecret;

    @Bean
    public GeetestLib geetest() {
        return new GeetestLib(gtCaptchaId, gtPrivateKey, Boolean.valueOf(gtNewFailback));
    }

    @Bean
    public EnumMap<ChatLang, ChatComponent> chatComponents() {
        final EnumMap<ChatLang, ChatComponent> handlers = new EnumMap<>(ChatLang.class);
        for (ChatLang lang : ChatLang.values()) {
            final ChatComponent chatComponent = new ChatComponent(new ReentrantReadWriteLock(), new TreeSet<>());
            handlers.put(lang, chatComponent);
        }
        return handlers;
    }

    @Bean(name = "ExratesSessionRegistry")
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public Twitter twitter() {
        return new TwitterTemplate(
                twitterConsumerKey,
                twitterConsumerSecret,
                twitterAccessToken,
                twitterAccessTokenSecret);
    }


    @Bean
    public DefaultSimpUserRegistry defaultSimpUserRegistry() {
        return new DefaultSimpUserRegistry();
    }


    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(new Locale("en"));
        resolver.setCookieName("myAppLocaleCookie");
        resolver.setCookieMaxAge(3600);
        return resolver;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
