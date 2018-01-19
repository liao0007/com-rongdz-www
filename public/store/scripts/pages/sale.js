var PageSale = function () {

    return {
        init: function () {

            $('.btnAddToCart').on('click', function addItem(event) {
                var saleNumber = $('#saleNumber').val();
                Store.addToCart(saleNumber, 1);
                return false;
            });
        }
    };

}();