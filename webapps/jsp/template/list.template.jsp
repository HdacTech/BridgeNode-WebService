<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><script id="list" type="text/x-Handlebars-template">
<div class="main_table">
	<table class="ui celled striped table">
		<colgroup>
			<col/>
			<col/>
			<col/>
			<col/>
			<col/>
			<col/>
			<col/>
		</colgroup>
		<thead>
   			<tr class="center aligned">
				<th>번호</th>
				<th>토큰명</th>
				<th>토큰 발행량</th>
				<th>토큰 스왑비율</th>
				<th>Contract address</th>
				<th>Domain</th>
				<th>Anchoring address</th>
			</tr>
		</thead>
		<tbody>
		{{#each this}}
			<tr>
				<td>{{no}}</td>
				<td><a href="javascript:;" data-click="Y" data-name="info" data-no="{{no}}">{{tokenName}}</a></td>
				<td>{{tokenCap}}</td>
				<td>{{tokenSwapRatio}}</td>
				<td>{{contractAddress}}</td>
				<td>{{host}}</td>
				<td>{{anchoringAddress}}</td>
			</tr>
		{{/each}}
		</tbody>
	</table>
</div>
</script>
<script id="info" type="text/x-Handlebars-template">
<div class="main_table">
	<table class="ui celled striped table">
		<colgroup>
			<col width="30%"/>
			<col width="70%"/>
		</colgroup>
		<tbody>
			<tr>
				<td class="right aligned"><b>번호</b></td>
				<td>{{no}}</td>
			</tr>
			<tr>
				<td class="right aligned"><b>토큰명</b></td>
				<td>{{tokenName}}</td>
			</tr>
			<tr>
				<td class="right aligned"><b>토큰 발행량</b></td>
				<td>{{tokenCap}}</td>
			</tr>
			<tr>
				<td class="right aligned"><b>토큰 스왑비율</b></td>
				<td>{{tokenSwapRatio}}</td>
			</tr>
			<tr>
				<td class="right aligned"><b>Contract address</b></td>
				<td>{{contractAddress}}</td>
			</tr>
			<tr>
				<td class="right aligned"><b>Domain</b></td>
				<td>{{host}}</td>
			</tr>
			<tr>
				<td class="right aligned"><b>Anchoring address</b></td>
				<td>{{anchoringAddress}}</td>
			</tr>
		</tbody>
	</table>
</div>
</script>
<script id="anchor_list" type="text/x-Handlebars-template">
<div class="main_table">
	<table class="ui celled striped table" style="width:70%; margin-left:20%;">
		<colgroup>
			<col width="10%"/>
			<col width="90%"/>
		</colgroup>
		<tbody>
			<tr>
				<td><b>128954</b></td>
				<td>
					<b>NO : </b>{{no}}<br>
					<b>Contract Address : </b>{{contractAddress}}<br>
					<b>Token Name : </b>{{tokenName}}
				</td>
			</tr>
		</tbody>
	</table>
</div>
</script>