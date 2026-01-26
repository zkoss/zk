/* CspProviderImpl.java

	Purpose:

	Description:

    History:
            Fri Nov 29 16:57:21 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.http;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.CspProvider;

/**
 * An {@link org.zkoss.zk.ui.util.CspProvider} implementation for Csp functions
 * and applies.
 *
 * @author peakerlee
 */
public class CspProviderImpl implements CspProvider {
    private static final String ATTR_ZK_CSP_NONCE = "cspNonce";
    private static final Set<String> CSP_DIRECTIVE = Set.of(
        "default-src",
        "script-src",
        "script-src-elem",
        "script-src-attr",
        "style-src",
        "style-src-elem",
        "style-src-attr",
        "img-src",
        "media-src",
        "font-src",
        "connect-src",
        "object-src",
        "embed-src",
        "child-src",
        "frame-src",
        "frame-ancestors",
        "form-action",
        "base-uri",
        "manifest-src",
        "worker-src",
        "prefetch-src",
        "navigate-to",
        "report-uri",
        "report-to",
        "upgrade-insecure-requests",
        "block-all-mixed-content",
        "require-sri-for",
        "sandbox",
        "trusted-types",
        "fenced-frame-src",
        "plugin-types",
        "disown-opener"
    );
    private static final String DEFAULT_POLICY =
        "script-src 'self' 'unsafe-inline' 'unsafe-eval'; "
        + "style-src 'self' 'unsafe-inline'; "
        + "img-src 'self' data: https:; "
        + "font-src 'self'; "
        + "connect-src 'self'; "
        + "frame-ancestors 'none';";

    private static final Set<String> DYNAMIC_TOKENS = Set.of(
            "'nonce-{nonce}'",
            "'strict-dynamic'"
    );

    public void setCspHeader(Execution exec, Configuration config) {
        boolean cspEnable = config.isCspEnabled(),
                cspStrictDynamicEnabled = config.isCspStrictDynamicEnabled();

        if (!cspEnable)
            return;

        String customizePolicy = config.getCspPolicy();
        if (config.isCspReportOnly()) {
            exec.setResponseHeader(
                "Content-Security-Policy-Report-Only",
                convertHeader(customizePolicy, cspStrictDynamicEnabled, config.getCspReportURI()));
        } else {
            exec.setResponseHeader(
                "Content-Security-Policy",
                convertHeader(customizePolicy, cspStrictDynamicEnabled, null));
        }
    }

    public String getCspNonce() {
        Execution exec = Executions.getCurrent();
        String nonce = (String) exec.getAttribute(ATTR_ZK_CSP_NONCE);
        if (nonce == null) {
            byte[] nonceBytes = new byte[32];
            new java.security.SecureRandom().nextBytes(nonceBytes);
            nonce = java.util.Base64.getEncoder().encodeToString(nonceBytes);
            exec.setAttribute(ATTR_ZK_CSP_NONCE, nonce);
        }
        return nonce;
    }

    private String convertHeader(String policy, boolean cspStrictDynamicEnabled, String cspReportURI) {
        Map<String, Set<String>> cspMap = mergePolicies(policy);
        StringBuilder cspHeader = generateHeader(cspMap, cspStrictDynamicEnabled);
        if (cspReportURI != null && !cspReportURI.isEmpty())
            cspHeader.append("report-uri ").append(cspReportURI).append("; ");
        return cspHeader.toString().trim();
    }

    private Map<String, Set<String>> mergePolicies(String customPolicy) {
        Map<String, Set<String>> cspMap = parsePolicyToMap(DEFAULT_POLICY);
        if (customPolicy != null && !customPolicy.isBlank()) {
            Map<String, Set<String>> customMap = parsePolicyToMap(customPolicy);
            for (Map.Entry<String, Set<String>> entry : customMap.entrySet()) {
                cspMap.computeIfAbsent(entry.getKey(), k -> new LinkedHashSet<>())
                        .addAll(entry.getValue());
            }
        }
        return cspMap;
    }

    private StringBuilder generateHeader(Map<String, Set<String>> cspMap, boolean cspStrictDynamicEnabled) {
        StringBuilder cspHeader = new StringBuilder();
        for (Map.Entry<String, Set<String>> entry : cspMap.entrySet()) {
            if ("script-src".equals(entry.getKey()) && cspStrictDynamicEnabled) {
                Set<String> scriptValues = new LinkedHashSet<>(entry.getValue());
                // ZK-6055: in dynamic mode need add unsafe-hashes and whitelist for javascript:void(0);
                cspHeader.append(entry.getKey())
                        .append(" ")
                        .append(String.join(" ", scriptValues))
                        .append(" 'unsafe-hashes' ")
                        .append("'sha256-lfXlPY3+MCPOPb4mrw1Y961+745U3WlDQVcOXdchSQc=' ")
                        .append("'sha256-kbHtQyYDQKz4SWMQ8OHVol3EC0t3tHEJFPCSwNG9NxQ='");
                addStrictDynamic(cspHeader);
            } else {
                cspHeader.append(entry.getKey())
                        .append(" ")
                        .append(String.join(" ", entry.getValue()));
            }
            cspHeader.append("; ");
        }
        return cspHeader;
    }

    private void addStrictDynamic(StringBuilder cspHeader) {
        cspHeader.append(" 'strict-dynamic'").append(" 'nonce-").append(getCspNonce()).append("'");
    }

    private static Map<String, Set<String>> parsePolicyToMap(String policy) {
        Map<String, Set<String>> cspMap = new LinkedHashMap<>();
        String[] tokens = policy.replaceAll(";", " ").split("\\s+");

        String currentDirective = null;
        Set<String> currentValues = new LinkedHashSet<>();

        for (String token : tokens) {
            if (CSP_DIRECTIVE.contains(token)) {
                if (currentDirective != null)
                    cspMap.put(currentDirective, currentValues);
                currentDirective = token;
                currentValues = new LinkedHashSet<>();
            } else
                if (!DYNAMIC_TOKENS.contains(token))
                    currentValues.add(token);
        }

        if (currentDirective != null)
            cspMap.put(currentDirective, currentValues);
        return cspMap;
    }
}