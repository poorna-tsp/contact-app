package com.plivo.contact.auth;

import com.plivo.contact.util.StringUtil;

public class AuthorizationFactory {
    public static Authorization getAuthorizationImpl(String token) throws IllegalArgumentException {
        if (StringUtil.isNullOrEmpty(token)) {
            throw new IllegalArgumentException("Invalid token. Token can not be empty.");
        }

        if (!token.startsWith("Basic ")) {
            throw new IllegalArgumentException("Invalid token. Unsupported authentication method.");
        }

        return new BasicAuthorization(token.substring(6)) ;
    }
}
