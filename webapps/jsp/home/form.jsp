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
			<span class="title">Contract service(Issuing token)</span>
		</div>	
	</header>
	<section class="main">
		<div class="input_form2">
			<form name="form" onsubmit="return false;" enctype="multipart/form-data">
				<div class="ui huge labels">
					<div class="ui label three_hundred">token 이름</div>
					<div class="ui input three_hundred">
						<input type="text" name="tokenName">
					</div>
					<p class="br_height"></p>
					<div class="ui label three_hundred">token 발행량</div>
					<div class="ui input three_hundred">
						<input type="text" name="tokenCap">
					</div>
					<p class="br_height"></p>
					<div class="ui label three_hundred">token swap비율</div>
					<div class="ui input three_hundred">
						<input type="text" name="tokenSwapRatio">
					</div>
					<p class="br_height"></p>
					<div class="ui label three_hundred">relay server 정보<br/>(ip or domain)</div>
					<div class="ui input three_hundred">
						<input type="text" name="serverInfo">
					</div>
 					
 					<div style="display:none">
					<p class="br_height"></p>
					<div class="ui label three_hundred">contract binary</div>
					<div class="ui input three_hundred">
						<input type="file" name="binary">
					</div>
					</div>

					<input type="hidden" name="recordAddress" value="1">
					<input type="hidden" name="anchoringAddress" value="1">
					<input type="hidden" name=binaryHash value="binaryHash">`
					<input type="hidden" name=binaryPath value="binaryPath">
 					
 					
					<div class="ui buttons">
						<button class="ui positive button" data-click="Y" data-name="submit">Submit</button>
						<div class="or"></div>
						<button class="ui button" data-click="Y" data-name="back">Cancel</button>
					</div>
				</div>
			</form>
		</div>
	</section>
</div>
<%@ include file="../include/incFooterScript.jsp" %>
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
		'ui'			: 'https://code.jquery.com/ui/1.12.1/jquery-ui.min',
		'form'			: 'form' + _JS_MINIFY,
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
	'ui',
	'form',
], function($, semantic, common, ui, form)
{
	$(document).ready(function()
	{
		$("[name=startDt]").datepicker(
		{
			minDate		: 0,
			dateFormat	: 'yy-mm-dd',
		});
		$("[name=endDt]").datepicker(
		{
			minDate		: 0,
			dateFormat	: 'yy-mm-dd',
		});

		common.init(form, "Hdac.form");
	});
});
</script>
</body>
</html>