package org.zkoss.zkmax.scripting.jython;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyStringMap;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.zkoss.zk.scripting.util.GenericInterpreter;
import org.zkoss.zk.ui.Page;

/**
 * The Python interpreter based on <a href="www.jython.org">jython</a>.
 * 
 * <p>Ludovic Drolez implemented the first Jython interpreter for ZK on May 2007:
 * http://zkforge.svn.sourceforge.net/viewvc/zkforge/trunk/scripting/jython/src/org/zkforge/scripting/jython/
 *
 * <p>Inspired by Drolez's job, Grace Lin wrote this class on March 2008
 * 
 * @author gracelin
 * @since 3.0.4
 */
public class JythonInterpreter extends GenericInterpreter {
	PythonInterpreter _ip;

	public JythonInterpreter() {
	}

	//super//
	public void init(Page owner, String zslang) {
		super.init(owner, zslang);
		if (System.getProperty("python.home") == null)
			System.setProperty("python.home", System.getProperty("java.io.tmpdir"));
		
		PySystemState.initialize();
		PySystemState.add_extdir(owner.getDesktop().getWebApp().getRealPath("/WEB-INF/lib"), true);
		PySystemState.add_classdir(owner.getDesktop().getWebApp().getRealPath("/WEB-INF/classes"));
		_ip = new PythonInterpreter(new Variables());
	}

	protected void exec(String script) {
		_ip.exec(script);
	}

	public void destroy() {
		_ip.cleanup();
		_ip = null;
		super.destroy();
	}

	protected Object get(String name) {
		//Bug 2208873: Don't use _ip.get(String, Object) since it
		//doesn't handle null well
		PyObject val = _ip.get(name);
		return val != null ? Py.tojava(val, Object.class): null;
	}

	protected void set(String name, Object value) {
		if (value == null) _ip.set(name, (PyObject)null);
		else _ip.set(name, value);
	}

	protected void unset(String name) {
		_ip.set(name, Py.None);
	}

	public Object getNativeInterpreter() {
		return _ip;
	}

	//helper classes//
	/** The global scope. */
	private class Variables extends PyStringMap {
		public synchronized PyObject __finditem__(String key) {
			PyObject pyo = super.__finditem__(key);

			if (pyo == null) { // use "null" without "Py.None", because we override __finditem__
				Object val = getFromNamespace(key);
				if (val != UNDEFINED)
					return Py.java2py(val);
			}
			return pyo;
		}
	}
}
