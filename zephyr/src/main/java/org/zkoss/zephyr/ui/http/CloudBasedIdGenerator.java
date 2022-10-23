/* CloudBasedIdGenerator.java

	Purpose:
		
	Description:
		
	History:
		11:30 AM 2022/8/31, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.ui.http;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Enumeration;
import java.util.UUID;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.sys.IdGenerator;

/**
 * A cloud based ID generator to avoid duplication uuid issue in a cloud mode.
 * @author jumperchen
 */
public class CloudBasedIdGenerator implements IdGenerator {
	private final IdGenerator delegator;

	public CloudBasedIdGenerator(IdGenerator delegator) {
		this.delegator = delegator;
	}

	public String nextComponentUuid(Desktop desktop, Component comp,
			ComponentInfo compInfo) {
		return delegator != null ? delegator.nextComponentUuid(desktop, comp, compInfo): null;
	}

	public String nextAnonymousComponentUuid(Component comp,
			ComponentInfo compInfo) {
		String uid = delegator != null ? delegator.nextAnonymousComponentUuid(comp, compInfo) : null;
		if (uid == null) {
			uid = nextCloudBasedUuid();
		}
		return uid;
	}

	private static byte[] hardwareAddress;

	private static final SecureRandom random = new SecureRandom();
	private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

	private static String nextCloudBasedUuid() {
		if (hardwareAddress == null) {
			try {
				Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
				while (networkInterfaces.hasMoreElements()) {
					NetworkInterface ni = networkInterfaces.nextElement();
					hardwareAddress = ni.getHardwareAddress();
					if (hardwareAddress != null) {
						break;
					}
				}
			} catch (Exception e) {
			}
			if (hardwareAddress == null) {
				final byte[] randomBytesValue = new byte[16];
				random.nextBytes(randomBytesValue);
				hardwareAddress = randomBytesValue;
			}
		}
		return new StringBuilder(
				ENCODER.encodeToString(hardwareAddress)).append(
				UUID.randomUUID().toString()).toString().replace("-", "");
	}

	public String nextPageUuid(Page page) {
		return delegator != null ? delegator.nextPageUuid(page) : null;
	}

	public String nextDesktopId(Desktop desktop) {
		return delegator != null ? delegator.nextDesktopId(desktop) : null;
	}
}
