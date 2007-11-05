<%--
zspfPlayer.dsp

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

<div id="${self.uuid}" ${self.outerAttrs} z.type="zul.xspfPlayer.Xspf">

<c:if test="${!c:isSafari()}"> 
<object id="${self.uuid}!obj" classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000"
type="application/x-shockwave-flash"
<c:if test="${!self.visible}">
width="0px" height="0px" 
</c:if>
<c:if test="${self.visible}">
width="400" height="18" 
</c:if>
>
        <param name="allowScriptAccess" value="sameDomain"/>
        <param name="movie" value="${c:encodeURL('~./zul/SWF/xspf_player_slim.swf')}?song_url=${self.songUrl}"/>
        <param name="quality" value="high"/>
        <param name="bgcolor" value="#E6E6E6"/>
</c:if>     
<embed id="${self.uuid}!emb" src="${c:encodeURL('~./zul/SWF/xspf_player_slim.swf')}?song_url=${self.songUrl}" type="application/x-shockwave-flash" align="left" height="18" width="400" Hidden=${!self.visible} >
</embed>
<c:if test="${!c:isSafari()}"> 
</object>
</c:if>  
</div>
