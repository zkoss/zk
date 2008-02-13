package org.zkoss.zkmax.scripting.jython;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyStringMap;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.zkoss.zk.scripting.util.GenericInterpreter;
import org.zkoss.zk.ui.Page;

/**
 * Python Interpreter
 * 
 * @author gracelin
 * @since 3.1.0
 */
public class JythonInterpreter extends GenericInterpreter {
	PythonInterpreter _interpreter;

	public JythonInterpreter() {
	}

	@Override
	public void init(Page owner, String zslang) {
		super.init(owner, zslang);

		PySystemState.initialize();
		_interpreter = new PythonInterpreter(new Variables());
	}

	@Override
	protected void exec(String script) {
		_interpreter.exec(script);
	}

	@Override
	public void destroy() {
		_interpreter.cleanup();
		_interpreter = null;
		super.destroy();
	}

	@Override
	protected Object get(String name) {
		return _interpreter.get(name, Object.class);
	}

	@Override
	protected void set(String name, Object value) {
		_interpreter.set(name, value);
	}

	@Override
	protected void unset(String name) {
		_interpreter.set(name, Py.None);
	}

	/** Returns the native interpreter, or null if it is not initialized
	 * or destroyed.
	 * 
	 * @since 3.1.0
	 */
	public Object getNativeInterpreter() {
		return _interpreter;
	}

	/** The global scope. */
	private class Variables extends PyStringMap {
		private static final long serialVersionUID = 20080213L;

		public Variables() {
			super();
		}

		@Override
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
