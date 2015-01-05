/** FormWithSet.java.

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
import java.util.LinkedHashSet;
import java.util.Set;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.SimpleForm;
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
public class FormWithSet implements Serializable{

	private String dbItemName = "screw";
	private String dbMainTagValue = "tool";
	private Set<String> dbTagValues = new LinkedHashSet<String>(){{
			add("metal");
			add("construction");
			add("small");
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
	public void onAddTag(@BindingParam("form") SimpleForm form,
			@BindingParam("tagValue") String tagValue) {
		Set<Tag> tags = (Set<Tag>) form.getField("tags");
		tags.add(new Tag(tagValue));
		BindUtils.postNotifyChange(null, null, form, "tags");
	}

	@Command("removeTag")
	public void onRemoveTag(@BindingParam("form") SimpleForm form,
			@BindingParam("tag") Tag tag) {
		Set<Tag> tags = (Set<Tag>) form.getField("tags");
		tags.remove(tag);
		BindUtils.postNotifyChange(null, null, form, "tags");
	}

	private void loadCurrentItem() {
		currentItem = new Item();
		currentItem.setName(dbItemName);
		currentItem.setMainTag(new Tag(dbMainTagValue));
		currentItem.setTags(new LinkedHashSet<Tag>());
		for (String tagValue : dbTagValues) {
			currentItem.getTags().add(new Tag(tagValue));
		}
	}

	private void saveCurrentItem() {
		dbItemName = currentItem.getName();
		dbMainTagValue = currentItem.getMainTag().getValue();
		dbTagValues.clear();
		for (Tag tag : currentItem.getTags()) {
			dbTagValues.add(tag.getValue());
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
		private Set<Tag> tags;
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

		public Set<Tag> getTags() {
			return tags;
		}

		public void setTags(Set<Tag> tags) {
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
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return value;
		}
	}
}