var PageCheckout = function () {
    var f = $('#saleOrderForm');
    var b = $('#btnSubmitOrder');

    return {
        init: function () {
            Store.initWechatJsApi();

            var cart = $('.checkout-cart-items');
            if (cart.outerHeight() < cart[0].scrollHeight)
                cart.css('padding-right', 0);

            //create and pay sale order
            f.validate(App.forms.validateBootstrapSettings);
            b.on('click', function () {
                var orderNumber = $.trim($('#orderNumber').val());
                var deliveryType = $.trim($('#deliveryType').val());
                var channel = App.context.channel;

                if($('#shipToAddressId').val() > 0) {
                    var shipToAddressId = parseInt($.trim($('#shipToAddressId').val()));
                    var paymentMethod = $.trim($('#paymentMethod').val());
                    if (b.is('.wait') || ! f.valid() ) return;
                    b.addClass('wait');
                    Store.createOrder({
                        orderNumber: orderNumber,
                        deliveryType: deliveryType,
                        channel: channel,
                        shipToAddressId: shipToAddressId
                    }, function (data, textStatus, jqXHR) {
                        Store.payOrder(data.orderNumber, $("#paymentMethod").val())
                    })
                } else {
                    App.messageBox("您还没填写收货地址","好的","请添加收货地址后提交订单");
                }
            });


        }
    };

}();