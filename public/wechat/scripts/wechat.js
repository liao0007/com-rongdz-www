var Wechat = function () {
    return {

        forms: {
            validateBootstrapSettings: {
                ignore: ":hidden, :disabled",
                highlight: function(n) {
                    $(n).closest(".weui-cell").addClass("weui-cell_warn").find('.weui-cell__ft .weui-icon-warn').removeClass("hidden");
                },
                unhighlight: function(n) {
                    $(n).closest(".weui-cell").filter(":not(:has(.weui-cell_warn))").removeClass("weui-cell_warn").find('.weui-cell__ft .weui-icon-warn').addClass("hidden")
                },
                errorClass: "text-danger",
                errorPlacement: function(n, t) {
                    n.insertAfter(t)
                }
            }
        },

        init: function () {
        }
    };
}();