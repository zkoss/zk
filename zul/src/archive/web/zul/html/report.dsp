<%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>

<c:set var="self" value="${requestScope.arg.self}"/>

<iframe id="${self.uuid}"${self.outerAttrs} >
</iframe>