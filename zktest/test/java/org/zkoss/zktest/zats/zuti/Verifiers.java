/** Verifiers.java
	Purpose:
		
	Description:
		
	History:
		4:02:30 PM Nov 6, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.zuti;

import static org.junit.Assert.fail;

import java.util.LinkedList;

import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zktest.zats.zuti.verifier.Verifier;

/**
 * @author jumperchen
 */
public class Verifiers {

	private AbstractComponent host;
	private LinkedList<Verifier> verifiers;

	private Verifiers(AbstractComponent host) {
		this.host = host;
	}

	private void init(Class<? extends Verifier>[] verifiers) {
		this.verifiers = new LinkedList<Verifier>();
		for (Class<? extends Verifier> ver : verifiers) {
			try {
				this.verifiers.add((Verifier)Classes.newInstance(ver, null));
			} catch (Exception e) {
				fail(e.getMessage());
			}
		}
	}

	public void verify() {
		for (Verifier v : verifiers)
			v.verify(host);
	}

	public static Verifiers getInstance(AbstractComponent host,
			Class<? extends Verifier>[] verclass) {
		Verifiers verifiers = new Verifiers(host);
		verifiers.init(verclass);
		return verifiers;
	}
}
