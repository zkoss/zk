package org.zkoss.zktest.test2;

import java.time.LocalTime;
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
		RecordData data1 = new RecordData("initial");
		dataMap.put("uid1", data1);
		dataMap.put("uid2", new RecordData("initial"));
		dataMap.put("uid3", data1);
	}

	@Command
	public void updateValue(@BindingParam("uid") String uid) {
		RecordData recordData = dataMap.get(uid);
		recordData.setValue(LocalTime.now().toString());
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
		private String value;

		public RecordData(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}

