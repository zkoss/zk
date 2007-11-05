<%--
odeo.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 24 12:01:12     2007, Created by jeffliu
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" ${self.outerAttrs} z.type="zul.odeo.Odeo" >
<embed 
id="${self.uuid}!emb"
src="http://www.odeo.com/flash/audio_player_${self.skinSize}_${self.skinColor}.swf" 
quality="high" 
width="${self.width}" 
height="${self.height}" 
name="audio_player__${self.skinSize}_${self.skinColor}" 
align="left" 
allowScriptAccess="always" 
wmode="transparent"  
type="application/x-shockwave-flash" 
flashvars="audio_duration=${self.audioDuration}&valid_sample_rate=
true&external_url=${self.songUrl}" 
pluginspage="http://www.macromedia.com/go/getflashplayer" 
Hidden="${!self.visible}"
/>
</div>
