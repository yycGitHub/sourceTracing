(function($) {

	$.config = {
		url: '', //链接地址
	};

	$.status = false; // 连接状态

	$.init = function(config) {
		this.config = config;
		return this;
	};

	/**
	 * 连接webcocket
	 */
	$.connect = function() {
		var protocol = (window.location.protocol == 'http:') ? 'ws:' : 'ws:';
		this.host = protocol + this.config.url;

		window.WebSocket = window.WebSocket || window.MozWebSocket;
		if(!window.WebSocket) { // 检测浏览器支持  
			this.error('Error: WebSocket is not supported .');
			return;
		}
		try {
			if(this.socket != null || this.socket != undefined) {
				this.socket.close();
				this.socket = null;
			}
			console.log(this.host);
			this.socket = new WebSocket(this.host); // 创建连接并注册响应函数  
		} catch(msg) {
			this.error('Error: WebSocket is not supported .');
		}
		this.socket.onopen = function() {
			$.status = true;
			$.onopen();
		};
		this.socket.onmessage = function(message) {
			$.onmessage(message);
		};
		this.socket.onclose = function() {
			$.onclose();
			$.status = false;
			$.socket = null; // 清理  
		};
		this.socket.onerror = function(errorMsg) {
			$.status = false;
			$.onerror(errorMsg);
		}
		return this;
	}

	/**
	 * 自定义异常函数
	 * @param {Object} errorMsg
	 */
	$.error = function(errorMsg) {
		this.onerror(errorMsg);
	}

	/**
	 * 消息发送
	 */
	$.send = function(message) {
		if(this.socket && this.status) {
			this.socket.send(message);
			return true;
		}
		this.error('please connect to the server first !!!');
		return false;
	}

	$.close = function() {
		if(this.socket != undefined && this.socket != null) {
			this.socket.close();
		} else {
			this.error("this socket is not available");
		}
	}

	/**
	 * 消息回調
	 * @param {Object} message
	 */
	$.onmessage = function(message) {

	}

	/**
	 * 链接回调函数
	 */
	$.onopen = function() {

	}

	/**
	 * 关闭回调
	 */
	$.onclose = function() {

	}

	/**
	 * 异常回调
	 */
	$.onerror = function() {

	}
})(ws = {});