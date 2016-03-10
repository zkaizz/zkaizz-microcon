$(function() {
	$('#portlet-container').portlet({
		sortable: true,
		columns: [{
			width: 650,
			portlets: [{
				title: '待办任务',
				content: {
					style: {
						maxHeight: 300
					},
					type: 'ajax',
					dataType: 'json',
					url: ctx + '/workflow/task/todo/list',
					formatter: function(o, pio, data) {
                        if (data.length == 0) {
                            return "无待办任务！";
                        }
						var ct = "<ol>";
						$.each(data, function() {
							ct += "<li>" + this.pdname + "->PID:" + this.pid + "-><span class='ui-state-highlight ui-corner-all'>" + this.name + "</span>";
							ct += "</li>";
						});
						return ct + "</ol>";
					},
					afterShow: function() {
						$('.trace').click(graphTrace);
					}
				}
			}, {
				title: '培养类型',
				content: {
					type: 'text',
					text: function() {
						return $('.forms').html();
					}
				}
			}]
		}, {
			width: 450,
			portlets: [{
				title: '演示内容',
				content: {
					type: 'text',
					text: function() {
						return $('.demos').html();
					}
				}
			}   ]
		}]
	});
});