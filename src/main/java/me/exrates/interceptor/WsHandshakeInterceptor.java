package me.exrates.interceptor;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Log4j2
public class WsHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private LocaleResolver localeResolver;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
      /*  log.error("begin handshake");
        Locale locale;
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            locale =  localeResolver.resolveLocale(servletRequest.getServletRequest());
        } else {
            locale = Locale.ENGLISH;
        }
        attributes.put("locale", locale);
        log.error("wsLocale {}", locale);*/
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
