package org.zkoss.zkmax.scripting.jython;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyStringMap;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.zkoss.zk.scripting.util.GenericInterpreter;
import org.zkoss.zk.ui.Page;

/**
 * The Python interpreter based on <a href="www.jython.org">jython</a>
 * 
 * @author gracelin
 * @since 3.0.4
 */
public class JythonInterpreter extends GenericInterpreter {
	PythonInterpreter _interpreter;

	public JythonInterpreter() {
	}

	//super//
	public void init(Page owner, String zslang) {
		super.init(owner, zslang);

		PySystemState.initialize();
		_interpreter = new PythonInterpreter(new Variables());
	}

	protected void exec(String script) {
		_interpreter.exec(script);
	}

	public void destroy() {
		_interpreter.cleanup();
		_interpreter = null;
		super.destroy();
	}

	protected Object get(String name) {
		return _interpreter.get(name, Object.class);
	}

	protected void set(String name, Object value) {
		_interpreter.set(name, value);
	}

	protected void unset(String name) {
		_interpreter.set(name, Py.None);
	}

	public Object getNativeInterpreter() {
		return _interpreter;
	}

	//helper classes//
	/** The global scope. */
	private class Variables extends PyStringMap {
		public Variables() {
			super();
		}

		public synchronized PyObject __finditem__(String key) {
			PyObject pyo = super.__finditem__(key);

			if (pyo == null) { // use "null" without "Py.None", because we override __finditem__
				Object val = getFromNamespace(key);
				if (val != UNDEFINED)
					return toPy(val);
			}
			return pyo;
		}
	}

	private PyObject toPy(Object value) {
		return Py.java2py(value);
	}
}
