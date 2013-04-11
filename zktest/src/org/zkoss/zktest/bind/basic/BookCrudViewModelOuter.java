package org.zkoss.zktest.bind.basic;

import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class BookCrudViewModelOuter {

	String src = "/bind/basic/bookCrud.zul";

	public String getSrc(){
		return src;
	}
}
