/* SimpleListModelSharer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct 9, 2007 2:26:29 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkex.zul;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.zkoss.lang.D;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zkex.zul.impl.Operation;
import org.zkoss.zkex.zul.impl.OperationQueue;
import org.zkoss.zkex.zul.impl.OperationQueueListener;
import org.zkoss.zkex.zul.impl.OperationThread;

/**
 * {@linkplain SimpleListModelSharer} is a simple implementation of {@link ListModelSharer}<br/>
 * 
 * To use this class, you should create a global {@link ListModel} first, 
 * and then create {@linkplain SimpleListModelSharer} with the global list model.
 * <pre><code>
 * ListModel globalModel = new ListModelList();
 * SimpleSharedListModel sharedModel = new SimpleSharedListModel(globalModel);
 * </code></pre>
 * Then, in each desktop, you get a proxy by call {@link SimpleListModelSharer#getProxy(Desktop)} and associate it to listbox or gird.
 * <pre><code>
 * ListModel model = sharedModel.getProxy(desktop);
 * listbox.setModel(model);
 * </code></pre>
 * 
 * @author Dennis.Chen
 * @since 3.0.0
 */
public class SimpleListModelSharer implements ListModelSharer{

	private static final Log log = Log.lookup(SimpleListModelSharer.class);
	
	static private final int OP_ADD = 1;
	static private final int OP_REMOVE = 2;
	static private final int OP_SET = 3;
	
	private List _proxys = Collections.synchronizedList(new LinkedList());
	
	
	private List _innerData;
	private ListModel _srcModel;
	private ListDataListener _srcListener;
	
	/**
	 * @param model the model to be shared to different desktop.
	 */
	public SimpleListModelSharer(ListModel model){
		_srcModel = model;
		init();
	}

	private void init() {
		_innerData = Collections.synchronizedList(new LinkedList());
		int size = _srcModel.getSize();
		for(int i=0;i<size;i++){
			_innerData.add(_srcModel.getElementAt(i));
		}
		
		_srcListener = new ListDataListener(){
			public void onChange(ListDataEvent event) {
				onListDataChnage(event);
			}
		};
		
		_srcModel.addListDataListener(_srcListener);
	}
	
	private void onListDataChnage(ListDataEvent event){
		
		int type = event.getType();
		ListModel model = event.getModel();
		if(_srcModel!=model) return;
		int index0 = event.getIndex0();
		int index1 = event.getIndex1();
		
		int min = (index0>index1)?index1:index0;
		int max = (index0>index1)?index0:index1;
		int start,end;
		switch(type){
		case ListDataEvent.CONTENTS_CHANGED:
			start=(min<0)?0:min;
			end=(max<0)?_srcModel.getSize():max;
			//TODO a smart way for special range
			for(int i=start;i<=end;i++){
				Object obj = _srcModel.getElementAt(i);
				_innerData.set(i,obj);
				putToQueue(OP_SET,new Object[]{new Integer(i),obj});
			}
			break;
		case ListDataEvent.INTERVAL_ADDED:
			start=(min<0)?0:min;
			end=(max<0)?_srcModel.getSize():max;
			//TODO a smart way for special range,e.g., 0 to n 
			for(int i=start;i<=end;i++){
				Object obj = _srcModel.getElementAt(i);
				_innerData.add(i,obj);
				putToQueue(OP_ADD,new Object[]{new Integer(i),obj});
			}
			break;
		case ListDataEvent.INTERVAL_REMOVED:
			start=(min<0)?0:min;
			end=(max<0)?_srcModel.getSize():max;
			//TODO a smart way for special range,e.g., 0 to size
			for(int i=end;i>=start;i--){
				_innerData.remove(i);
				putToQueue(OP_REMOVE,new Object[]{new Integer(i)});
			}
			break;
		default :
			throw new IllegalStateException("Unknow Event Type:"+type);
		}
	}
	
	
	/**
	 * Get a proxy which is to be used in listbox or grid of a desktop.
	 * @param desktop a desktop
	 * @return a ListModel proxy
	 */
	public ListModel getProxy(Desktop desktop){
		if(D.ON && log.debugable()){
			log.debug("create proxy model for:"+desktop);
		}
		ProxyModel proxy;
		QueueListener oql;
		//TODO check is there same proxy in desktop for more effective
		synchronized(_proxys){
			proxy = new ProxyModel(_innerData);
			
			oql = new QueueListener(desktop,proxy);
			proxy.setOperationQueueListener(oql);
			
			OperationQueue queue = OperationThread.getQueue(desktop);
			queue.addListener(oql);
			proxy.setQueue(queue);
			
			_proxys.add(proxy);
		}
		return proxy;
	}

	/**
	 * Get the count of created proxy.
	 * @return the created proxy count
	 */
	public int getProxyCount() {
		synchronized(_proxys){
			return _proxys.size();
		}
	}
	
	private void destroyProxy(Desktop desktop,ListModel model,boolean rmQueueListener) {
		if(!(model instanceof ProxyModel)){
			throw new IllegalArgumentException("Not a created proxy model:"+model.getClass());
		}
		synchronized(_proxys){
			if(_proxys.remove(model)){
				if(D.ON && log.debugable()){
					log.debug("destory proxy model for:"+desktop);
				}
				((ProxyModel)model).clear();
			}
			
		}
		
	}
	
	private void putToQueue(int op, Object[] parms) {
		synchronized(_proxys){
			Iterator iter = _proxys.iterator();
			while (iter.hasNext()) {
				ProxyModel model = (ProxyModel) iter.next();
				ListModelOperation lmop = new ListModelOperation(op,parms,model);
				OperationQueue queue = model.getQueue();
				if(queue!=null){
					queue.put(lmop);
				}
			}
		}
	}
	
	private class QueueListener implements OperationQueueListener{
		Desktop _desktop;
		ProxyModel _proxy;
		QueueListener(Desktop desktop,ProxyModel proxy){
			this._desktop = desktop;
			this._proxy = proxy;
		}
		
		public void queueUnavailable(Desktop desktop) {	
			if(_desktop == desktop){
				//since queue of _model is unavailable , i must destroy this model.
				//i don't remove listener, queue will clean it after all queue unavailable event.
				destroyProxy(desktop,_proxy,false);
			}
		}
		
	}
	
	
	/**
	 * A Operation implementation.
	 */
	private class ListModelOperation implements Operation {

		int _op;
		Object[] _parms;
		ProxyModel _model;
		
		ListModelOperation(int op, Object[] parms,ProxyModel model){
			this._op = op;
			this._parms = parms;
			this._model = model;
		}

		public void execute(Desktop _desktop) {
			switch (_op) {
			case OP_ADD:
				_model.add(((Integer) _parms[0]).intValue(), _parms[1]);
				break;
			case OP_REMOVE:
				_model.remove(((Integer) _parms[0]).intValue());
				break;
			case OP_SET:
				_model.set(((Integer) _parms[0]).intValue(), _parms[1]);
				break;
			default:
				throw new UnsupportedOperationException("Unknow operation:"
								+ _op);
			}
		}

		public void failToExecute(Desktop _desktop) {
			destroyProxy(_desktop,_model,true);
		}

		
	}
	
	
	/**
	 * A proxy model implementation
	 */
	static private class ProxyModel extends AbstractListModel{

		private OperationQueue _queue;
		private OperationQueueListener _oqListener;
		List _proxyedData;
		
		//private Object hashObj = new Object();
		
		OperationQueue getQueue(){
			return _queue;
		}
		
		void setQueue(OperationQueue queue){
			this._queue = queue;
		}
		
		ProxyModel(Collection c) {
			_proxyedData = Collections.synchronizedList(new LinkedList(c));
		}

		void clear(){
			//queue maybe null if there are several operations which be called fail to failToExecute 
			if(_queue!=null && _oqListener!=null){
				_queue.removeListener(_oqListener);
			}
			_queue = null;
			_oqListener = null;
			_proxyedData.clear();
		}

		void add(int index, Object element) {
			_proxyedData.add(index,element);
			fireEvent(ListDataEvent.INTERVAL_ADDED, index,index);
		}

		
		Object remove(int index) {
			Object obj = _proxyedData.remove(index);
			fireEvent(ListDataEvent.INTERVAL_REMOVED, index,index);
			return obj;
		}

		Object set(int index, Object element) {
			Object obj = _proxyedData.set(index,element);
			fireEvent(ListDataEvent.CONTENTS_CHANGED, index,index);
			return obj;
		}

		void setOperationQueueListener(OperationQueueListener oql) {
			_oqListener = oql;
		}
		

		public Object getElementAt(int index) {
			return _proxyedData.get(index);
		}

		public int getSize() {
			return _proxyedData.size();
		}
	}
	
	
}
