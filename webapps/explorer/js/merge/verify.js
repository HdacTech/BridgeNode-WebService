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
			url				: 'rest/verify',
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
			var verifyTx = function()
			{
				if (_reqAjax)
					return;

				if (checkField() == false)
					return;
				
				var formData = new FormData();
				formData.append("txid", $("#check_txid").val());
				//formData.append("contractLib", $("#check_contract")[0].files[0]);
																
				$("#result_result").val("");
				$("#result_msg").val("");
				$("#result_height").val("");
				$("#result_address").val("");
				$("#result_main").val("");
				$("#result_side").val("");
				$("#result_txid").val("");
				/*$("#result_qty").val("");
				$("#result_libHash").val("");*/
				
				$(".verifyLoading").css("display", "block");
				
				$.ajax(
				{
					url			: _DEFINE_.url,
					data		: formData,
					processData	: false,
					contentType	: false,
					enctype		: 'multipart/form-data',
					type		: 'POST',
					success		: function(data)
					{
						//alert(JSON.stringify(data));
						$("#result_result").val(data.matchingResult);
						$("#result_msg").val(data.message);
						$("#result_height").val(data.sideBlockHeight);
						$("#result_address").val(data.publicAddress);
						$("#result_main").val(data.mainData);
						$("#result_side").val(data.sideHashData);
						$("#result_txid").val(data.matchingPublicTx);
						/*if(data.tokenQty == "{}")
							$("#result_qty").val("No Token");
						else
							$("#result_qty").val(data.tokenQty);
						$("#result_libHash").val(data.libHash);*/
												
						$(".verifyLoading").css("display", "none");
	                },
					complete	: function()
					{
						_reqAjax = false;
					}
	            });

				_reqAjax = true;
			};

			var data = $trgt.data(), name = data.name;
			switch (name)
			{
				case 'home' :
					var url = '/main.do'; 
					COMMON._COMMON_.MovePage(url, true);
					break;

				case 'check' :
					verifyTx();
					break;

				case 'back' :
					history.back();
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