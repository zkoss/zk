<%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>

<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}"  ${self.outerAttrs} style="height:${self.height};width:${serlf.width};" z.type="zul.flash.Flash">
<object id="${self.uuid}!obj"  width="${self.width}" height="${self.height}">
<param name="movie" value="${self.src}"></param>
<param name="wmode" value="transparent"></param>
<embed id="${self.uuid}!emb" src="${self.src}" type="application/x-shockwave-flash" wmode="transparent" width="${self.width}" height="${self.height}">
</embed>
</object>
</div>