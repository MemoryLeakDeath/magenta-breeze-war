package tv.memoryleakdeath.magentabreeze.common;

public enum OAuthTokenTypes {
    ACCESSTOKEN("access_token"), REFRESHTOKEN("refresh_token");

    private String tokenKey;

    OAuthTokenTypes(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public String getTokenKey() {
        return tokenKey;
    }
}
