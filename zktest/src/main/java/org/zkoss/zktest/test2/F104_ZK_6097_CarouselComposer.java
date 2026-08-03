/* F104_ZK_6097_CarouselComposer.java

	Purpose:

	Description:

	History:
		Wed May 13 13:05:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Carousel;
import org.zkoss.zul.Label;

public class F104_ZK_6097_CarouselComposer extends SelectorComposer<Component> {

	@Wire
	private Carousel crMvc;
	@Wire
	private Label mvcSelResult;
	@Wire
	private Label mvcChangingResult;

	@Listen("onSelect = #crMvc")
	public void onCarouselSelect(Event event) {
		@SuppressWarnings("unchecked")
		Map<String, Object> data = (Map<String, Object>) event.getData();
		int idx = ((Number) data.get("index")).intValue();
		mvcSelResult.setValue("i=" + idx);
	}

	@Listen("onChanging = #crMvc")
	public void onCarouselChanging(InputEvent event) {
		int from = ((Number) event.getPreviousValue()).intValue();
		int to = Integer.parseInt(event.getValue());
		mvcChangingResult.setValue("from=" + from + " to=" + to);
	}

	@Listen("onClick = #btnNextMvc")
	public void next() {
		int idx = crMvc.getActiveIndex();
		if (idx + 1 < crMvc.getChildren().size()) crMvc.setActiveIndex(idx + 1);
	}

	@Listen("onClick = #btnPrevMvc")
	public void prev() {
		int idx = crMvc.getActiveIndex();
		if (idx > 0) crMvc.setActiveIndex(idx - 1);
	}
}
