package com.acn.utility;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * ProxyAuthenticator is a custom implementation of {@link Authenticator}
 * used to provide credentials for HTTP proxy authentication.
 * <p>
 * This class can be registered using {@code Authenticator.setDefault(new ProxyAuthenticator(...))}
 * to automatically supply credentials when connecting through a proxy that requires authentication.
 * </p>
 *
 * <p><b>Note:</b> You can also use the "Proxy-Authenticate" HTTP header for manual authentication
 * if needed, but this class simplifies the process for Java's built-in networking APIs.</p>
 */
public class ProxyAuthenticator extends Authenticator {

    private final String userName;
    private final String password;

    /**
     * Constructs a ProxyAuthenticator with the specified username and password.
     *
     * @param userName the proxy username
     * @param password the proxy password
     */
    public ProxyAuthenticator(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    /**
     * Provides the password authentication when requested by the system.
     *
     * @return a {@link PasswordAuthentication} object containing the username and password
     */
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password.toCharArray());
    }
}
