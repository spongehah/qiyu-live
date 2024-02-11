function initRedPacket(num,redPacketConfigCode) {
    var dom = createDom(num);
    var wrapper = document.getElementById('wrapper');
    wrapper.appendChild(dom);
    console.log(num);
    console.log(redPacketConfigCode);
    bindEvent(redPacketConfigCode);
}

var totalMoney = 0;   //保存所有抢到的红包总金额
var delayTime = 0;
var lastImg = null;   //最后一张掉落的图片

//创建红包结构, num代表红包个数
function createDom(num) {
    var frag = document.createDocumentFragment();  //创建文档碎片
    for (var i = 0; i < num; i++) {
        var img = new Image();
        img.src = '../img/petal.jpg';
        img.style.width = 140 +'px';
        img.style.left = ranNum(0, window.innerWidth) + 'px';      //让红包散列分布
        var delay = ranNum(0, 100) / 10;
        img.style.animationDelay = delay + 's';                   //设置红包出现时间
        if (delayTime < delay) {
            delayTime = delay;
            lastImg = img;
        }
        //data-money
        img.dataset.money = ranNum(0, 1000) / 100;     //设置每个红包的钱数
        frag.appendChild(img);
    }
    return frag;
}

//绑定点击事件
function bindEvent(redPacketConfigCode) {
    var wrapper = document.getElementById('wrapper'),
        imgList = document.getElementsByTagName('img'),
        modol = document.getElementById('modol'),
        text = document.getElementById('text'),
        btn = document.getElementById('btn');   
    
    //点击领取红包
    addEvent(wrapper, 'mousedown', function (e) {
        var event = e || window.event,
            target = event.target || event.srcElement,
            money = event.target.dataset.money;
        //请求接口,领取红包
        let data = new FormData();
        data.append("redPacketConfigCode",redPacketConfigCode);
        httpPost(getRedPacketUrl, data)
            .then(resp => {
                if (isSuccess(resp)) {
                    if(resp.data.price!=null) {
                        money = resp.data.price;
                        if (money) {
                            text.innerText = resp.data.msg;
                            for (var i = 0, len = imgList.length; i < len; i++) {
                                imgList[i].style.animationPlayState = 'paused';
                            }
                            modol.style.display = 'block';
                            totalMoney += Number(money);
                        }
                    } else {
                        text.innerText = resp.data.msg;
                        for (var i = 0, len = imgList.length; i < len; i++) {
                            imgList[i].style.animationPlayState = 'paused';
                        }
                        modol.style.display = 'block';
                    }
                }
        });
    });    
    //点击继续抢红包按钮事件
    addEvent(btn, 'click', function () {
        modol.style.display = 'none';
        for (var i = 0, len = imgList.length; i < len; i++) {
            imgList[i].style.animationPlayState = 'running';
        }
    });
    //当所有红包都下完了之后
    addEvent(lastImg, 'webkitAnimationEnd', function () {
        modol.style.display = 'block';
        text.innerText = '恭喜总共抢到了' + totalMoney.toFixed(2) + '元';
        btn.style.display = 'none';
        setTimeout(() => modol.style.display='none', 3000);
    });

  
   
}

//min 到 max 之间的随机数
function ranNum(min, max) {
    return Math.ceil(Math.random() * (max - min) + min);
}

//兼容的 添加事件函数
function addEvent(elem, type, handle) {
    if (elem.addEventListener) {
        elem.addEventListener(type, handle, false);
    } else if (elem.attachEvent) {
        elem.attachEvent('on' + type, function () {
            handle.call(elem);
        })
    } else {
        elem['on' + type] = handle;
    }
}



