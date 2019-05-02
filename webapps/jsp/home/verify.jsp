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
			<span class="title">Verify</span>
		</div>	
	</header>
	<section class="main">
		<div class="input_form2" style="">
			<div class="ui huge labels">
				<div class="ui label three_hundred">Txid</div>
				<div class="ui input three_hundred">
					<input id="check_txid" type="text" name="txid">
				</div>
				<!-- <div class="ui label three_hundred">Contract Lib</div>
				<div class="ui input three_hundred">
					<input id="check_contract" type="file" name="contractlib">
				</div>
				<br> -->
				<p class="br_height"></p>
				<div class="ui buttons">
					<button class="ui positive button" data-click="Y" data-name="check">Check</button>
					<div class="or"></div>
					<button class="ui button" data-click="Y" data-name="back">Cancel</button>
				</div>
			</div>
			<div class="verifyLoading" style="display:none"></div>
			<div class="ui huge labels" style="margin-top:10%">
				<div class="ui label three_hundred">Result</div>
				<div class="ui input three_hundred">
					<input id="result_result" type="text" style="font-weight:bold;" readonly>
				</div>
				<br>
				<div class="ui label three_hundred">Main Data</div>
				<div class="ui input three_hundred">
					<input id="result_main" type="text" readonly>
				</div>
				<br>
				<div class="ui label three_hundred">Side Hash Data</div>
				<div class="ui input three_hundred">
					<input id="result_side" type="text" readonly>
				</div>
				<br>
				<div class="ui label three_hundred">Message</div>
				<div class="ui input three_hundred">
					<input id="result_msg" type="text" readonly>
				</div>
				<br>
				<div class="ui label three_hundred">Side Block Height</div>
				<div class="ui input three_hundred">
					<input id="result_height" type="text" readonly>
				</div>
				<br>
				<div class="ui label three_hundred">Anchor Address</div>
				<div class="ui input three_hundred">
					<input id="result_address" type="text" readonly>
				</div>
				<br>
				<div class="ui label three_hundred">Public Txid</div>
				<div class="ui input three_hundred">
					<input id="result_txid" type="text" readonly>
				</div>
				<br>
				<!-- <div class="ui label three_hundred">Token Qty</div>
				<div class="ui input three_hundred">
					<input id="result_qty" type="text" style="color:red" readonly>
				</div>
				<br>
				<div class="ui label three_hundred">Contract Lib Hash</div>
				<div class="ui input three_hundred">
					<input id="result_libHash" type="text" style="color:red" readonly>
				</div>
				<br> -->
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
		'verify'		: 'verify' + _JS_MINIFY,
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
	'verify',
], function($, semantic, common, ui, verify)
{
	$(document).ready(function()
	{
		common.init(verify, "Hdac.verify");
	});
});
</script>
</body>
</html>