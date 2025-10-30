package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;

public class B96_ZK_5399VM {
	private ListModelList<Integer> numbers;

	@Init
	public void init() {
		this.numbers = new ListModelList<>();
		this.numbers.add(1);
	}

	@GlobalCommand
	public void refresh() {
		this.numbers.set(0, this.numbers.get(0) + 1);
	}

	public ListModelList<Integer> getNumbers() {
		return numbers;
	}
}