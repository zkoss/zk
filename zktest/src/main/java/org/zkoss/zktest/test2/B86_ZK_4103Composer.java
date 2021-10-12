/* B86_ZK_4103Composer.java

        Purpose:
                
        Description:
                
        History:
                Fri Oct 26 12:23:26 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.zul.Video;
import org.zkoss.zul.Audio;

public class B86_ZK_4103Composer extends SelectorComposer {
	@Wire
	private Video video;
	@Wire
	private Audio audio;

	@Listen("onUpload = #videoUpload")
	public void videoUpload(UploadEvent event) {
		video.setContent((org.zkoss.video.Video) event.getMedia());
	}

	@Listen("onUpload = #audioUpload")
	public void audioUpload(UploadEvent event) {
		audio.setContent((org.zkoss.sound.Audio) event.getMedia());
	}
}
