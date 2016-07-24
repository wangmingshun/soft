<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp" %>

<c:if test="${imageSize ==0}">
<div class="image_message m_a">

暂无影像信息
 
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
    if (_ie == true) document.writeln('<object classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" WIDTH = "100%" HEIGHT = "100%" NAME = "ShowMaxImage"  codebase="http://java.sun.com/update/1.6.0/jinstall-6u21-windows-i586.cab#Version=6,0,0,6"><xmp>');
    else if (_ns == true && _ns6 == false) document.writeln('<embed ' +
	    'type="application/x-java-applet;version=1.6" \
            CODE = "com.sinolife.im.imview.applets.imaging.ShowMaxImage.class" \
            JAVA_CODEBASE = "${ctx}applet" \
            ARCHIVE = "IMApplet.jar,jai_core.jar,jai_codec.jar,antlr.jar,sf-im-client.jar,jsontools-core.jar,com.springsource.org.codehaus.jackson.mapper.jar,com.springsource.org.codehaus.jackson.jar" \
            NAME = "ShowMaxImage" \
            WIDTH = "100%" \
            HEIGHT = "100%" \
            session ="<%=request.getHeader("Cookie")%>" / \
            verbose ="true" / \
            subdir ="" / \
            screen ="<%=request.getParameter("screenwithimage")%>" / \
            username ="<%=request.getRemoteUser()%>" / ' +
	    'scriptable=false ' +
	    'pluginspage="http://java.sun.com/products/plugin/index.html#download"><xmp>');
//--></script>
<applet  CODE = "com.sinolife.im.imview.applets.imaging.ShowMaxImage.class" JAVA_CODEBASE = "${ctx}applet" ARCHIVE = "IMApplet.jar,jai_core.jar,jai_codec.jar,antlr.jar,sf-im-client.jar,jsontools-core.jar,com.springsource.org.codehaus.jackson.mapper.jar,com.springsource.org.codehaus.jackson.jar" WIDTH = "100%" HEIGHT = "100%" NAME = "ShowMaxImage"></xmp>
    <PARAM NAME = CODE VALUE = "com.sinolife.im.imview.applets.imaging.ShowMaxImage.class" >
    <PARAM NAME = CODEBASE VALUE = "${ctx}applet" >
    <PARAM NAME = ARCHIVE VALUE = "IMApplet.jar,jai_core.jar,jai_codec.jar,antlr.jar,sf-im-client.jar,jsontools-core.jar,com.springsource.org.codehaus.jackson.mapper.jar,com.springsource.org.codehaus.jackson.jar" >
    <PARAM NAME="cache_version" VALUE="1.0.0,1.0.0,1.0.0,1.0.0,1.0.0,1.0.0,1.0.0,1.0.0">
    <PARAM NAME = NAME VALUE = "ShowMaxImage" >
    <param name="type" value="application/x-java-applet;version=1.6">
    <param name="scriptable" value="false">
    <PARAM NAME = "session" VALUE="<%=request.getHeader("Cookie")%>" />
    <PARAM NAME = "verbose" VALUE="true" />
    <PARAM NAME = "subdir" VALUE="" />
    <PARAM NAME = "screen" VALUE="<%=request.getParameter("screenwithimage")%>" />
    <PARAM NAME = "username" VALUE="<%=request.getRemoteUser()%>" />


</applet>
</embed>
</object>

<%--
<APPLET CODE = "com.sinolife.im.imview.applets.imaging.ShowMaxImage.class" JAVA_CODEBASE = "${ctx}applet" ARCHIVE = "IMApplet.jar,jai_core.jar,jai_codec.jar,antlr.jar,sf-im-client.jar,jsontools-core.jar,com.springsource.org.codehaus.jackson.mapper.jar,com.springsource.org.codehaus.jackson.jar" WIDTH = "100%" HEIGHT = "100%" NAME = "ShowMaxImage">
<PARAM NAME = "session" VALUE="<%=request.getHeader("Cookie")%>" />
<PARAM NAME = "verbose" VALUE="true" />
<PARAM NAME = "subdir" VALUE="" />
<PARAM NAME = "screen" VALUE="<%=request.getParameter("screenwithimage")%>" />
<PARAM NAME = "username" VALUE="<%=request.getRemoteUser()%>" />
</APPLET>

<applet id="ShowMaxImage" name="ShowMaxImage" code="com.sinolife.im.imview.applets.imaging.ShowMaxImage.class"
	codebase="${ctx}applet" height="100%" width="100%" archive="IMApplet.jar,jai_core.jar,jai_codec.jar,antlr.jar,sf-im-client.jar,jsontools-core.jar,com.springsource.org.codehaus.jackson.mapper.jar,com.springsource.org.codehaus.jackson.jar">
	<param name = "session" value="<%=request.getHeader("Cookie")%>" />
    <param name = "verbose" value="true" />
    <param name = "subdir" value="" />
    <param name = "screen" value="<%=request.getParameter("screenwithimage")%>" />
    <param name = "username" value="<%=request.getRemoteUser()%>" />
</applet>

--%>
<!--"END_CONVERTED_APPLET"-->

</c:if>
