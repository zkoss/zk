/* OperationQueue.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 29, 2007 9:20:26 AM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkex.zul.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.zkoss.zk.ui.Desktop;

/**
 * A queue for storing {@link Operation} and is thread-safe.
 * This class is for model sharer developer only, you rarely need to use this class.<br/>
 * 
 * @author Dennis.Chen
 * @since 3.0.0
 */
public class OperationQueue {
	
	private LinkedList _inner = new LinkedList();
	private List _listeners = Collections.synchronizedList(new LinkedList());
	
	/**
	 * Add a listener to this queue
	 */
	public void addListener(OperationQueueListener listener){
		synchronized(_listeners){
			_listeners.add(listener);
		}
	}
	
	/**
	 * Remove a listener to this queue.
	 */
	public void removeListener(OperationQueueListener listener){
		synchronized(_listeners){
			_listeners.remove(listener);
		}
	}
	
	void fireQueueUnavailable(Desktop desktop){
		synchronized(_listeners){
			//do not use iterator, some listener will remove it registration when queue is not available
			OperationQueueListener[] ls = (OperationQueueListener[])_listeners.toArray(new OperationQueueListener[0]);
			for(int i=0;i<ls.length;i++){
				ls[i].queueUnavailable(desktop);
			}
		}
	}
	
	
	void clearListener(){
		synchronized(_listeners){
			_listeners.clear();
		}
	}
	
	/**
	 * Put an operation to queue
	 * @param op the operation.
	 */
	public void put(Operation op){
		synchronized(_inner){
			_inner.add(op);
		}
		synchronized(this){
			this.notifyAll();
		}
	}
	
	/**
	 * Get an operation from queue, doesn't remove it.
	 * @return the first operation in queue, null if not such operation. 
	 */
	public Operation element(){
		try{
			synchronized(_inner){
				if(_inner.size()>0){
					Operation op = (Operation)_inner.getFirst();
					return op;
				}
				return null;
			}
		}catch(NoSuchElementException e){
			return null;
		}
	}
	
	/**
	 * Check is there any operation in queue.
	 * @return true if there exist any operation in queue.
	 */
	public boolean hasElement(){
		return _inner.size()>0;
	}
	
	/**
	 * Get an operation from queue, and then remove it.
	 * @return the first operation in queue, null if not such operation. 
	 */
	public Operation next(){
		try{
			synchronized(_inner){
				if(_inner.size()>0){
					Operation op = (Operation)_inner.getFirst();
					_inner.removeFirst();
					return op;
				}
				return null;
			}
		}catch(NoSuchElementException e){
			return null;
		}
	}
	
	/**
	 * Remove the first operation in queue if exist.
	 */
	public void remove(){
		next();
	}
	
	
}
