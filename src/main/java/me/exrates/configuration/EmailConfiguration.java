package me.exrates.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfiguration {

    @Value("${mail.mail_support.host}")
    private String mailSupportHost;
    @Value("${mail.mail_support.port}")
    private String mailSupportPort;
    @Value("${mail.mail_support.protocol}")
    private String mailSupportProtocol;
    @Value("${mail.mail_support.user}")
    private String mailSupportUser;
    @Value("${mail.mail_support.password}")
    private String mailSupportPassword;
    @Value("${mail.mail_mandrill.host}")
    private String mailMandrillHost;
    @Value("${mail.mail_mandrill.port}")
    private String mailMandrillPort;
    @Value("${mail.mail_mandrill.protocol}")
    private String mailMandrillProtocol;
    @Value("${mail.mail_mandrill.user}")

    private String mailMandrillUser;
    @Value("${mail.mail_mandrill.password}")
    private String mailMandrillPassword;
    @Value("${mail.mail_info.host}")
    private String mailInfoHost;
    @Value("${mail.mail_info.port}")
    private String mailInfoPort;
    @Value("${mail.mail_info.protocol}")
    private String mailInfoProtocol;
    @Value("${mail.mail_info.user}")
    private String mailInfoUser;
    @Value("${mail.mail_info.password}")
    private String mailInfoPassword;

    @Bean(name = "SupportMailSender")
    public JavaMailSenderImpl javaMailSenderImpl() {
        final JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(mailSupportHost);
        mailSenderImpl.setPort(Integer.parseInt(mailSupportPort));
        mailSenderImpl.setProtocol(mailSupportProtocol);
        mailSenderImpl.setUsername(mailSupportUser);
        mailSenderImpl.setPassword(mailSupportPassword);
        final Properties javaMailProps = new Properties();
        javaMailProps.put("mail.smtp.auth", true);
        javaMailProps.put("mail.smtp.starttls.enable", true);
        javaMailProps.put("mail.smtp.ssl.trust", mailSupportHost);
        mailSenderImpl.setJavaMailProperties(javaMailProps);
        return mailSenderImpl;
    }

    @Bean(name = "MandrillMailSender")
    public JavaMailSenderImpl mandrillMailSenderImpl() {
        final JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(mailMandrillHost);
        mailSenderImpl.setPort(Integer.parseInt(mailMandrillPort));
        mailSenderImpl.setProtocol(mailMandrillProtocol);
        mailSenderImpl.setUsername(mailMandrillUser);
        mailSenderImpl.setPassword(mailMandrillPassword);
        final Properties javaMailProps = new Properties();
        javaMailProps.put("mail.smtp.auth", true);
        javaMailProps.put("mail.smtp.starttls.enable", true);
        javaMailProps.put("mail.smtp.ssl.trust", mailMandrillHost);
        mailSenderImpl.setJavaMailProperties(javaMailProps);
        return mailSenderImpl;
    }

    @Bean(name = "InfoMailSender")
    public JavaMailSenderImpl infoMailSenderImpl() {
        final JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(mailInfoHost);
        mailSenderImpl.setPort(Integer.parseInt(mailInfoPort));
        mailSenderImpl.setProtocol(mailInfoProtocol);
        mailSenderImpl.setUsername(mailInfoUser);
        mailSenderImpl.setPassword(mailInfoPassword);
        final Properties javaMailProps = new Properties();
        javaMailProps.put("mail.smtp.auth", true);
        javaMailProps.put("mail.smtp.starttls.enable", true);
        javaMailProps.put("mail.smtp.ssl.trust", mailInfoHost);
        mailSenderImpl.setJavaMailProperties(javaMailProps);
        return mailSenderImpl;
    }

}
