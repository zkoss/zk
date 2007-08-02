/* ConversationUtil.java
 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
    Jul 25, 2007 10:03:38 AM , Created by Dennis Chen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.seam;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.jboss.seam.core.Manager;
/**
 * This class helps developers to do thing with Seam's Conversation
 * @author Dennis.Chen
 */
public class ConversationUtil {
    
    /**
     * Append current Long Running Conversation's id to url.
     * @param url
     * @return if exist a long running conversation the new url which contains a long running conversation id will be return.
     * other wish return url directly.  
     */
    static public String appendLongRunningConversation(String url) {
        if(!Manager.instance().isLongRunningConversation()) return url;
        String parmname = Manager.instance().getConversationIdParameter();
        String conversationId = Manager.instance().getCurrentConversationId();
        if (containsParameter(url, parmname))
            return url;
        int loc = url.indexOf('?');
        if (loc > 0) {
            try {
                url += "&" + parmname + "="
                        + URLEncoder.encode(conversationId, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            try {
                url += "?" + parmname + "="
                        + URLEncoder.encode(conversationId, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return url;
    }

    /**package**/ static String encodeParameters(String url, Map<String, Object> parameters) {
        if (parameters.isEmpty())
            return url;

        StringBuilder builder = new StringBuilder(url);
        for (Map.Entry<String, Object> param : parameters.entrySet()) {
            String parameterName = param.getKey();
            if (!containsParameter(url, parameterName)) {
                Object parameterValue = param.getValue();
                if (parameterValue instanceof Iterable) {
                    for (Object value : (Iterable) parameterValue) {
                        builder.append('&').append(parameterName).append('=');
                        if (value != null) {
                            builder.append(encode(value));
                        }
                    }
                } else {
                    builder.append('&').append(parameterName).append('=');
                    if (parameterValue != null) {
                        builder.append(encode(parameterValue));
                    }
                }
            }
        }
        if (url.indexOf('?') < 0) {
            builder.setCharAt(url.length(), '?');
        }
        return builder.toString();
    }

    /**package**/ static boolean containsParameter(String url, String parameterName) {
        return url.indexOf('?' + parameterName + '=') > 0
                || url.indexOf('&' + parameterName + '=') > 0;
    }

    /**package**/ static String encode(Object value) {
        try {
            return URLEncoder.encode(String.valueOf(value), "UTF-8");
        } catch (UnsupportedEncodingException iee) {
            throw new RuntimeException(iee);
        }
    }
}
