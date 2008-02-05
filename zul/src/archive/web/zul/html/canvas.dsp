<%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>

<c:set var="self" value="${requestScope.arg.self}"/>
 
 <canvas id="${self.uuid}"${self.outerAttrs} z.type="zul.canvas.DrawCanvas" width="${self.width}" height="${self.height}"></canvas>

 