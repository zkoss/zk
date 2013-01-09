/* BindingExecutionInfoCollectorFactory.java

	Purpose:
		
	Description:
		
	History:
		2013/1/7 Created by Dennis Chen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger;

import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.debugger.impl.DefaultExecutionInfoCollectorFactory;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.UiException;
/**
 * the factory to provide {@link BindingExecutionInfoCollector} instance.
 * it is also possible to provide null instance depends on the configuration(a production env should always provide nothing) 
 * @author dennis
 * @since 6.5.2
 */
public abstract class BindingExecutionInfoCollectorFactory {

	private static BindingExecutionInfoCollectorFactory _factory;
	private static boolean _instanceSet;
	
	public static final String ENABLE_PROP = "org.zkoss.bind.BindingExecutionInfoCollector.enable";
	public static final String FACTORY_CLASS_PROP = "org.zkoss.bind.BindingExecutionInfoCollectorFactory.class";
	
	/**
	 * Get the collector of binder, the sub-class have to consider the thread-safe issue when implementing.
	 * @return the BindingExecutionInfoCollector or null if isn't existed
	 */
	abstract public BindingExecutionInfoCollector getCollector(Binder binder);

	/**  
	 * Thread safe method to get the factory instance
	 * @return default factory, null if there is no factory existed
	 */
	public static BindingExecutionInfoCollectorFactory getInstance(){
		if(_instanceSet){
			return _factory;
		}
		synchronized(BindingExecutionInfoCollectorFactory.class){
			if(_instanceSet){//check again
				return _factory;
			}
			_instanceSet = true;
			
			if("true".equals(Library.getProperty(ENABLE_PROP))){
				String clz = Library.getProperty(FACTORY_CLASS_PROP);
				if(!Strings.isEmpty(clz)){
					try {
						_factory = (BindingExecutionInfoCollectorFactory)Classes.forNameByThread(clz).newInstance();
					} catch (Exception e) {
						throw new UiException(e.getMessage(),e);
					}
				}else{ 
					_factory = new DefaultExecutionInfoCollectorFactory();
				}
			}
			return _factory;
		}
	}
}
