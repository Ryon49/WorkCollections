package com.wdong.http;

import com.wdong.config.GoogleCaptchaSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@EnableConfigurationProperties(GoogleCaptchaSettings.class)
public class GoogleReCaptcha {
    @Test
    public void test() {
        RestTemplate rest = new RestTemplate();
        String s = rest.getForObject("https://api.iextrading.com/1.0/stock/aapl/stats", String.class);

        System.out.println(s);
    }

    @Test
    public void test1() {
        System.out.println(captcha.getSecret());
    }

    @Autowired
    private GoogleCaptchaSettings captcha;
}
