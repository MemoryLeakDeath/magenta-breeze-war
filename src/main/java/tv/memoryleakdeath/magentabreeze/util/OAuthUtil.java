package tv.memoryleakdeath.magentabreeze.util;

import jakarta.servlet.http.HttpServletRequest;

public final class OAuthUtil {
    private OAuthUtil() {
    }

    public static String buildUrlPath(HttpServletRequest request, String uri) {
        return "https://localhost:%d%s".formatted(request.getServerPort(), uri);
    }

}
