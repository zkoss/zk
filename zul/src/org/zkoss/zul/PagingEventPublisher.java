package org.zkoss.zul;

import org.zkoss.zul.event.PagingListener;

public interface PagingEventPublisher {
	public static final String INTERNAL_EVENT = "internalModelEvent";
	public void addPagingEventListener(PagingListener l);
	public void removePagingEventListener(PagingListener l);
}
