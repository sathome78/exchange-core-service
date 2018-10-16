package me.exrates.controller;

import me.exrates.util.geetest.GeetestLib;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
public class GeetestController {

    @Autowired
    private GeetestLib geetest;

    @RequestMapping(value = "/gt/register")
    public String initGeetest(HttpServletRequest request) {
        String userid = "test";

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", userid);
        param.put("client_type", "web");
        param.put("ip_address", "127.0.0.1");

        int gtServerStatus = geetest.preProcess(param);
        request.getSession().setAttribute(geetest.gtServerStatusSessionKey, gtServerStatus);
        request.getSession().setAttribute("userid", userid);

        return geetest.getResponseStr();
    }
}
