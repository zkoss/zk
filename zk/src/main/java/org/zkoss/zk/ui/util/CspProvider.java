/* CspProvider.java

	Purpose:

	Description:

    History:
            Fri Nov 29 16:57:21 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.util;


import org.zkoss.zk.ui.Execution;

/**
 * Used to provide Content Security Policy (CSP) headers and nonces.
 *
 * <p>Developers can implement this interface to customize CSP header generation
 * and nonce management for their applications.
 *
 * <p>The provider is responsible for:
 * <ul>
 * <li>Setting appropriate CSP headers on HTTP responses based on the configuration</li>
 * <li>Generating and managing cryptographically secure nonces for inline scripts</li>
 * </ul>
 *
 * @author peakerlee
 * @since 10.3.0
 */
public interface CspProvider {
    /** Sets the CSP header for the given execution based on the configuration.
     *
     * <p>The actual headers applied to depend on the provided configuration,
     * including whether report-only mode is enabled and whether a custom
     * policy is specified.
     *
     * @param exec the execution being processed
     * @param config the CSP configuration
     * @since 10.3.0
     */
    public void setCspHeader(Execution exec, Configuration config);

    /** Returns the current CSP nonce for use in inline scripts
     * if no nonce is available.
     *
     * <p>If a nonce has already been generated for the current execution,
     * it is returned; otherwise, a new cryptographically secure, Base64-encoded
     * nonce is generated. The nonce is typically used for inline script tags
     * in strict-dynamic mode but can be retrieved regardless of the mode.
     *
     * @return the current CSP nonce
     * @since 10.3.0
     */
    public String getCspNonce();
}
