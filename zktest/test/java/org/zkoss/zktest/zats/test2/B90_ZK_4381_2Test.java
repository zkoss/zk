/* B90_ZK_4381_2Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Feb 11 09:40:11 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.MatchMedia;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;

/**
 * @author rudyhuang
 */
public class B90_ZK_4381_2Test {
	@Before
	public void setUp() {
		ExecutionsCtrl.setCurrent(mockExecution());
	}

	private static Execution mockExecution() {
		Execution execution = Mockito.mock(Execution.class,
				Mockito.withSettings().extraInterfaces(ExecutionCtrl.class));
		Desktop desktop = Mockito.mock(Desktop.class);
		Mockito.when(execution.getDesktop()).thenReturn(desktop);
		Mockito.when(execution.getNativeRequest()).thenReturn(Mockito.mock(HttpServletRequest.class));
		return execution;
	}

	@After
	public void tearDown() {
		ExecutionsCtrl.setCurrent(null);
	}

	@Test
	public void testBinderSerializationWithMatchMedia() throws Exception {
		BinderImpl binder = new BinderImpl();
		Component dummy = new AbstractComponent();
		binder.init(dummy, new ViewModelWithMatchMedia(), null);
		writeAndReadBack(binder);
	}

	private BinderImpl writeAndReadBack(BinderImpl binder) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		new ObjectOutputStream(buffer).writeObject(binder);
		return (BinderImpl) new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray())).readObject();
	}

	@Test
	public void testBinderSerializationWithoutMatchMedia() throws Exception {
		BinderImpl binder = new BinderImpl();
		Component dummy = new AbstractComponent();
		binder.init(dummy, new ViewModelWithoutMatchMedia(), null);
		writeAndReadBack(binder);
	}

	public static class ViewModelWithMatchMedia {
		@Init
		public void init() {
		}
		@Command
		public void dummyCommand() {
		}
		@MatchMedia("dummy")
		public void handleMediaQuery() {
		}
	}

	public static class ViewModelWithoutMatchMedia {
		@Init
		public void init() {
		}
		@Command
		public void dummyCommand() {
		}
	}
}
