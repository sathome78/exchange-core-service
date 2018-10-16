package me.exrates.configuration;

import me.exrates.model.ChatComponent;
import me.exrates.model.enums.ChatLang;
import me.exrates.util.geetest.GeetestLib;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

import java.util.EnumMap;
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

}
