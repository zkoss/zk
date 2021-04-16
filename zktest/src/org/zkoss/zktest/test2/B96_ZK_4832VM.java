package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;

public class B96_ZK_4832VM {
	private Map<String, RecordData> dataMap = new HashMap<>();
	private List<String> uids = Arrays.asList("uid1", "uid2", "uid3");

	@Init
	public void init() {
		RecordData data1 = new RecordData(0);
		dataMap.put("uid1", data1);
		dataMap.put("uid2", new RecordData(0));
		dataMap.put("uid3", data1);
	}

	@Command
	public void updateValue(@BindingParam("uid") String uid) {
		RecordData recordData = dataMap.get(uid);
		recordData.setValue(recordData.getValue() + 1);
		BindUtils.postNotifyChange(null, null, recordData, "value");
		//BindUtils.postNotifyChange(null, null, dataMap, uid); // also doesn't update
		//BindUtils.postNotifyChange(null, null, dataMap, "*"); // updates both
//		Clients.log("new value for " + uid + " :" + recordData.getValue());
	}

	public Map<String, RecordData> getDataMap() {
		return dataMap;
	}

	public List<String> getUids() {
		return uids;
	}

	public static class RecordData {
		private int value;

		public RecordData(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
	}
}

