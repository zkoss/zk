/* FileData.java

	Purpose:

	Description:

	History:
		Wed Dec 29 11:40:55 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/

/**
 * Represents file data from an action
 * @author katherine
 */
package org.zkoss.zephyr.action.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;

import org.zkoss.util.media.Media;

/**
 * Represents file data from an action
 * @author katherine
 */
public class FileData implements ActionData {
	private List<Media> medias;

	@JsonCreator
	protected FileData(Map data) {
		Object file = data.get("file");

		// for client widget event
		if (file != null) {
			if (file instanceof List) {
				medias = ((List<Media>) file);
			} else if (file instanceof Media) {
				medias = Arrays.asList((Media) file);
			} else {
				medias = Collections.EMPTY_LIST;
			}
		} else {
			// for @ActionVarible case
			medias = (List<Media>) data.values().stream().filter(f -> f instanceof Media).collect(
					Collectors.toList());
		}
	}

	/**
	 * return the media if any.
	 */
	public Media getMedia() {
		if (medias.isEmpty())
			return null;
		return medias.get(0);
	}

	/**
	 * return the medias if any.
	 */
	public List<Media> getMedias() {
		return medias;
	}

	/**
	 * @hidden for Javadoc
	 */
	public String toString() {
		return "FileData{" + "medias=" + medias + '}';
	}
}