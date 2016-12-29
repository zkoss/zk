package org.zkoss.zktest.test2;

import org.zkoss.bind.impl.ReferenceBindingHandler;
import org.zkoss.lang.ContextClassLoaderFactory;
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
}