package com.acn.utility;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

//You can use Proxy-Authenticate header as well
public class ProxyAuthenticator extends Authenticator {
	
	private String userName, password;

    public ProxyAuthenticator(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password.toCharArray());
    }
}
