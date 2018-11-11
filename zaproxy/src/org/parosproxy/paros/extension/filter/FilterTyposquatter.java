/*
 *
 * Paros and its related class files.
 *
 * Paros is an HTTP/HTTPS proxy for assessing web application security.
 * Copyright (C) 2003-2004 Chinotec Technologies Company
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Clarified Artistic License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Clarified Artistic License for more details.
 *
 * You should have received a copy of the Clarified Artistic License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
// ZAP: 2011/04/16 i18n
// ZAP: 2012/04/25 Added @Override annotation to all appropriate methods.
// ZAP: 2013/01/25 Removed the "(non-Javadoc)" comments.
// ZAP: 2013/03/03 Issue 546: Remove all template Javadoc comments

package org.parosproxy.paros.extension.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpRequestHeader;


public class FilterTyposquatter extends ExtensionFilter /*implements Filter*/ {



    public int getId() {
        return 500;
    }

    @Override
    public String getName() {
        return "AAAAAAAAAAAAAA Typosquatter extension";
    }

    @Override
    public boolean onHttpRequestSend(HttpMessage msg) {

        List<String> whiteList = new ArrayList<>(); // TODO replace with actual whitelist
        whiteList.add("motherfuckingwebsite.com");

        String host = msg.getRequestHeader().getHostName();

        if (whiteList.contains(host)) {
            //throw new RuntimeException("AAAAAAAAAAAAAAAAAAA");
            return false;

//            HttpRequestHeader h = new HttpRequestHeader();
//            try {
//                h.setURI(new URI("127.0.0.1"));
//                h.setMethod("GET");
//            } catch (URIException e) {
//                e.printStackTrace();
//            }
//            msg.setRequestHeader(h);
        }
        else {
            // pass through
            return true;
        }

    }

    @Override
    public boolean onHttpResponseReceive(HttpMessage msg) {
        return true;
    }


}
