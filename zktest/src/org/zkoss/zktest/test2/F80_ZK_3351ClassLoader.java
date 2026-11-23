package org.zkoss.zktest.test2;

import org.zkoss.bind.impl.ReferenceBindingHandler;
import org.zkoss.lang.ContextClassLoaderFactory;
import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

/**
 * @author jameschu
 */
public class F80_ZK_3351ClassLoader implements ContextClassLoaderFactory {
	@Override
	public ClassLoader getContextClassLoader(Class<?> reference) {
		Execution exc = Executions.getCurrent();
		if (exc != null && reference.equals(ReferenceBindingHandler.class) && exc.getAttribute("3351") != null)
			exc.getDesktop().setAttribute("3351", "done");
		return Thread.currentThread().getContextClassLoader();
	}

	@Override
	public ClassLoader getContextClassLoaderForName(String className) {
		// Test for ZK-3762
		Execution exc = Executions.getCurrent();
		// Execution is not ready when initiating LabelLoader, so use Library instead.
		if ("org.zkoss.util.resource.impl.LabelLoaderImpl".equals(className))
			Library.setProperty("3762-labels", "done");
		if (exc != null && "test.3762".equals(className))
			exc.getDesktop().setAttribute("3762-test", "done");
		return Thread.currentThread().getContextClassLoader();
	}
}
