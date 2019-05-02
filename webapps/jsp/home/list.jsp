<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@include file="../include/incHeadForm.jsp"
%><%
%>
</head>
<body>
<div id="modal"></div>
<div id="wrap">
	<header class="header">
		<div class="header_inner">
			<div class="header_logo">
				<h1>
					<span class="logo focus" data-click="Y" data-name="home"></span>
				</h1>
			</div>
		</div>
		<div class="header_title">
			<span class="title">Contract info</span>
		</div>	
	</header>
	<section class="main" id="main"></section>
	<div class="header_title">
		<!-- <span><input type="button" class="ui button" data-click="Y" data-name="regist" value="regist"/></span> -->
	</div>
</div>
<%@ include file="../include/incFooterScript.jsp" %>
<%@ include file="../template/list.template.jsp" %>
<script type="text/javascript">
requirejs.config(
{
	baseUrl : _JS_PATH_,
	paths :
	{
		'jquery'		: JsUrl.jquery,
		'semantic'		: JsUrl.semantic,
		'handlebars'	: JsUrl.handlebars,
		'common'		: JsUrl.common,
		'list'			: 'list' + _JS_MINIFY,
    },
    shim :
    {
        semantic :
        {
            deps: ['jquery'],
        },
    },
	urlArgs : _JS_PARAM_,
});
requirejs(
[
	'jquery',
	'semantic',
	'common',
	'list',
], function($, semantic, common, list)
{
	$(document).ready(function()
	{
		common.init(list, "Hdac.list");
	});
});
</script>
</body>
</html>