/** FormWithMap.java.

	Purpose:
		
	Description:
		
	History:
		4:13:01 PM Dec 31, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.bind.viewmodel.form;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

/**
 * @author jumperchen
 */
public class FormWithMap implements Serializable{

	private String dbItemName = "screw";
	private String dbMainTagValue = "tool";
	private Map<String, String> dbTagValues = new LinkedHashMap<String, String>(){{
			put("metal", "metal");
			put("construction", "construction");
			put("small", "small");
			}};

	private Item currentItem;

	@Init
	public void init() {
		loadCurrentItem();
	}

	@Command("save")
	@NotifyChange("currentItem")
	public void onSave() {
		saveCurrentItem();
	}

	@Command("cancel")
	@NotifyChange("currentItem")
	public void onCancel() {
		this.getCurrentItem().newTagValue = "";
	}

	@Command("reload")
	@NotifyChange("currentItem")
	public void onReload() {
		loadCurrentItem();
	}

	@Command("addTag")
	public void onAddTag(@BindingParam("form") Item form,
			@BindingParam("tagValue") String tagValue) {
		Map<String, Tag> tags = form.getTags();
		tags.put(tagValue, new Tag(tagValue));
		BindUtils.postNotifyChange(null, null, form, "tags");
	}

	@Command("removeTag")
	public void onRemoveTag(@BindingParam("form") Item form,
			@BindingParam("tag") Tag tag) {
		Map<String, Tag> tags = form.getTags();
		tags.remove(tag.getValue());
		BindUtils.postNotifyChange(null, null, form, "tags");
	}

	private void loadCurrentItem() {
		currentItem = new Item();
		currentItem.setName(dbItemName);
		currentItem.setMainTag(new Tag(dbMainTagValue));
		currentItem.setTags(new LinkedHashMap<String, Tag>());
		for (Map.Entry<String, String> me: dbTagValues.entrySet()) {
			currentItem.getTags().put(me.getKey(), new Tag(me.getValue()));
		}
	}

	private void saveCurrentItem() {
		dbItemName = currentItem.getName();
		dbMainTagValue = currentItem.getMainTag().getValue();
		dbTagValues.clear();
		for (Map.Entry<String, Tag> me : currentItem.getTags().entrySet()) {
			dbTagValues.put(me.getKey(), me.getValue().getValue());
		}
	}

	public Item getCurrentItem() {
		return currentItem;
	}

	public void setCurrentItem(Item currentItem) {
		this.currentItem = currentItem;
	}
	byte[] _bytes;
	public void doSerialize(Window win,Label msg){
		try{
			doSerialize0(win, msg);
			doDeserialize0(win, msg);
		}catch(Exception x){
			x.printStackTrace();
			msg.setValue("error :"+x.getClass()+","+x.getMessage());
		}
	}
	public void doSerialize0(Window win,Label msg) throws Exception{
		Page pg = win.getPage();
		((ComponentCtrl)win).sessionWillPassivate(pg);//simulate
		ByteArrayOutputStream oaos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(oaos);
		oos.writeObject(win);
		oos.close();
		oaos.close();
		_bytes = oaos.toByteArray();
	}
	
	public void doDeserialize0(Window win, Label msg) throws Exception{
		ByteArrayInputStream oaos = new ByteArrayInputStream(_bytes);
		ObjectInputStream oos = new ObjectInputStream(oaos);
		
		Window newwin = (Window) oos.readObject();
		Page pg = win.getPage();
		Component parent = win.getParent();
		Component ref = win.getNextSibling();
		win.detach();
		oos.close();
		oaos.close();
		parent.insertBefore(newwin, ref);
		//for load component back.
		((ComponentCtrl)newwin).sessionDidActivate(newwin.getPage());//simulate
		msg.setValue("done deserialize: "+_bytes.length);	
	}

	public static class Item  implements Serializable {

		private String name;
		private Tag mainTag;
		private Map<String, Tag> tags;
		private String newTagValue;

		public Item() {
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Tag getMainTag() {
			return mainTag;
		}

		public void setMainTag(Tag mainTag) {
			this.mainTag = mainTag;
		}

		public Map<String, Tag> getTags() {
			return tags;
		}

		public void setTags(Map<String, Tag> tags) {
			this.tags = tags;
		}
		public void setNewTagValue(String v) {
			newTagValue = v;
		}
		public String getNewTagValue() {
			return newTagValue;
		}
	}

	public static class Tag  implements Serializable{
		private String value;

		public Tag() {
		}

		public Tag(String value) {
			super();
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			final String value = getValue();
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Tag other = (Tag) obj;
			String value1 = getValue();
			String value2 = other.getValue();
			if (value1 == null) {
				if (value2 != null)
					return false;
			} else if (!value1.equals(value2))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return value;
		}
	}
}