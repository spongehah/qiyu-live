new Vue({
    el: '#app',
    data: {
        form: {
            review: ""
        },
        chatList: [],
        giftList: [],
        canvas: {},
        player: {},
        parser: {},
        websock: null,
        roomId: -1,
        anchorId: -1,
        isLogin: false,
        wsServer: '',
        initInfo: {},
        imServerConfig: {},
        showGiftRank: false,
        rankList: [],
        accountInfo: {},
        showBankInfo: false,
        lastPayBtnId: -1,
        payProducts: [],
        nickname: '',
        currentBalance: 0,
        qrCode: 'true',
        dlProgress: 10,
        closeLivingRoomDialog: false,
        livingRoomHasCloseDialog: false,
        timer: null,
        pkUserId: 0,
        pkObjId: 0,
        lastChooseAnchorTab: '',
        lastChooseAnchorId: 0,
        anchorImg: '',
        pkObjImg: '',
        pkObjId: 0,
        pkEnd: false
    },

    mounted() {
        this.roomId = getQueryStr("roomId");
        this.anchorConfig();
        this.initSvga();
        this.initGiftConfig();
        this.listPayProduct();
    },

    beforeDestroy() {
        this.timer = null;
    },

    methods: {

        initGiftConfig: function () {
            let that = this;
            httpPost(listGiftConfigUrl, {})
                .then(resp => {
                    if (isSuccess(resp)) {
                        that.giftList = resp.data;
                        console.log(resp.data);
                    }
                });
        },

        initSvga: function () {
            canvas = document.getElementById('svga-wrap');
            player = new window.SVGA.Player(canvas);
            parser = new window.SVGA.Parser(canvas);

        },

        listPayProduct: function () {
            let data = new FormData();
            data.append("type", 0);
            let that = this;
            httpPost(payProductsUrl, data)
                .then(resp => {
                    if (isSuccess(resp)) {
                        that.payProducts = resp.data.payProductItemVOList;
                        that.currentBalance = resp.data.currentBalance;
                    }
                });
        },

        sendGift: function (giftId) {
            if (this.pkEnd) {
                this.$message.success('pk已经结束');
                return;
            }
            let data = new FormData();
            let anchorId = this.lastChooseAnchorId;
            let userId = this.initInfo.userId;
            if (this.pkObjId == 0 || this.pkObjId == null) {
                this.$message.error('需要等pk方上线后才可进行送礼互动');
                return;
            }
            if (this.lastChooseAnchorId < 1000) {
                this.$message.error('请选择指定嘉宾送礼');
                return;
            }
            if (anchorId == userId) {
                this.$message.error('不能给自己送礼物');
                return;
            }
            data.append("giftId", giftId);
            data.append("type", 1);
            data.append("roomId", getQueryStr("roomId"));
            data.append("receiverId", anchorId);
            data.append("senderUserId", userId);
            let that = this;
            httpPost(sendGiftUrl, data)
                .then(resp => {
                    if (!isSuccess(resp)) {
                        that.$message.error('送礼异常');
                    }
                });
        },

        //渲染礼物特效svga
        playGiftSvga: function (url) {
            player.clearsAfterStop = true;
            player.stopAnimation();
            console.log(url);
            parser.load(url, function (videoItem) {
                player.loops = 1; // 设置循环播放次数是1
                player.setVideoItem(videoItem);
                player.startAnimation();
                player.onFinished(function () {
                    console.log("动画停止了！！！");
                });
            });
        },
        //选择对应的支付产品，并且充值
        payProduct: function (productId) {
            let data = new FormData();
            data.append("productId", productId);
            data.append("payChannel", 1);
            data.append("paySource", 1);
            let that = this;
            httpPost(payProductUrl, data)
                .then(resp => {
                    if (!isSuccess(resp)) {
                        that.$message.error('充值异常');
                    } else {
                        that.$message.success('充值成功');
                        that.listPayProduct();
                    }
                });
        },

        connectLiving: function () {
            console.log('连线');
            let that = this;
            let data = new FormData();
            data.append("roomId", getQueryStr("roomId"));
            httpPost(onlinePkUrl, data)
                .then(resp => {
                    if (isSuccess(resp)) {
                        that.winnerId = null;
                        that.pkEnd = false;
                        that.$message.success('连线成功');
                    } else {
                        that.$message.success(resp.msg);
                    }
                });
        },

        showBankInfoTab: function () {
            this.showBankInfo = true;
        },

        hiddenBankInfoTabNow: function () {
            this.showBankInfo = false;
        },

        choosePkUserAnchor: function () {
            this.chooseAnchor('anchorVideo', this.pkUserId);
        },

        choosePkObjectAnchor: function () {
            this.chooseAnchor('subAnchorVideo', this.pkObjId);
        },

        //给指定主播送礼
        chooseAnchor: function (id, anchorId) {
            console.log('选中主播id' + anchorId);
            this.lastChooseAnchorId = anchorId;
            let lastChooseAnchor = document.getElementById(this.lastChooseAnchorTab);
            if (lastChooseAnchor != undefined) {
                lastChooseAnchor.style.border = "rgba(255,165,0,0) 3px solid";
            }
            let currentChooseAnchor = document.getElementById(id);
            currentChooseAnchor.style.border = "rgba(255,165,0,1) 3px solid";
            this.lastChooseAnchorTab = id;
        },

        //直播间初始化配置加载时候调用
        anchorConfig: function () {
            let data = new FormData();
            data.append("roomId", getQueryStr("roomId"));
            var that = this;
            httpPost(anchorConfigUrl, data)
                .then(resp => {
                    if (isSuccess(resp)) {
                        if (resp.data.roomId > 0) {
                            that.initInfo = resp.data;
                            that.anchorImg = that.initInfo.avatar;
                            that.pkUserId = that.initInfo.anchorId;
                            that.pkObjId = that.initInfo.pkObjId;
                            that.connectImServer();
                        } else {
                            this.$message.error('直播间已不存在');
                        }
                    } else {
                        this.$message.error(resp.msg);
                    }
                });
        },


        connectImServer: function () {
            let that = this;
            httpPost(getImConfigUrl, {})
                .then(resp => {
                    if (isSuccess(resp)) {
                        that.imServerConfig = resp.data;
                        let url = "ws://" + that.imServerConfig.wsImServerAddress + "/" + that.imServerConfig.token + "/" + that.initInfo.userId + "/1001/" + this.roomId;
                        console.log(url);
                        that.websock = new WebSocket(url);
                        that.websock.onmessage = that.websocketOnMessage;
                        that.websock.onopen = that.websocketOnOpen;
                        that.websock.onerror = that.websocketOnError;
                        that.websock.onclose = that.websocketClose;
                        console.log('初始化ws服务器');
                    }
                });

        },


        websocketOnOpen: function () {
            console.log('初始化连接建立');
        },

        websocketOnError: function () {
            console.error('出现异常');
        },

        websocketOnMessage: function (e) { //数据接收
            let wsData = JSON.parse(e.data);
            console.log(wsData);
            if (wsData.code == 1001) {
                this.startHeartBeatJob();
            } else if (wsData.code == 1003) {
                let respData = JSON.parse(utf8ByteToUnicodeStr(wsData.body));
                //属于直播间内的聊天消息
                if (respData.bizCode == 5555) {
                    let respMsg = JSON.parse(respData.data);
                    let sendMsg = { "content": respMsg.content, "senderName": respMsg.senderName, "senderImg": respMsg.senderAvatar };
                    let msgWrapper = { "msgType": 1, "msg": sendMsg };
                    console.log(sendMsg);
                    this.chatList.push(msgWrapper);
                    //注意让滑轮滚到底
                    this.$nextTick(() => {
                        var div = document.getElementById('talk-content-box')
                        div.scrollTop = div.scrollHeight
                    })
                } else if (respData.bizCode == 5557) {
                    //送礼失败
                    let respMsg = JSON.parse(respData.data);
                    this.$message.error(respMsg.msg);
                } else if (respData.bizCode == 5558) {
                    //pk送礼成功
                    let respMsg = JSON.parse(respData.data);
                    //说明已经有pk结果了
                    if (respMsg.winnerId != null) {
                        this.pkEnd = true;
                        this.$message.success("恭喜主播" + respMsg.winnerId+"pk获胜");
                        return;
                    } 
                    console.log(respMsg);
                    this.playGiftSvga(respMsg.url);
                    this.changeBarWidth(respMsg.pkNum);
                } else if (respData.bizCode == 5559) {
                    this.$message.success("pk用户已上线");
                    let respMsg = JSON.parse(respData.data);
                    this.pkObjId = respMsg.pkObjId;
                    this.pkObjImg = respMsg.pkObjAvatar;
                }
                this.sendAckCode(respData);
            }
        },

        changeBarWidth: function (pkNum) {
            console.log(pkNum);
            let pkBar = document.getElementById("pkBar");
            pkBar.style.width = (pkNum + '%');
        },

        sendAckCode: function (respData) {
            let jsonStr = { "userId": this.initInfo.userId, "appId": 10001, "msgId": respData.msgId };
            let bodyStr = JSON.stringify(jsonStr);
            let ackMsgStr = { "magic": 19231, "code": 1005, "len": bodyStr.length, "body": bodyStr };
            this.websocketSend(JSON.stringify(ackMsgStr));
        },

        websocketSend: function (data) {//数据发送
            this.websock.send(data);
        },

        websocketClose: function (e) {  //关闭
            console.log('断开连接', e);
        },

        startHeartBeatJob: function () {
            console.log('首次登录成功');
            let that = this;
            //发送一个心跳包给到服务端
            let jsonStr = { "userId": this.initInfo.userId, "appId": 10001 };
            let bodyStr = JSON.stringify(jsonStr);
            let heartBeatJsonStr = { "magic": 19231, "code": 1004, "len": bodyStr.length, "body": bodyStr };
            setInterval(function () {
                that.websocketSend(JSON.stringify(heartBeatJsonStr));
            }, 30000);
        },

        closeLivingRoom: function () {
            let data = new FormData();
            data.append("roomId", getQueryStr("roomId"));
            httpPost(closeLiving, data)
                .then(resp => {
                    if (isSuccess(resp)) {
                        window.location.href = './living_room_list.html';
                    }
                });
        },


        sendReview: function () {
            if (this.form.review == '') {
                this.$message({
                    message: "评论不能为空",
                    type: 'warning'
                });
                return;
            }
            let sendMsg = { "content": this.form.review, "senderName": this.initInfo.watcherNickName, "senderImg": this.initInfo.watcherAvatar };
            let msgWrapper = { "msgType": 1, "msg": sendMsg };
            this.chatList.push(msgWrapper);
            //发送评论消息给到im服务器
            let msgBody = { "roomId": this.roomId, "type": 1, "content": this.form.review, "senderName": this.initInfo.watcherNickName, "senderAvatar": this.initInfo.watcherAvatar };
            console.log(this.initInfo);
            let jsonStr = { "userId": this.initInfo.userId, "appId": 10001, "bizCode": 5555, "data": JSON.stringify(msgBody) };
            let bodyStr = JSON.stringify(jsonStr);
            console.log('发送消息');
            let reviewMsg = { "magic": 19231, "code": 1003, "len": bodyStr.length, "body": bodyStr };
            console.log(JSON.stringify(reviewMsg));
            this.websocketSend(JSON.stringify(reviewMsg));
            this.form.review = '';
            this.$nextTick(() => {
                var div = document.getElementById('talk-content-box')
                div.scrollTop = div.scrollHeight
            })

        }


    }

});