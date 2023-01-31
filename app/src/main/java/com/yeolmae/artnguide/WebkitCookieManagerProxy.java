package com.yeolmae.artnguide;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;

public class WebkitCookieManagerProxy extends CookieManager {
    android.webkit.CookieManager webkitCookieManager;

    public WebkitCookieManagerProxy() {
        this(null, null);
    }

    WebkitCookieManagerProxy(CookieStore store, CookiePolicy cookiePolicy) {
        super(null, cookiePolicy);

        this.webkitCookieManager = webkitCookieManager.getInstance();
    }

    public String get(String uri) throws IOException { // Map<String, List<String>> requestHeaders
        String url = uri.toString();

        // get the cookie
        String cookie = this.webkitCookieManager.getCookie(url);
//        System.out.print("cookie cookie  "+cookie);
        return cookie;
    }

    @Override
    public CookieStore getCookieStore() {
        // we don't want anyone to work with this cookie store directly
        throw new UnsupportedOperationException();
    }
}
