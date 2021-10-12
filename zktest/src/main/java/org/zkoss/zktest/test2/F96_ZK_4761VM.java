package org.zkoss.zktest.test2;

import java.lang.reflect.Field;
import java.util.Map;

import org.zkoss.bind.BindComposer;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Destroy;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.HistoryPopState;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.HistoryPopStateEvent;
import org.zkoss.zk.ui.util.Clients;

public class F96_ZK_4761VM {

	private String result = "";

	private int resultNum = 0;

	@Init
	public void init() {
	}

	@Command("test")
	@NotifyChange({"result", "resultNum"})
	public void doTest() {
		result = "";
		resultNum = 0;
		logMap(BinderImpl.class, "_initMethodCache");
		logMap(BinderImpl.class, "_destroyMethodCache");
		logMap(BinderImpl.class, "_commandMethodCache");
		logMap(BinderImpl.class, "_globalCommandMethodCache");
		logMap(BindComposer.class, "_afterComposeMethodCache");
		logMap(BindComposer.class, "_historyPopStateMethodCache");
	}

	@GlobalCommand
	public void doTestGlobal() {
		Clients.log("Global test");
	}

	@Destroy
	public void destroy() {
	}

	@HistoryPopState
	public void handleHistoryPopState(@ContextParam(ContextType.TRIGGER_EVENT) HistoryPopStateEvent event) {
		Clients.log(event.getState());
	}

	private void logMap(Object obj, String fieldName) {
		int size = ((Map) getField(obj, fieldName)).size();
		resultNum += size;
		result += "Size of " + fieldName + ": " + size + "\n";
	}

	private <T> T getField(Object obj, String fieldName) {
		try {
			Class cls = obj instanceof Class ? (Class) obj : obj.getClass();
			Field field = cls.getDeclaredField(fieldName);
			field.setAccessible(true);
			return (T) field.get(obj);
		} catch (Exception e) {
			throw new UiException("Fail to get Field", e);
		}
	}

	public String getResult() {
		return result;
	}

	public int getResultNum() {
		return resultNum;
	}
}
