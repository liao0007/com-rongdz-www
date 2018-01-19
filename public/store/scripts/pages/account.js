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

            f.validate(App.forms.validateBootstrapSettings);
            b.on('click', function () {
                if (! f.valid()) return;

                b.attr("disabled", "disabled")
                e.addClass('hidden');

                var mobile = $.trim($('#mobile').val());

                App.ajax(RestRoutes.controllers.rest.AuthController.signUp(), {
                    identifier: mobile,
                    name: mobile
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
                            e.text(res.status).removeClass('hidden-xs-up');
                    } else {
                        e.removeClass('hidden-xs-up');
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

            f.validate(App.forms.validateBootstrapSettings);
            b.on('click', function () {

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
                    var returnUrl = App.parseQuery().returnUrl || location.href;
                    location.replace(/\/(account\/(sign\-in|sign\-out|sign\-up))/i.test(returnUrl) ? App.site.url : returnUrl);
                }).fail(function (jqXHR, textStatus, errorThrown) {
                    if (jqXHR.responseText) {
                        var res = JSON.parse(jqXHR.responseText);
                        if (res.status)
                            e.text(res.status).removeClass('hidden-xs-up');
                    } else {
                        e.removeClass('hidden-xs-up');
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

        handleSignIn : function () {
            var b = $('#loginButton');
            var f = $('#loginForm');
            var e = $('#error');

            f.validate(App.forms.validateBootstrapSettings);
            b.on('click', function () {

                var mobile = $.trim($('#mobile').val());
                var password = $.trim($('#password').val());

                if (b.is('.wait') || ! f.valid() ) return;
                b.addClass('wait');
                e.addClass('hidden');

                App.ajax(RestRoutes.controllers.rest.AuthController.signIn(), {
                    identifier: mobile,
                    password: password,
                    rememberMe: "true"
                }).done(function (data, textStatus, jqXHR) {
                    var returnUrl = App.parseQuery().returnUrl || location.href;
                    location.replace(/\/(account\/(sign\-in|sign\-out|sign\-up))/i.test(returnUrl) ? App.site.url : returnUrl);
                }).fail(function (jqXHR, textStatus, errorThrown) {
                    if (jqXHR.responseText) {
                        var res = JSON.parse(jqXHR.responseText);
                        if (res.status)
                            e.text(res.status).removeClass('hidden-xs-up');
                    } else {
                        e.removeClass('hidden-xs-up');
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

        handleChangePassword : function () {
            var b = $('#changePasswordButton');
            var f = $('#changePasswordForm');
            var e = $('#error');

            f.validate(App.forms.validateBootstrapSettings);
            b.on('click', function () {

                var mobile = $.trim($('#mobile').val());
                var password = $.trim($('#password').val());

                if (b.is('.wait') || ! f.valid() ) return;
                b.addClass('wait');
                e.addClass('hidden');

                App.ajax(RestRoutes.controllers.rest.AuthController.changePassword(), {
                    identifier: mobile,
                    password: password,
                }).done(function (data, textStatus, jqXHR) {
                    var returnUrl = App.parseQuery().returnUrl || location.href;
                    location.replace(/\/(account\/(sign\-in|sign\-out|sign\-up))/i.test(returnUrl) ? App.site.url : returnUrl);
                }).fail(function (jqXHR, textStatus, errorThrown) {
                    if (jqXHR.responseText) {
                        var res = JSON.parse(jqXHR.responseText);
                        if (res.status)
                            e.text(res.status).removeClass('hidden-xs-up');
                    } else {
                        e.removeClass('hidden-xs-up');
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


        handleRequestPasswordResetToken : function () {
            var b = $('#requestButton');
            var f = $('#requestCheckCodeForm');
            var e = $('#error');
            var timer;

            var resetCountDown = function(){
                clearTimeout(timer);
                b.text("获取验证码");
                b.removeAttr("disabled");
            };

            f.validate(App.forms.validateBootstrapSettings);
            b.on('click', function () {
                if (! f.valid()) return;

                b.attr("disabled", "disabled")
                e.addClass('hidden');

                var mobile = $.trim($('#mobile').val());

                App.ajax(RestRoutes.controllers.rest.AuthController.requestSmsResetPasswordToken(), {
                    identifier: mobile
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
                            e.text(res.status).removeClass('hidden-xs-up');
                    } else {
                        e.removeClass('hidden-xs-up');
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

        handleResetPassword : function () {
            var b = $('#resetPasswordButton');
            var f = $('#resetPasswordForm');
            var e = $('#error');

            f.validate(App.forms.validateBootstrapSettings);
            b.on('click', function () {

                var mobile = $.trim($('#mobile').val());
                var password = $.trim($('#password').val());
                var checkCode = $.trim($('#checkCode').val());

                if (b.is('.wait') || ! f.valid() ) return;
                b.addClass('wait');
                e.addClass('hidden');

                App.ajax(RestRoutes.controllers.rest.AuthController.execute("credentials", mobile, checkCode), {
                    identifier: mobile,
                    password: password
                }).done(function (data, textStatus, jqXHR) {
                    var returnUrl = App.parseQuery().returnUrl || location.href;
                    location.replace(/\/(account\/(sign\-in|sign\-out|sign\-up))/i.test(returnUrl) ? App.site.url : returnUrl);
                }).fail(function (jqXHR, textStatus, errorThrown) {
                    if (jqXHR.responseText) {
                        var res = JSON.parse(jqXHR.responseText);
                        if (res.status)
                            e.text(res.status).removeClass('hidden-xs-up');
                    } else {
                        e.removeClass('hidden-xs-up');
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