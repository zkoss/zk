/* CspProvider.java

	Purpose:

	Description:

    History:
            Fri Nov 29 16:57:21 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.util;


import org.zkoss.zk.ui.Execution;

public interface CspProvider {
    /**
     * Sets the CSP headers for the current execution using the given configuration.
     * <p>
     * The headers applied to depend on the configuration, including whether report-only mode
     * is enabled <csp-report-only>. A custom CSP header generator.
     *
     * @param exec the current Execution
     * @param config the configuration
     * @since 10.3.0
     */
    public void setCspHeader(Execution exec, Configuration config);

    /**
     * Returns the current CSP nonce for use in script tags.
     * <p>
     * If a nonce has already been generated for this execution, it is returned;
     * otherwise, a new cryptographically secure, Base64-encoded nonce is generated.
     * The nonce is used for script tags in strict-dynamic mode but can be retrieved
     * regardless of the mode.
     *
     * @return the current CSP nonce, or null/empty if strict-dynamic is disabled
     * @since 10.3.0
     */
    public String getCspNonce();
}
