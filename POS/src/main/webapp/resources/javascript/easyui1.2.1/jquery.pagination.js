/**
 * pagination - jQuery EasyUI
 * 
 * Licensed under the GPL:
 *   http://www.gnu.org/licenses/gpl.txt
 *
 * Copyright 2010 stworthy [ stworthy@gmail.com ] 
 * 
 * Dependencies:
 * 	linkbutton
 * 
 */
(function($){
	function buildToolbar(target){
		var opts = $.data(target, 'pagination').options;
		
		var pager = $(target).addClass('pagination').empty();
		var t = $('<table cellspacing="0" cellpadding="0" border="0"><tr></tr></table>').appendTo(pager);
		var tr = $('tr', t);
		
		
		$('<td><a href="'+flowExecutionUrl+'" id="pagination-first" icon="pagination-first"></a></td>').appendTo(tr);
		$('<td><a href="'+flowExecutionUrl+'" id="pagination-prev" icon="pagination-prev"></a></td>').appendTo(tr);
		
		$('<td><input class="pagination-num" type="text" value="1" size="2"></td>').appendTo(tr);
		
		$('<td><a href="'+flowExecutionUrl+'" id="pagination-next" icon="pagination-next"></a></td>').appendTo(tr);
		$('<td><a href="'+flowExecutionUrl+'" id="pagination-last" icon="pagination-last"></a></td>').appendTo(tr);
		
		if (opts.showRefresh){
			$('<td><div class="pagination-btn-separator"></div></td>').appendTo(tr);
			$('<td><a href="javascript:void(0)" icon="pagination-load"></a></td>').appendTo(tr);		
		}
		
		if (opts.buttons){
			$('<td><div class="pagination-btn-separator"></div></td>').appendTo(tr);
			for(var i=0; i<opts.buttons.length; i++){
				var btn = opts.buttons[i];
				if (btn == '-') {
					$('<td><div class="pagination-btn-separator"></div></td>').appendTo(tr);
				} else {
					var td = $('<td></td>').appendTo(tr);
					$('<a href="javascript:void(0)"></a>')
							.addClass('l-btn')
							.css('float', 'left')
							.text(btn.text || '')
							.attr('icon', btn.iconCls || '')
							.bind('click', eval(btn.handler || function(){}))
							.appendTo(td)
							.linkbutton({plain:true});
				}
			}
		}
		
		$('<div class="pagination-info"></div>').appendTo(pager);
		$('<div style="clear:both;"></div>').appendTo(pager);
		
		
		$('a[icon^=pagination]', pager).linkbutton({plain:true});
		
		
	}
	
	
	
	
	
	$.fn.pagination = function(options) {
		if (typeof options == 'string'){
			switch(options){
				case 'options':
					return $.data(this[0], 'pagination').options;
				case 'loading':
					return this.each(function(){
						setLoadStatus(this, true);
					});
				case 'loaded':
					return this.each(function(){
						setLoadStatus(this, false);
					});
			}
		}
		
		options = options || {};
		return this.each(function(){
			var opts;
			var state = $.data(this, 'pagination');
			if (state) {
				opts = $.extend(state.options, options);
			} else {
				opts = $.extend({}, $.fn.pagination.defaults, options);
				$.data(this, 'pagination', {
					options: opts
				});
			}
			
			buildToolbar(this);
			
			
		});
	};
	
	$.fn.pagination.defaults = {
		total: 1,
		pageSize: 10,
		pageNumber: 1,
		
		loading: false,
		buttons: null,
		showPageList: true,
		showRefresh: true,
		
		
		onBeforeRefresh: function(pageNumber, pageSize){},
		onRefresh: function(pageNumber, pageSize){},
		
		
		beforePageText: 'Page',
		afterPageText: 'of {pages}',
		displayMsg: 'Displaying {from} to {to} of {total} items'
	};
})(jQuery);