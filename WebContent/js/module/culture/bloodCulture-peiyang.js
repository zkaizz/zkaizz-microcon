/**
 * 读取表单字段
 */
function readFormFields(taskId) {
	var $form = $('.form-bloodCulture');

	// 设置表单提交id
	$form.attr('action', ctx + '/bloodCulture/task/complete/' + taskId);

 	// 读取启动时的表单
	$.getJSON(ctx + '/bloodCulture/get-form/task/' + taskId, function(datas) {
		var trs = "";
		$.each(datas.taskFormData.formProperties, function() {
			var className = this.required === true ? "required" : "";
			this.value = this.value ? this.value : "";
			trs += "<tr>" + createFieldHtml(this, datas, className)
			if (this.required === true) {
				trs += "<span style='color:red'>*</span>";
			}
			trs += "</td></tr>";
		});
		if(datas.taskName=='G染色涂片'){	
			trs +="<tr><td>操作</td><td>";
			trs +="1 样本涂片操作<br/>&nbsp;1.1 涂片操作步骤1<br/>&nbsp;1.2 涂片操作步骤2<br/>&nbsp;1.3 涂片操作步骤3<br/>";
			trs +="2 样本G染色方法<br/>&nbsp;2.1 样本染色操作步骤1<br/>&nbsp;2.2 样本染色操作步骤2";
			trs +="</td></tr>";	
		}else if(datas.taskName=='瑞氏染色涂片'){
			trs +="<tr><td>操作</td><td>";
			trs +="1 样本涂片操作<br/>&nbsp;1.1 涂片操作步骤1<br/>&nbsp;1.2 涂片操作步骤2<br/>&nbsp;1.3 涂片操作步骤3<br/>";
			trs +="2 样本瑞氏染色方法<br/>&nbsp;2.1 样本染色操作步骤1<br/>&nbsp;2.2 样本染色操作步骤2";
			trs +="</td></tr>";	
		}else if(datas.taskName=='抗酸染色涂片'){
			trs +="<tr><td>操作</td><td>";
			trs +="1 样本涂片操作<br/>&nbsp;1.1 涂片操作步骤1<br/>&nbsp;1.2 涂片操作步骤2<br/>&nbsp;1.3 涂片操作步骤3<br/>";
			trs +="2 样本抗酸染色方法<br/>&nbsp;2.1 样本染色操作步骤1<br/>&nbsp;2.2 样本染色操作步骤2";
			trs +="</td></tr>";	
		}else if(datas.taskName=='平板转种'){
			trs +="<tr id='pingban_opt'><td>血平板操作方法</td><td>";
			trs +="1 平板转种操作<br/>2 平板转种操作步骤1<br/>3 平板转种操作步骤2<br/>4 放入孵箱培养60分钟<br/>";
			trs +="</td></tr>";	
		}
			
		// 添加table内容
		$('.table-bloodCulture').html(trs).find('tr').hover(function() {
			$(this).addClass('ui-state-hover');
		}, function() {
			$(this).removeClass('ui-state-hover');
		});

		// 初始化日期组件
		$form.find('.date').datepicker();

		// 表单验证
		$form.validate($.extend({}, $.common.plugin.validator));
	});
}

function bindSelect(){
	$('#pingbanType').live("change",function(){
		var optStr = "";
		if($('#pingbanType').val()=='1'){
			optStr +="<td>血平板操作方法</td><td>";
			optStr +="1 平板转种操作<br/>2 平板转种操作步骤1<br/>3 平板转种操作步骤2<br/>4 放入孵箱培养60分钟3<br/>";
			optStr +="</td>";	
		}else if($('#pingbanType').val()=='2'){
			optStr +="<td>巧克力平板操作方法</td><td>";
			optStr +="1 平板转种操作<br/>2 平板转种操作步骤1<br/>3 平板转种操作步骤2<br/>4 放入孵箱培养40分钟<br/>";
			optStr +="</td>";	
		}else if($('#pingbanType').val()=='3'){
			optStr +="<td>麦康平板操作方法</td><td>";
			optStr +="1 平板转种操作<br/>2 平板转种操作步骤1<br/>3 平板转种操作步骤2<br/>4 放入孵箱培养90分钟<br/>";
			optStr +="</td>";	
		}
		$('#pingban_opt').html(optStr);
	});
}

/**
 * form对应的string/date/long/enum/boolean类型表单组件生成器
 * fp_的意思是form paremeter
 */
var formFieldCreator = {
	'string': function(prop, datas, className) {
		var result = "<td width='120'>" + prop.name + "：</td>";
		if (prop.writable === true) {
			result += "<td><input type='text' id='" + prop.id + "' name='fp_" + prop.id + "' class='" + className + "' value='" + prop.value + "' />";
		} else {
			result += "<td>" + prop.value;
		}
		return result;
	},
	'date': function(prop, datas, className) {
		var result = "<td width='120'>" + prop.name + "：</td>";
		if (prop.writable === true) {
			result += "<td><input type='text' id='" + prop.id + "' name='fp_" + prop.id + "' class='date " + className + "' value='" + prop.value + "'/>";
		} else {
			result += "<td>" + prop.value;
		}
		return result;
	},
	'enum': function(prop, datas, className) {
		var result = "<td width='120'>" + prop.name + "：</td>";
		if (prop.writable === true) {
			result += "<td><select id='" + prop.id + "' name='fp_" + prop.id + "' class='" + className + "'>";
			$.each(datas[prop.id], function(k, v) {
				result += "<option value='" + k + "'>" + v + "</option>";
			});
			result += "</select>";
		} else {
			result += "<td>" + prop.value;
		}
		return result;
	},
	'users': function(prop, datas, className) {
		var result = "<td width='120'>" + prop.name + "：</td>";
		if (prop.writable === true) {
			result += "<td><input type='text' id='" + prop.id + "' name='fp_" + prop.id + "' class='" + className + "' value='" + prop.value + "' />";
		} else {
			result += "<td>" + prop.value;
		}
		return result;
	}
};

/**
 * 生成一个field的html代码
 */
function createFieldHtml(prop, className) {
	return formFieldCreator[prop.type.name](prop, className);
}