package me.exrates.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XssFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {


    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            Map<String, String[]> parameterMap = request.getParameterMap();
            Map<String, String> errorMap = new HashMap<>();
//            parameterMap.forEach((k, v) -> Stream.of(v).forEach(va -> {
//                if (!k.equalsIgnoreCase("creatorEmail") && !k.equalsIgnoreCase("recipientEmail") && !k.equalsIgnoreCase("hash") &&
//                        !k.equalsIgnoreCase("password") && !k.equalsIgnoreCase("confirmPassword") && !va.equals(Jsoup.clean(va, Whitelist.basic()))) {
//                    errorMap.put(k, "validation.notallowedsymbol");
//                }
//            }));
//            request.setAttribute("xssErrors", errorMap);
            chain.doFilter(request, response);
        } catch (RuntimeException e) {
            ((HttpServletResponse) response).sendRedirect("/dashboard");
        }
    }

    @Override
    public void destroy() {

    }
}
