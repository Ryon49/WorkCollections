package com.wdong.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "google.recaptcha.key")
public class GoogleCaptchaSettings {
    private String baseUrl;
    private String secret;

    // region getter and setter

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUrl(String recaptchaToken) {
        return String.format("%s?secret=%s&response=%s", this.baseUrl, this.secret, recaptchaToken);
    }

    // endregion
}
