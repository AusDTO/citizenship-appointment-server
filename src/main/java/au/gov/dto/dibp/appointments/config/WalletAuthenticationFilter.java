package au.gov.dto.dibp.appointments.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

class WalletAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication authentication = createAuthenticationFromQueryParameters(request);
            if (authentication == null) {
                authentication = createAuthenticationFromAuthorizationHeader(request);
            }
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private Authentication createAuthenticationFromQueryParameters(HttpServletRequest request) throws UnsupportedEncodingException {
        String queryString = StringUtils.defaultString(request.getQueryString());
        String[] pairs = queryString.split("&");
        Map<String, String> queryParams = new HashMap<>();
        for (String pair : pairs) {
            String[] splitPair = pair.split("=", 2);
            if (splitPair.length == 2) {
                queryParams.put(URLDecoder.decode(splitPair[0], StandardCharsets.UTF_8.name()), URLDecoder.decode(splitPair[1], StandardCharsets.UTF_8.name()));
            }
        }
        if (!queryParams.containsKey("id") || !queryParams.containsKey("otherid")) {
            return null;
        }
        return new UsernamePasswordAuthenticationToken(queryParams.get("id"), queryParams.get("otherid"));
    }

    private Authentication createAuthenticationFromAuthorizationHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("ApplePass ")) {
            return null;
        }
        String authorizationToken = authorizationHeader.replace("ApplePass ", "");
        String decodedAuthorizationToken = new String(Base64.getMimeDecoder().decode(authorizationToken), StandardCharsets.ISO_8859_1);
        String[] credentials = decodedAuthorizationToken.split(":", 2);
        if (credentials.length != 2) {
            return null;
        }
        return new UsernamePasswordAuthenticationToken(credentials[0], credentials[1]);
    }
}
