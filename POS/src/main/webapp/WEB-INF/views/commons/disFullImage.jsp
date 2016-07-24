<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp" %>
<%
   	String imageInfo  = pageContext.findAttribute("imageInfo")+"";
	String imageTypeName = pageContext.findAttribute("imageTypeName")+"";
%>
<c:if test="${imageSize ==0}">
<div class="image_message m_a">
<c:choose>
	<c:when test="${empty message}">
		暂无影像信息
	</c:when>
	<c:otherwise>		
		${message}
	</c:otherwise>
</c:choose>
</div> 
</c:if>
<c:if test="${imageSize !=0}">

<!--"CONVERTED_APPLET"-->
<!-- HTML CONVERTER -->
<script language="JavaScript" type="text/javascript"><!--
    var _info = navigator.userAgent;
    var _ns = false;
    var _ns6 = false;
    var _ie = (_info.indexOf("MSIE") > 0 && _info.indexOf("Win") > 0 && _info.indexOf("Windows 3.1") < 0);
//--></script>
    <comment>
        <script language="JavaScript" type="text/javascript"><!--
        var _ns = (navigator.appName.indexOf("Netscape") >= 0 && ((_info.indexOf("Win") > 0 && _info.indexOf("Win16") < 0 && java.lang.System.getProperty("os.version").indexOf("3.5") < 0) || (_info.indexOf("Sun") > 0) || (_info.indexOf("Linux") > 0) || (_info.indexOf("AIX") > 0) || (_info.indexOf("OS/2") > 0) || (_info.indexOf("IRIX") > 0)));
        var _ns6 = ((_ns == true) && (_info.indexOf("Mozilla/5") >= 0));
//--></script>
    </comment>

<script language="JavaScript" type="text/javascript"><!--
    if (_ie == true) document.writeln('<object classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" WIDTH = "100%" HEIGHT = "100%" NAME = "ShowFullImage"  codebase="http://java.sun.com/update/1.6.0/jinstall-6u21-windows-i586.cab#Version=6,0,0,6"><xmp>');
    else if (_ns == true && _ns6 == false) document.writeln('<embed ' +
	    'type="application/x-java-applet;version=1.6" \
            CODE = "com.sinolife.im.imview.applets.imaging.ShowFullImage.class" \
            JAVA_CODEBASE = "${ctx}applet" \
            cache_archive = "IMApplet.jar,jai_core.jar,jai_codec.jar,antlr.jar,sf-im-client.jar,jsontools-core.jar,com.springsource.org.codehaus.jackson.mapper.jar,com.springsource.org.codehaus.jackson.jar" \
            cache_version="1.1.13,1.0.0,1.0.0,1.0.0,1.0.0,1.0.0,1.0.0,1.0.0" \
            NAME = "ShowFullImage" \
            WIDTH = "100%" \
            HEIGHT = "100%" \
            session ="<%=request.getHeader("Cookie")%>" \
            verbose ="true" \
            subdir ="" \
            screen ="<%=request.getParameter("screenwithimage")%>" \
            username ="<%=request.getRemoteUser()%>" ' +
            "imageInfo =\"<%=imageInfo%>\" " +
            'imageTypeName ="<%=imageTypeName%>" ' +
	    'scriptable=false ' +
	    'pluginspage="http://java.sun.com/products/plugin/index.html#download"><xmp>');
	function setIndex(imageName){
		var fullImage=document.getElementsByName('ShowFullImage');
		if(fullImage){
			try{
				if(fullImage.length) {
					fullImage[0].setIndex(imageName);
				} else {
					fullImage.setIndex(imageName);
				}
			}catch(e){
			}
		}
	}
//--></script>
<applet  CODE = "com.sinolife.im.imview.applets.imaging.ShowFullImage.class" JAVA_CODEBASE = "${ctx}applet" ARCHIVE = "IMApplet.jar,jai_core.jar,jai_codec.jar,antlr.jar,sf-im-client.jar,jsontools-core.jar,com.springsource.org.codehaus.jackson.mapper.jar,com.springsource.org.codehaus.jackson.jar" WIDTH = "100%" HEIGHT = "100%" NAME = "ShowFullImage"></xmp>
    <PARAM NAME = CODE VALUE = "com.sinolife.im.imview.applets.imaging.ShowFullImage.class" >
    <PARAM NAME = CODEBASE VALUE = "${ctx}applet" >
    <PARAM NAME = "cache_archive" VALUE = "IMApplet.jar,jai_core.jar,jai_codec.jar,antlr.jar,sf-im-client.jar,jsontools-core.jar,com.springsource.org.codehaus.jackson.mapper.jar,com.springsource.org.codehaus.jackson.jar" >
    <PARAM NAME=  "cache_version" VALUE="1.1.13,1.0.0,1.0.0,1.0.0,1.0.0,1.0.0,1.0.0,1.0.0">
    <PARAM NAME = NAME VALUE = "ShowFullImage" >
    <param name="type" value="application/x-java-applet;version=1.6">
    <param name="scriptable" value="false">
    <PARAM NAME = "session" VALUE="<%=request.getHeader("Cookie")%>" />
    <PARAM NAME = "verbose" VALUE="true" />
    <PARAM NAME = "subdir" VALUE="" />
    <PARAM NAME = "screen" VALUE="<%=request.getParameter("screenwithimage")%>" />
    <PARAM NAME = "username" VALUE="<%=request.getRemoteUser()%>" />
    <PARAM NAME = "imageInfo" VALUE="<%=imageInfo%>" />
    <PARAM NAME = "imageTypeName" VALUE="<%=imageTypeName%>" />
</applet>
</embed>
</object>

<%--
<APPLET CODE = "com.sinolife.im.imview.applets.imaging.ShowFullImage.class" JAVA_CODEBASE = "${ctx}applet" ARCHIVE = "IMApplet.jar,jai_core.jar,jai_codec.jar,antlr.jar,sf-im-client.jar,jsontools-core.jar,com.springsource.org.codehaus.jackson.mapper.jar,com.springsource.org.codehaus.jackson.jar" WIDTH = "100%" HEIGHT = "100%" NAME = "ShowFullImage">
<PARAM NAME = "session" VALUE="<%=request.getHeader("Cookie")%>" />
<PARAM NAME = "verbose" VALUE="true" />
<PARAM NAME = "subdir" VALUE="" />
<PARAM NAME = "screen" VALUE="<%=request.getParameter("screenwithimage")%>" />
<PARAM NAME = "username" VALUE="<%=request.getRemoteUser()%>" />
<PARAM NAME = "imageInfo" VALUE="<%=imageInfo%>" />
<PARAM NAME = "imageTypeName" VALUE="<%=imageTypeName%>" />
</APPLET>

<applet id="ShowFullImage" name="ShowFullImage" code="com.sinolife.im.imview.applets.imaging.ShowFullImage.class"
	codebase="${ctx}applet" height="100%" width="100%" archive="IMApplet.jar,jai_core.jar,jai_codec.jar,antlr.jar,sf-im-client.jar,jsontools-core.jar,com.springsource.org.codehaus.jackson.mapper.jar,com.springsource.org.codehaus.jackson.jar">
	<param name = "session" value="<%=request.getHeader("Cookie")%>" />
    <param name = "verbose" value="true" />
    <param name = "subdir" value="" />
    <param name = "screen" value="<%=request.getParameter("screenwithimage")%>" />
    <param name = "username" value="<%=request.getRemoteUser()%>" />
	<param name = "imageInfo" value="<%=imageInfo%>" />
	<param name = "imageTypeName" value="<%=imageTypeName%>" />
</applet>

--%>
<!--"END_CONVERTED_APPLET"-->


</c:if>
