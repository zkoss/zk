package org.zkoss.zktest.test2;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;

public class B95_ZK_4688VM {

	private String result = "";

	@GlobalCommand
	@NotifyChange("result")
	public void stopSetup(@BindingParam("finishDate") Date when, @BindingParam("setupQty") int setupQty,
			@BindingParam("reason") Locale reason) {
		result = when + "," + setupQty + "," + reason;
	}

	@Command
	public void triggerGlobalCmd() {
		Map<String, Object> params = new HashMap<>();
		params.put("finishDate", Calendar.getInstance().getTime());
		params.put("setupQty", 1);
		params.put("reason", null); // workaround: remove this line
		BindUtils.postGlobalCommand(null, null, "stopSetup", params);
	}

	@Command
	@NotifyChange("result")
	public void cmd(int index, Double number) {
		result = index + "," + number;
	}

	public String getResult() {
		return result;
	}
}