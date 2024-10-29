package tv.memoryleakdeath.magentabreeze.common.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAlias;

public class TwitchOAuthAppTokenResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonAlias("access_token")
    private String accessToken;

    @JsonAlias("expires_in")
    private Integer expiresIn;

    @JsonAlias("token_type")
    private String tokenType;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
