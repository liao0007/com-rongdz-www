var PageAccount = function () {

    return {

        handleSignUp : function () {
            var b = $('#signUpButton');
            var f = $('#signUpForm');
            var e = $('#error');
            var timer;

            var resetCountDown = function(){
                clearTimeout(timer);
                b.text("获取验证码");
                b.removeAttr("disabled");
            };

            f.validate(Wechat.forms.validateBootstrapSettings);
            b.on('click', function () {
                if (! f.valid()) return;

                b.attr("disabled", "disabled")
                e.addClass('hidden');

                var openId = $.trim($('#openId').val());
                var mobile = $.trim($('#mobile').val());
                var name = mobile;
                if($('#name')) {
                    name = $.trim($('#name').val());
                }
                var avatar = null;
                if($('#avatar')) {
                    avatar = $.trim($('#avatar').val());
                }

                App.ajax(RestRoutes.controllers.rest.AuthController.socialSignUp(openId), {
                    identifier: mobile,
                    name: name,
                    avatar: avatar
                }).done(function (data, textStatus, jqXHR) {
                    var count = 60;
                    var countDown = function(){
                        b.text("重新发送(" + count-- + ")");
                        if(count >= 0) {
                            timer = setTimeout(countDown, 1000)
                        } else {
                            resetCountDown();
                        }
                    };
                    countDown();
                }).fail(function (jqXHR, textStatus, errorThrown) {
                    if (jqXHR.responseText) {
                        var res = JSON.parse(jqXHR.responseText);
                        if (res.status)
                            e.text(res.status).removeClass('hidden');
                    } else {
                        e.removeClass('hidden');
                    }
                    resetCountDown();
                });
            });

            f.find('input').keypress(function (e) {
                if (e.which == 13) {
                    b.trigger('click', e);
                    return false;
                }
            });
        },

        handleActivate : function () {
            var b = $('#registerButton');
            var f = $('#activateForm');
            var e = $('#error');

            f.validate(Wechat.forms.validateBootstrapSettings);
            b.on('click', function () {

                var returnUrl = $.trim($('#returnUrl').val());
                var mobile = $.trim($('#mobile').val());
                var password = $.trim($('#password').val());
                var checkCode = $.trim($('#checkCode').val());

                if (b.is('.wait') || ! $('#signUpForm').valid() || ! f.valid() ) return;
                b.addClass('wait');
                e.addClass('hidden');

                App.ajax(RestRoutes.controllers.rest.AuthController.execute("credentials", mobile, checkCode), {
                    identifier: mobile,
                    password: password
                }).done(function (data, textStatus, jqXHR) {
                    var checkedReturnUrl = /\/(account\/(login|register|logout))/i.test(returnUrl) ? App.site.url : returnUrl;
                    location.replace(WechatRoutes.controllers.wechat.AccountController.signIn(checkedReturnUrl).url)
                }).fail(function (jqXHR, textStatus, errorThrown) {
                    if (jqXHR.responseText) {
                        var res = JSON.parse(jqXHR.responseText);
                        if (res.status)
                            e.text(res.status).removeClass('hidden');
                    } else {
                        e.removeClass('hidden');
                    }
                    b.removeClass('wait');
                });

            });

            f.find('input').keypress(function (e) {
                if (e.which == 13) {
                    b.trigger('click', e);
                    return false;
                }
            });
        },

        handleBind : function () {
            var b = $('#bindButton');
            var f = $('#bindForm');
            var e = $('#error');


            f.validate(Wechat.forms.validateBootstrapSettings);
            b.on('click', function () {

                var openId = $.trim($('#openId').val());
                var returnUrl = $.trim($('#returnUrl').val());
                var mobile = $.trim($('#mobile').val());
                var password = $.trim($('#password').val());

                if (b.is('.wait') || ! f.valid() ) return;
                b.addClass('wait');
                e.addClass('hidden');

                App.ajax(RestRoutes.controllers.rest.AuthController.socialBind(openId), {
                    identifier: mobile,
                    password: password,
                    rememberMe: "true"
                }).done(function (data, textStatus, jqXHR) {
                    location.replace(/\/(account\/(login|register|logout))/i.test(returnUrl) ? App.site.url : returnUrl);
                }).fail(function (jqXHR, textStatus, errorThrown) {
                    if (jqXHR.responseText) {
                        var res = JSON.parse(jqXHR.responseText);
                        if (res.status)
                            e.text(res.status).removeClass('hidden');
                    } else {
                        e.removeClass('hidden');
                    }
                    b.removeClass('wait');
                });
            });

            f.find('input').keypress(function (e) {
                if (e.which == 13) {
                    b.trigger('click', e);
                    return false;
                }
            });
        },

        init: function() {

        }
    };

}();