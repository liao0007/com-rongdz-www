var PageTape = function () {
    return {
        init: function () {
            $('.tape-adjust').TouchSpin({ decimals: 2, booster: false })
        }
    };
}();