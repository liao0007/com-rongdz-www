var PageBag = function () {

    var updateSubtotalContainer = function() {
        $('#subtotal-container').load( RestRoutes.controllers.rest.CartController.subtotal().url + "?" + $.now(), null, function () {
            $(this).find('.popover-trigger-text').popover({ trigger: Modernizr.touch ? 'click' : 'hover', delay: 100, placement: 'top' });
        });
    };

    var handleBag = function () {
        function updateQty(saleNumber, quantity) {
            App.ajax(RestRoutes.controllers.rest.CartController.update(saleNumber, quantity),{}).done(function (data) {
                Store.updateCartCountLabel(data);
                updateSubtotalContainer();
            });
        }

        function updateError() {
            $('.error-wrapper').prop('hidden', !$('.qty-error').filter(':visible').length);

            if (!$('.item-row').length)
                location.href = location.href; // if bag is now empty, reload page to show empty message
        }

        function qtyChange() {
            var $e = $(this);
            var $tr = $e.closest('.row');
            var qty = parseInt($e.val(), 10);
            if (!isNaN(qty) && qty <= $tr.data('availableQty')) {
                $tr.find('.qty-error').prop('hidden', true);
                $tr.find('.item-price').text((qty * $tr.data('unitPrice')).toFixed(1)+ "元");
                updateQty($tr.data('saleNumber'), qty);
            } else {
                $tr.find('.qty-error').prop('hidden', false);
            }

            updateError();
        }

        var dlgopts = { top: 'center', fixed: false, close: '.close-dialog', mask: '#000' };

        $('#btnSendToOMS').overlay($.extend(dlgopts, { target: '#dlgComment' }));

        $('.item-qty').filter(':enabled').TouchSpin({ max: 99, booster: false }).on('change', $.debounce(200, qtyChange));

        $('.remove-link').on('click', function () {
            var $tr = $(this).closest('.item-row');
            updateQty($tr.data('saleNumber'), 0);
            $tr.css('height', $tr.outerHeight()).empty().slideUp(400, function () { $tr.remove(); updateError(); });
        });

        //send to oms
        $('#btnSend').on('click', function () {
            var $this = $(this);
            if ($this.is('.wait'))
                return;

            $this.addClass('wait');

            App.ajax('~/secure/clientapi/bag-send-to-stylist', $('#order-comments').val()).done(function (d) {

                setTimeout(function () { location.href = '/secure/sendtostylistconfirmation?id=' + d.orderHeaderID }, 200);
                //alert(JSON.stringify(d));

            }).fail(function (x) {
                var res = JSON.parse(x.responseText);
                if (res.Message)
                    App.messageBox('Error', 'Continue', res.Message);
                else
                    App.messageBox('Error', 'Continue', x.responseText);
            }).always(function () {
                $this.removeClass('wait');
            });
        });
    };

    var handleGa = function () {
        qc('_fp.event.Bag');

        window.google_tag_params = {
            ecomm_prodid: ["R59"],
            ecomm_quantity: [2],
            ecomm_pagetype: 'cart'
        };
    };

    return {
        init: function () {
            handleBag();
            updateSubtotalContainer();
        }
    };

}();