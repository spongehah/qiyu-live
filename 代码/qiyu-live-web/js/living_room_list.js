new Vue({
	el: '#app',
	data: {
		userId: 0,
		showLoginPop: false,
		loginCodeBtn: '验证码',
		lastTime: 60,
		mobile: '',
		code: '',
		hasSendSms: false,
		livingRoomList: {},
		isLogin: false,
		initInfo: {},
		loginBtnMsg: '登录',
		showStartLivingBtn: false,
		listType: 1,
		page: 1,
		pageSize: 15,
		startLivingRoomTab: false,
		loadingNextPage: false,
		hasNextPage: true,
		currentChooseTab: null
	},

	//页面初始化的时候会调用下这里面的方法
	mounted() {
		this.initPage();
		this.listLivingRoom(1);
		this.initLoad();
		console.log('handler');
	},

	methods: {
		load: function() {
			console.log('this is load');
		},
		initPage: function() {
			var that = this;
			httpPost(homePageUrl, {}).then(resp => {
				//登录成功
				if (resp.data.loginStatus == true) {
					that.initInfo = resp.data;
					that.loginBtnMsg = '';
					that.isLogin = true;
				}
			})
		},

		chooseLivingType: function(type, id) {
			this.listType = type;
			this.listLivingRoom(type);
			if (this.currentChooseTab != null) {
				this.currentChooseTab.classList.remove('top-title-active');
			}
			this.currentChooseTab = document.getElementById(id);
			this.currentChooseTab.classList.add('top-title-active');
		},

		listLivingRoom: function(type) {
			var that = this;
			let data = new FormData();
			this.page = 1;
			data.append("page", this.page);
			data.append("pageSize", this.pageSize);
			data.append("type", type);
			httpPost(listLivingRoomUrl, data).then(resp => {
				console.log('直播间列表');
				//登录成功
				if (isSuccess(resp)) {
					that.livingRoomList = resp.data.list;
				}
			})
		},
		showLoginPopNow: function() {
			this.showLoginPop = true;
		},
		hiddenLoginPopNow: function() {
			this.showLoginPop = false;
		},

		mobileLogin: function() {
			if (this.code == '') {
				this.$message.error('请输入验证码');
				return;
			}
			var checkStatus = this.checkPhone();
			if (!checkStatus) {
				return;
			}
			var that = this;
			let data = new FormData();
			data.append("phone", this.mobile);
			data.append("code", this.code);
			//请求登录接口
			httpPost(loginUrl, data).then(resp => {
				//登录成功
				if (resp.code == 200) {
					that.userId = resp.data.userId;
					that.$message.success('登录成功');
					that.hiddenLoginPopNow();
					that.isLogin = true;
					that.userId = resp.data.userId;
					that.initPage();
				} else {
					that.$message.error(resp.msg);
				}
			})
		},

		showStartLivingRoomTab: function() {
			this.startLivingRoomTab = true;
		},
		startLivingRoom: function() {
			this.toLivingRoom();
		},

		jumpToLivingRoomPage(livingType) {
			console.log(this.isLogin);
			if (!this.isLogin) {
				this.$message.error('请先登录');
				return;
			}
			let data = new FormData();
			data.append("type", livingType);
			//请求开播接口
			httpPost(startLiving, data).then(resp => {
				//开播成功
				if (isSuccess(resp)) {
					if (livingType == 1) {
						//去直播间详情页面
						window.location.href = "./living_room.html?roomId=" + resp.data.roomId;
					} else if (livingType == 2) {
						window.location.href = "./living_room_pk.html?roomId=" + resp.data.roomId;
					}

				} else {
					that.$message.error(resp.msg);
				}
			})

		},
		jumpToLivingRoom(roomId, type) {
			if (!this.isLogin) {
				this.$message.error('请先登录');
				return;
			}
			type = this.listType;
			if (type == 1) {
				window.location.href = "./living_room.html?roomId=" + roomId;
			} else if (type == 2) {
				window.location.href = "./living_room_pk.html?roomId=" + roomId;
			}
		},

		sendSmsCode: function() {
			if (this.hasSendSms) {
				return;
			}
			console.log(this.mobile);
			var checkStatus = this.checkPhone();
			if (!checkStatus) {
				return;
			}
			//发送验证码按钮文字调整
			var that = this;
			let data = new FormData();
			data.append("phone", this.mobile);
			//请求短信发送接口
			httpPost(sendSmsUrl, data).then(resp => {
				if (resp.code == 200) {
					that.hasSendSms = true;
					//短信发送成功会有一个弹窗
					that.$message.success('短信发送成功');
					var interval = setInterval(function() {
						that.loginCodeBtn = '发送中(' + that.lastTime + ')';
						if (that.lastTime == 0) {
							that.lastTime = 60;
							that.loginCodeBtn = '验证码';
							that.hasSendSms = false;
							console.log('清理定时器');
							clearInterval(interval);
							return;
						} else {
							that.lastTime = that.lastTime - 1;
						}
					}, 1000);
				} else {
					that.$message.error(resp.msg);
				}
			})
		},

		checkPhone: function() {
			let phoneReg = /(^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$)/;
			if (this.mobile == '' || !phoneReg.test(this.mobile)) {
				this.$message.error('手机号格式有误');
				return false;
			}
			return true;
		},

		initLoad: function() {
			let that = this;
			window.addEventListener('scroll', function() {
				let scrollTop = document.documentElement.scrollTop //滚动条在Y轴滚动过的高度
				let scrollHeight = document.documentElement.scrollHeight //滚动条的高度
				let clientHeight = document.documentElement.clientHeight //浏览器的可视高度
				//可能会有部分误差
				if (scrollTop + clientHeight >= scrollHeight - 100 && that.loadingNextPage ==
					false && that.hasNextPage == true) {
					that.loadingNextPage = true;
					console.log('滚动到底部了');
					//触发第二页的数据加载
					that.page = that.page + 1;
					let data = new FormData();
					data.append("page", that.page);
					data.append("pageSize", that.pageSize);
					data.append("type", that.listType);
					httpPost(listLivingRoomUrl, data).then(resp => {
						//登录成功
						if (isSuccess(resp)) {
							let livingRoomTempList = resp.data.list;
							for (i = 0; i < livingRoomTempList.length; i++) {
								that.livingRoomList.push(livingRoomTempList[i]);
							}
							if (!resp.data.hasNext) {
								that.hasNextPage = false;
							}
							that.loadingNextPage = false;

						}
					})
				}
			});
		}
	}

})
