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
			<span class="title">Token Swap</span>
		</div>	
	</header>
	<section class="main">
		<div class="main_box">
			<div class="menubar">
				<div class="ui huge labels" style="margin-top:150px">
					<input class="ui label three_hundred" value="Menu" type="button" disabled><br><br><br>
					<input class="ui label three_hundred" type="button" style="cursor:pointer" value="Contract Info" data-click="Y" data-name="list"><br>
					<input class="ui label three_hundred" type="button" style="cursor:pointer" value="Issue Token" data-click="Y" data-name="regist"><br>
					<input class="ui label three_hundred" type="button" style="cursor:pointer" value="Verify Transaction" data-click="Y" data-name="verify"><br>
					<!-- <input class="ui label three_hundred" type="button" style="cursor:pointer" value="ETC" data-click="Y" data-name="etc"><br> -->
				</div>
			</div>
			<div class="info">
				<div class="ui huge labels" style="margin-top:150px;">
					<p style="font-size:20px">HDAC Token Swap Main Page. You have to deposit coin on that addresses.</p>
					<p style="font-size:16px;">Contract Address : Address to register contract, recommend 1 HDAC.</p>
					<p style="font-size:16px;">Anchoring Address : Address to register side chain hash, recommend 30 HDAC for using 1 year.</p>
					<p style="font-size:16px;">Contract Lib Record Address : Address to register history of lib changing, recommend 1 HDAC.</p>
				</div>
				<div class="ui huge labels" style="margin-top:60px;">
					<div class="ui label three_hundred">Contract Address</div>
					<div class="ui input three_hundred for_main">
						<input id="result_contract" type="text" style="font-weight:bold;" readonly>
					</div>
					<div class="ui input one_hundred">
						<input id="result_contract_bal" type="text" style="font-weight:bold;" readonly>
					</div>
					<br>
					<div class="ui label three_hundred">Anchoring Address</div>
					<div class="ui input three_hundred for_main">
						<input id="result_anchoring" type="text" style="font-weight:bold;" readonly>
					</div>
					<div class="ui input one_hundred">
						<input id="result_anchoring_bal" type="text" style="font-weight:bold;" readonly>
					</div>
					<br>
					<div class="ui label three_hundred">Contract Lib Record Address</div>
					<div class="ui input three_hundred for_main">
						<input id="result_record" type="text" style="font-weight:bold;" readonly>
					</div>
					<div class="ui input one_hundred">
						<input id="result_record_bal" type="text" style="font-weight:bold;" readonly>
					</div>
				</div>
			</div>
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
		'main'			: 'main' + _JS_MINIFY,
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
	'main',
], function($, semantic, common, ui, verify)
{
	$(document).ready(function()
	{
		common.init(verify, "Hdac.main");
	});
});
</script>
</body>
</html>