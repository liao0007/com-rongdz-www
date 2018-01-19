var Store = function () {

    var handlePullOrderState = function (orderNumber) {
        var longPull = function () {
            App.ajax(RestRoutes.controllers.rest.SaleOrderController.paymentState(orderNumber)).done(function (data, textStatus, jqXHR) {
                if(data.paymentState == App.constant.SaleOrderPaymentState.Paid ) {
                    App.messageBox("感谢您使用荣定制", "订单详情", "订单支付成功!", Store.viewOrderDetail(orderNumber), Store.viewOrderDetail(orderNumber));
                } else {
                    setTimeout(longPull, 1000)
                }
            });
        }
        longPull();
    };

    return {
        updateCartCountLabel : function(n) {
            $("#cart-count-label").html(n > 0 ? "("+n+")" : "");
            $("#cart-mini-label").html(n > 0 ? n : "");
            $("#cart-link").data("cartCount", n)
        },

        showCartAddedMessage: function(saleId, quantity) {
            var container = $("#cart-added").addClass("loading");
            App.ajax(RestRoutes.controllers.rest.CartController.added(saleId, quantity), {}).done(function(data) {
                container.empty().removeClass("loading").removeClass("fixed").css("right", "0px").html(data);
                var u = container.parent()[0],
                    width = window.innerWidth || window.width(),
                    rect = u.getBoundingClientRect ? u.getBoundingClientRect() : {
                            top: 0,
                            right: width
                        };
                (rect.top < -10 || rect.right > width) && container.addClass("fixed").css("right", Math.max(Math.abs(Math.min(rect.right - width, 0)), 15));
                container.fadeIn().data("timer", setTimeout(function() {
                    container.fadeOut()
                }, 2500));
                Modernizr.touch || container.hoverIntent(function() {
                    clearTimeout(container.data("timer"))
                }, function() {
                    container.fadeOut()
                })
            })
        },

        addToCart: function(saleNumber, quantity) {
            var i = this;
            return App.ajax(RestRoutes.controllers.rest.CartController.update(saleNumber, quantity), {
            }).done(function(data, textStatus, jqXHR) {
                i.updateCartCountLabel(data);
                i.showCartAddedMessage(saleNumber, quantity)
            }).fail(function (jqXHR, textStatus, errorThrown) {
                if(jqXHR.status == 401) {
                    location.href = jqXHR.responseText
                }
            })
        },

        initWechatJsApi: function () {
            if(App.context.channel == App.constant.SaleOrderChannel.OnlineWechat) {
                App.ajax(WechatRoutes.controllers.wechat.ApplicationController.jsApiSignature()).done(function (data, textStatus, jqXHR) {
                    wx.config(
                        $.extend(data, {
                            debug: App.settings.env != "prd",
                            jsApiList: ['chooseWXPay']
                        })
                    );
                })
            }
        },

        createOrder: function (data, done, fail) {
            App.ajax(RestRoutes.controllers.rest.SaleOrderController.create(), data).done(done);
        },

        payOrder: function (orderNumber, paymentMethod) {

            if(paymentMethod == App.constant.SaleOrderPaymentMethod.Wepay ) {
                if(App.context.channel == App.constant.SaleOrderChannel.OnlineWechat ) {
                    Store.payByWepay(orderNumber, "JSAPI");
                } else {
                    Store.payByWepay(orderNumber, "NATIVE");
                }
            } else if (paymentMethod == App.constant.SaleOrderPaymentMethod.Alipay ) {
                if(App.context.isMobile) {
                    Store.payByAlipay(orderNumber, "QUICK_WAP_PAY");
                } else {
                    Store.payByAlipay(orderNumber);
                }
            } else if (paymentMethod == App.constant.SaleOrderPaymentMethod.Cash ) {
                Store.payByCash(orderNumber)
            }
        },

        payByWepay: function (orderNumber, tradeType) {
            App.ajax(RestRoutes.controllers.rest.SaleOrderController.wepayInfo(orderNumber, tradeType)).done(function (data, textStatus, jqXHR) {
                //fix stupid timestamp bug
                data.timestamp = data.timeStamp
                delete data.timeStamp

                if(tradeType == 'JSAPI') {
                    wx.chooseWXPay($.extend(data, {
                        success: function (res) {
                            handlePullOrderState(orderNumber);
                        },
                        cancel: function (res) {
                            App.messageBox("支付取消","订单详情","请您在24小时内完成支付，否则订单会被自动取消（库存紧俏商品支付时限以订单详情页为准）", Store.viewOrderDetail(orderNumber), Store.viewOrderDetail(orderNumber));
                        }
                    }));
                } else if (tradeType == 'NATIVE') {
                    var qrcode = $("<div class='qrcode'></div>").qrcode({width: 200,height: 200, text: data.codeUrl, render:"image"});
                    var content = "<div style='text-align: center;'>"+ qrcode.html() +"<div class='mt-1'>请使用微信扫一扫<br/>扫描二维码支付</div></div>";
                    App.messageBox("微信支付", "取消", content, Store.viewOrderDetail(orderNumber), Store.viewOrderDetail(orderNumber));
                    handlePullOrderState(orderNumber);
                }
            });
        },

        payByAlipay: function (orderNumber, tradeType) {
            App.ajax(RestRoutes.controllers.rest.SaleOrderController.alipayInfo(orderNumber, tradeType)).done(function (data, textStatus, jqXHR) {
                if(tradeType == 'QUICK_WAP_PAY') {
                    $(document.body).prepend(data) // use prepend coz we got another form down there <.<
                } else {
                    var qrcode = $("<div class='qrcode'></div>").qrcode({width: 200,height: 200, text: data, render:"image"});
                    var content = "<div style='text-align: center;'>"+ qrcode.html() +"<div style='margin-top: 20px;'>请使用支付宝钱包<br/>扫描二维码支付</div></div>"
                    App.messageBox("支付宝支付", "取消", content, Store.viewOrderDetail(orderNumber), Store.viewOrderDetail(orderNumber));
                    handlePullOrderState(orderNumber);
                }
            });
        },

        payByCash: function (orderNumber) {
            App.ajax(RestRoutes.controllers.rest.SaleOrderController.cashInfo(orderNumber)).done(function (data, textStatus, jqXHR) {
                App.messageBox("现金支付", "订单详情", "您选择了现金支付，荣定制工作人员会主动和您联系", Store.viewOrderDetail(orderNumber), Store.viewOrderDetail(orderNumber));
            });
        },

        viewOrderDetail: function (orderNumber) {
            return function () {
                location.replace(StoreRoutes.controllers.store.AccountController.saleOrder(orderNumber).url)
            }
        },

        init: function () {

            $("[data-zoom-src]").on("click", function() {
                var n = $(this);
                $(".cloudzoom").data('CloudZoom').loadImage(n.data("mainSrc"), n.data("zoomSrc"));
            });

            var option = {
                position: "right",
                zoomWidth: 100,
                zoomHeight: 100,
                adjustX: 30,
                adjustY: 0,
                autoInside: 767
                // disableOnScreenWidth: 767
            };
            option.zoomWidth = $(".js-zoom-target").outerWidth();
            option.zoomHeight = Math.min(option.zoomWidth / .745, 600) - option.adjustY;
            $(".cloudzoom").CloudZoom(option);

            if(!App.context.isMobile) {
                //catalog hover
                $(".catalog-tile-container").filter(":has(.catalog-hover-image)").hoverIntent({
                    selector: ".catalog-image",
                    over: function() {
                        $(this).find(".catalog-hover-image").addClass("in").find("[data-hover-src]").each(function() {
                            var n = $(this);
                            n.data("init") || $("<img />").on("load", function() {
                                n.css("background-image", 'url("' + n.data("hoverSrc") + '")').data("init", !0)
                            }).attr("src", n.data("hoverSrc"))
                        })
                    },
                    out: function() {
                        $(this).find(".catalog-hover-image").removeClass("in")
                    },
                    interval: 30,
                    sensitivity: 10
                });
            }

            //update minicart bag count
            App.ajax(RestRoutes.controllers.rest.CartController.count()).done(function (data) {
                Store.updateCartCountLabel(data);
            });

        }
    };
}();