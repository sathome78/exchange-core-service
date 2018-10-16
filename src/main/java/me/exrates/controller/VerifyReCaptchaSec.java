package me.exrates.controller;

/**
 * Created by Valk on 30.03.16.
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

@Component
@PropertySource("classpath:/captcha.properties")
public class VerifyReCaptchaSec {

    private static final Logger logger = LogManager.getLogger(VerifyReCaptchaSec.class);

    public static final String URL = "https://www.google.com/recaptcha/api/siteverify";
    private
    @Value("${captcha.skey}")
    String SECRET_KEY;

    public boolean verify(String recapchaResponse) {
        boolean result = false;
        if (recapchaResponse == null || "".equals(recapchaResponse)) {
            logger.warn("empty response of reCapcha");
        } else {
            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL(URL).openConnection();
            /**/
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                dos.writeBytes("secret=" + SECRET_KEY + "&response=" + recapchaResponse);
                dos.flush();
                dos.close();
            /**/
                BufferedReader isbr = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String inputLine;
                StringBuilder jsonStringResponse = new StringBuilder();

                while ((inputLine = isbr.readLine()) != null) {
                    jsonStringResponse.append(inputLine);
                }
                isbr.close();

                JsonReader jsonReader = Json.createReader(new StringReader(jsonStringResponse.toString()));
                JsonObject jsonObject = jsonReader.readObject();
                jsonReader.close();
                result = jsonObject.getBoolean("success");
            } catch (Exception e) {
                logger.error("error while response of reCapcha verifying " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        logger.debug("result the response of reCapcha verifying: " + result);
        return result;
    }
}
