/** B02078.java.

	Purpose:
		
	Description:
		
	History:
		4:08:37 PM Dec 29, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Converter;
import org.zkoss.bind.SimpleForm;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.proxy.FormProxyObject;
import org.zkoss.zk.ui.Component;

/**
 * @author jumperchen
 */
public class B02078 {

	private String dbItemName = "screw";
	private String dbMainTagValue = "tool";
	private List<String> dbTagValues = new ArrayList<String>(Arrays.asList(
			"metal", "construction", "small"));

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

	}

	@Command("reload")
	@NotifyChange("currentItem")
	public void onReload() {
		loadCurrentItem();
	}

	@Command("addTag")
	public void onAddTag(@BindingParam("form") SimpleForm form,
			@BindingParam("tagValue") String tagValue) {
		List<Tag> tags = (List<Tag>) form.getField("tags");
		tags.add(new Tag(tagValue));
		BindUtils.postNotifyChange(null, null, form, "tags");
	}

	@Command("removeTag")
	public void onRemoveTag(@BindingParam("form") SimpleForm form,
			@BindingParam("tag") Tag tag) {
		List<Tag> tags = (List<Tag>) form.getField("tags");
		tags.remove(tag);
		BindUtils.postNotifyChange(null, null, form, "tags");
	}

	private void loadCurrentItem() {
		currentItem = new Item();
		currentItem.setName(dbItemName);
		currentItem.setMainTag(new Tag(dbMainTagValue));
		currentItem.setTags(new ArrayList<Tag>());
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
	public static class Item {

		private String name;
		private Tag mainTag;
		private List<Tag> tags;
		
		public Item() {}
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
		
		public List<Tag> getTags() {
			return tags;
		}
		
		public void setTags(List<Tag> tags) {
			this.tags = tags;
		}
	}
	public static class Tag {
		private String value;

		public Tag() {}
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
