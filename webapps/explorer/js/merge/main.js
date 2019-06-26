define(["jquery", "handlebars", "common"], function($, HANDLEBARS, COMMON)
{
	"use strict";

	var _M_ = function()
	{
		var yPos = 0;
		var bInit = true;
		var _reqAjax = false;

		var _DEFINE_ =
		{
			url				: 'getmain',
		};
		var info =
		{
			block_template	: null,
		};
		var result =
		{
			info			: null,
			sync			: null,
			information		: null,
			last			: null,
		};

		this.initData = function(data)
		{
			yPos			= data.yPos;
//			bInit			= data.bInit;
//			info			= $.parseJSON(data.info);
			result			= $.parseJSON(data.result);
		};
		this.getSavedData = function()
		{
			var data = {};

			data.yPos		= $(window).scrollTop();
//			data.bInit		= bInit;
//			data.info		= JSON.stringify(info);
			data.result		= JSON.stringify(result);

			return data;
		};
		this.init = function()
		{
			showPage();
			bInit = false;
		};
		this.resume = function()
		{
			showPage();
		};
		this.handleClick = function(e, $trgt, callbackFunc)
		{
			var checkField = function()
			{
				return true;
			};
			var setDate = function(field, value)
			{
				var dField = field + "Dt";
				var tField = field + "Time";
				var sField = field + "Date";

				var dVal = $("[name=" + dField + "]").val();
				var tVal = $("[name=" + tField + "]").val() + value;

				var date = new Date(dVal + " " + tVal);
				$("[name=" + sField + "]").val(date.toUTCString());
			};
			
			var data = $trgt.data(), name = data.name;
			switch (name)
			{
				case 'list' :
					var url = '/list.do';
					COMMON._COMMON_.MovePage(url, true);
					break;

				case 'regist' :
					var url = '/form.do';
					COMMON._COMMON_.MovePage(url, true);
					break;

				case 'verify' :
					var url = '/verify.do';
					COMMON._COMMON_.MovePage(url, true);
					break;
					
				case 'etc' :
					var url = '/main.do';
					COMMON._COMMON_.MovePage(url, true);
					break;
					
				case 'home' :
					var url = '/main.do';
					COMMON._COMMON_.MovePage(url, true);
					break;

				default :
					if (typeof callbackFunc == "function")
						callbackFunc(e, $trgt);
					break;
			}
		};
		this.getYpos = function()
		{
			return yPos;
		};
		this.getInit = function()
		{
			return bInit;
		};

		var getDataList = function(page_no, bInit, callbackFunc)
		{

		};
		var getMainData = function()
		{
			if (_reqAjax)
				return;

			$("#result_contract").val("");
			$("#result_anchoring").val("");
			$("#result_record").val("");
							
			$.ajax(
			{
				url			: _DEFINE_.url,
				type		: 'POST',
				dataType	: 'json',
				success		: function(data)
				{
					//alert(JSON.stringify(data));
					$("#result_contract").val(data.contractAddress);
					$("#result_anchoring").val(data.anchoringAddress);
					$("#result_record").val(data.recordAddress);
					$("#result_contract_bal").val(data.contractBalance);
					$("#result_anchoring_bal").val(data.anchoringBalance);
					$("#result_record_bal").val(data.recordBalance);
                },
				complete	: function()
				{
					_reqAjax = false;
				}
            });

			_reqAjax = true;
		};
		var showPage = function()
		{
			var registerHelpers = function()
			{
				HANDLEBARS.registerHelper('chk', function(userNo, options)
				{
					if (userNo > -1)
						return options.fn(this);
					return options.inverse(this);
				});
			};

//			registerHelpers();
//			setHeader();
//			setInput();
			
			getMainData();

			$('.ui.dropdown').dropdown();
		};
		var setHeader = function()
		{
			var setMenu = function()
			{
				var template = HANDLEBARS.compile($("#header-menu").html());
				$("#menu").html(template());
			};

			setMenu();
		};
		var setInput = function()
		{
			$("[name=user_id]").focus();
			$("[name=password]").keyup(function(e)
			{
				if (e.keyCode == 13)
				{
					$("[data-name=login]").trigger("click");
				}
			});
		};
		var showPopup = function(header, content, callbackFunc)
		{
			var message =
			{
				header		: header,
				content		: content,
			};

			if (info.template == null)
			{
				info.template = HANDLEBARS.compile($("#modal_popup").html());
			}

			$("#modal").html(info.template(message));
			$("#ui_modal").modal(
			{
				onHide		: function()
				{
					if (typeof callbackFunc == "function")
						callbackFunc();
				}
			}).modal('show');
		};
	};
	return new _M_();
});