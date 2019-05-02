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
			url				: '/submitReport.do',
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
			var createMember = function()
			{
				if (_reqAjax)
					return;

				if (checkField() == false)
					return;

				setDate("start", ":00:00");
				setDate("end", ":59:59");

				var form = $("[name=form]")[0];
				var formData = new FormData(form);

				$.ajax(
				{
					url			: _DEFINE_.url,
					processData	: false,
					contentType	: false,
					data		: formData,
					type		: 'POST',
					enctype		: 'multipart/form-data',
					success		: function()
					{
						alert('저장성공');
						var url = '/list.do';
						COMMON._COMMON_.MovePage(url, true);
						
	                },
					complete	: function()
					{
						_reqAjax = false;
					}
	            });
/*
				var opt =
				{
					type		: 'post',
					dataType	: 'json',
					url			: _DEFINE_.url,
					cache		: false,
					data		: formData,
				};
				$.ajax(opt).done(function(data, state, xhr)
				{
					console.log(data);
					_reqAjax = false;
				});
*/
				_reqAjax = true;
			};

			var data = $trgt.data(), name = data.name;
			switch (name)
			{
				case 'home' :
					var url = '/list.do';
					COMMON._COMMON_.MovePage(url, true);
					break;

				case 'submit' :
					createMember();
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