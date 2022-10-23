/* B85_ZK_3268VM.java

        Purpose:
                
        Description:
                
        History:
                Thu May 24 5:30 PM:13 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.util.Clients;

public class F85_ZK_3272VM {
	private Media photo;
	private List<String> attachments;
	
	@Init
	public void init() throws IOException{
		attachments = new ArrayList<String>();
	}
	
	@Command
	@NotifyChange("photo")
	public void doUploadPhoto(@BindingParam("photo") Media photo) {
		if (!photo.getContentType().startsWith("image/")) {
			Clients.showNotification("Please upload an image");
			return;
		}
		this.photo = photo;
	}
	
	@Command
	@NotifyChange("attachments")
	public void doUploadFiles(@BindingParam("files") Media[] files) {
		for (Media file : files) {
			attachments.add(file.getName());
		}
	}
	
	public Media getPhoto() {
		return photo;
	}
	
	public List<String> getAttachments() {
		return attachments;
	}
}
