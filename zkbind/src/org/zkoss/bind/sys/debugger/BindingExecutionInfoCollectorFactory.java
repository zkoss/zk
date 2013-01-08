/* BindingExecutionInfoCollectorFactory.java

	Purpose:
		
	Description:
		
	History:
		2013/1/7 Created by Dennis Chen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger;

import org.zkoss.bind.sys.debugger.impl.DefaultCollectorFactory;
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

	static private BindingExecutionInfoCollectorFactory factory;
	static private boolean instanceSet;
	//TODO check zk naming pattern
	static public final String ENABLE_KEY = BindingExecutionInfoCollectorFactory.class.getName()+".enable";
	static public final String FACTORY_CLASS_KEY = BindingExecutionInfoCollectorFactory.class.getName()+".class";
	
	/**
	 * get the collector, the sub-class should consider the thread-safe issue when implementing.
	 * @return
	 */
	abstract public BindingExecutionInfoCollector getCollector();

	/**  
	 * thread safe method to get default factory
	 * @return default factory, null if there is no factory
	 */
	public static BindingExecutionInfoCollectorFactory getDefaultFactory(){
		if(instanceSet){
			return factory;
		}
		synchronized(BindingExecutionInfoCollectorFactory.class){
			if(instanceSet){//check again
				return factory;
			}
			instanceSet = true;
			
			if("true".equals(Library.getProperty(ENABLE_KEY))){
				String clz = Library.getProperty(FACTORY_CLASS_KEY);
				if(!Strings.isEmpty(clz)){
					try {
						factory = (BindingExecutionInfoCollectorFactory)Classes.forNameByThread(clz).newInstance();
					} catch (Exception e) {
						throw new UiException(e.getMessage(),e);
					}
					//TODO check zk naming pattern
				}else{ 
					factory = new DefaultCollectorFactory();
				}
			}
			return factory;
		}
	}
	
	/**  
	 * thread safe method to get default collector
	 * @return default collector, null if there is no factory or no collector
	 */
	public static BindingExecutionInfoCollector getDefaultCollector(){
		BindingExecutionInfoCollectorFactory factory = getDefaultFactory();
		return factory==null?null:factory.getCollector();
	}

}
