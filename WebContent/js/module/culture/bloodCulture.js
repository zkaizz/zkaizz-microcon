/**
 * 读取表单字段
 */

function readFormFields() {
	var $form = $('.form-bloodCulture');

    // 添加隐藏域
    if ($('#processType').length == 0) {
        $('<input/>', {
            'id': 'processType',
            'name': 'processType',
            'type': 'hidden'
        }).val(processType).appendTo($form);
    }

	// 读取启动时的表单
	$.getJSON(ctx + '/bloodCulture/get-form/start', function(data) {
		var trs = "";
		$.each(data.form.formProperties, function() {
			var className = this.required === true ? "required" : "";
			trs += "<tr>" + createFieldHtml(data, this, className,data.defaultMap[this.id]);
			if(this.required === true) {
				trs += "<span style='color:red'>*</span>";
			}
			trs += "</td></tr>";
		});
		var $form = $('.form-bloodCulture');
		// 设置表单提交id
		$form.attr('action', ctx + '/bloodCulture/start-process/' + data.processDefinitionId);
		// 添加table内容
		$('.table-bloodCulture').html(trs).find('tr').hover(function() {
			$(this).addClass('ui-state-hover');
		}, function() {
			$(this).removeClass('ui-state-hover');
		});

		// 初始化日期组件
		$form.find('.dateISO').datepicker();

		// 表单验证
		$form.validate($.extend({}, $.common.plugin.validator));
	});
}

/**
 * form对应的string/date/long/enum/boolean类型表单组件生成器
 * fp_的意思是form paremeter
 */
var formFieldCreator = {
	string: function(formData, prop, className,val) {
		result = "<td width='120'>" + prop.name + "：</td><td>"+val+"<input type='hidden' id='" + prop.id + "' value='"+val+"' name='fp_" + prop.id + "' class='" + className + "'/>";
		return result;
	},
	date: function(formData, prop, className,val) {
		result = "<td>" + prop.name + "：</td><td>"+val+"<input type='hidden' id='" + prop.id + "' name='fp_" + prop.id + "' value='"+val+"' class='dateISO " + className + "' />";
		return result;
	},
	'enum': function(formData, prop, className,val) {
		console.log(prop);
		var result = "<td width='120'>" + prop.name + "：</td>";
		if(prop.writable === true) {
			result += "<td><select id='" + prop.id + "' name='fp_" + prop.id + "' class='" + className + "'>";
			//result += "<option>" + datas + "</option>";
			
			$.each(formData['enum_' + prop.id], function(k, v) {
				result += "<option value='" + k + "'>" + v + "</option>";
			});
			 
			result += "</select>";
		} else {
			result += "<td>" + prop.value;
		}
		return result;
	},
	'users': function(formData, prop, className,val) {
		var result = "<td width='120'>" + prop.name + "：</td><td><input type='text' id='" + prop.id + "' name='fp_" + prop.id + "' class='" + className + "' />";
		return result;
	}
};

/**
 * 生成一个field的html代码
 */

function createFieldHtml(formData, prop, className,val) {
	return formFieldCreator[prop.type.name](formData, prop, className,val);
}

