var App = function () {

    var _ajaxGetSettings = {
        type: "GET",
        cache: !1
    };

    var _ajaxPostSettings = {
        type: "POST",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        converters: {
            "text json": function(n) {
                var t = JSON.parse(n || "{}");
                return t && t.hasOwnProperty("d") ? t.d : t
            }
        }
    };

    var _dialog = null;

    var _dialogInit = function() {
        _dialog || (_dialog = $('<div class="modal" role="dialog" aria-hidden="true"><div class="modal-dialog modal-sm" role="document"><div class="modal-content"><div class="modal-header"><button type="button" class="close" data-dismiss="modal"><i class="icon-cancel"><\/i><\/button><h4 class="modal-title"><\/h4><\/div><div class="modal-body"><\/div><div class="modal-footer"><button type="button" class="cta-button modal-continue" data-dismiss="modal">Continue<\/button><\/div><\/div><\/div><\/div>').appendTo("body"))
    };

    var extend = function () {
        $.fn.outerXML = function() {
            var n = "";
            return this.each(function() {
                $.isXMLDoc(this) && (n += window.ActiveXObject && this.xml ? this.xml : (new XMLSerializer).serializeToString(this))
            }), n
        };
        $.fn.appendXML = function(n) {
            return this.each(function() {
                var t = this,
                    i;
                $.isXMLDoc(t) && (t.parentNode || (t = t.documentElement), i = $.parseXML("<x>" + n + "<\/x>").documentElement, $.each(i.childNodes, function() {
                    var n = this;
                    $.isFunction(t.ownerDocument.importNode) && (n = t.ownerDocument.importNode(n, !0));
                    t.appendChild(n)
                }))
            })
        };
        $.event.special.enterkeypress = {
            bindType: "keypress",
            delegateType: "keypress",
            handle: function(n) {
                if (n.which == 13)
                    return n.handleObj.handler.apply(this, arguments), !1
            }
        };
        $.fn.toggleAttr = function(n, t) {
            return this.each(function() {
                var i = $(this);
                i.is("[" + n + "]") ? i.removeAttr(n) : i.attr(n, t === undefined ? "" : t)
            })
        };
        $.fn.html5Placeholder = function() {
            if (Modernizr.placeholder)
                return this;
            this.on("focus", function() {
                var n = $(this);
                n.val() == n.attr("placeholder") && n.val("").removeClass("placeholding")
            });
            this.on("blur", function() {
                var n = $(this);
                n.val() == "" && n.val(n.attr("placeholder")).addClass("placeholding")
            });
            return this.not(":focus").blur()
        };
        $.fn.flyout = function(selector, show, hide) {
            var r = Modernizr.touch,
                e = show || function(n) {
                        $(this).find(n).stop(!0).slideDown("fast")
                    },
                o = hide || show || function(n) {
                        $(this).find(n).stop(!0).slideUp("slow")
                    },
                s = this,
                u = function() {
                    e.call(this, selector);
                    r && $(document).on("touchstart.flyout", c)
                },
                f = function() {
                    o.call(this, selector);
                    r && $(document).off("touchstart.flyout")
                },
                h = function() {
                    var t = $(this).find(selector);
                    if (t.length && t.is(":hidden") || t.data("flyoutHidden"))
                        return u.call(this), !1
                },
                c = function(t) {
                    $(t.target).closest(selector).length == 0 && f.call(s)
                };
            if (r)
                this.on("touchstart", h);
            return this.hoverIntent({
                over: u,
                out: f,
                timeout: 300
            }), this
        };
    };

    return {
        site: {},
        constant: {},
        context: {},
        settings: {},

        ajax: function(route, data, conf) {
            if(route.type == 'GET') {
                return $.ajax($.extend(_ajaxGetSettings, {
                    url: route.url,
                    type: route.type,
                }, conf))

            } else if (route.type == 'POST') {
                return $.ajax($.extend(_ajaxPostSettings, {
                    url: route.url,
                    type: route.type,
                    data: JSON.stringify(data || {})
                }, conf))

            } else {
                return $.ajax($.extend({
                    url: route.url,
                    type: route.type,
                    data: data
                }, conf))
            }
        },
        messageBox: function(title, buttonTitle, message, continueCallback, closeCallback) {
            _dialogInit();
            var u = _dialog;
            if (message && u.find(".modal-title").text(title), buttonTitle && u.find(".modal-continue").text(buttonTitle), message ? u.find(".modal-body").html(message) : u.find(".modal-body").text(title)) {
                if($.isFunction(continueCallback)) u.find(".modal-continue").on("click", continueCallback.bind(this));
                if($.isFunction(closeCallback)) u.find(".close").on("click", closeCallback.bind(this));
            }

            _dialog.modal()
        },

        popupWindow: function(url, name, refreshParent) {
            var w = window.open(url, name, 'menubar=no, toolbar=no, location=no, directories=no, status=no, scrollbars=no, resizable=no, dependent, width=700, height=750');
            if(refreshParent) {
                w.onunload = function() {
                    w.opener.location.reload();
                }
            }
            return false;
        },

        forms: {
            validateBootstrapSettings: {
                ignore: ":hidden, :disabled",
                highlight: function(n) {
                    $(n).addClass("err").closest(".form-group").addClass("has-error")
                },
                unhighlight: function(n) {
                    $(n).removeClass("err").closest(".form-group").filter(":not(:has(.err))").removeClass("has-error")
                },
                errorElement: "span",
                errorClass: "help-block",
                errorPlacement: function(errorNode, input) {
                    input.parent(".input-group").length ? errorNode.insertAfter(input.parent()) : errorNode.insertAfter(input)
                }
            }
        },
        parseQuery: function() {
            var n = {},
                t = location.search.substr(1).split("&");
            return $.each(t, function(t, i) {
                var r = i.split("=");
                r[0] && (n[r[0]] = decodeURIComponent(r[1]))
            }), n
        },
        setQuery: function(n) {
            var t = "";
            for (var i in n)
                t += "&" + i + "=" + encodeURIComponent(n[i]);
            Modernizr.history && history.replaceState(null, null, location.href.replace(location.search, "") + "?" + t.substr(1))
        },
        uniqueSort: function(n) {
            if ($.isArray(n)) {
                n.sort();
                for (var t = 1; t < n.length; t++)
                    n[t] === n[t - 1] && n.splice(t--, 1)
            }
        },

        handleAddressForm : function (containerForm) {
            var f = containerForm;
            f.validate(App.forms.validateBootstrapSettings);

            f.find("select.shipToProvince").on("change", function(event){
                App.ajax(RestRoutes.controllers.rest.AddressController.city($(this).val(), true), {
                }).done(function (data, textStatus, jqXHR) {
                    f.find("select.shipToCity").html("<option>--请选择--</option>"+data);
                    f.find("select.shipToDistrict").html("<option>--请选择--</option>");
                    f.find("select.shipToCity").trigger("option.loaded");
                });
            });

            f.find("select.shipToCity").on("change", function(event){
                App.ajax(RestRoutes.controllers.rest.AddressController.district($(this).val(), true), {
                }).done(function (data, textStatus, jqXHR) {
                    f.find("select.shipToDistrict").html("<option>--请选择--</option>"+data);
                    f.find("select.shipToDistrict").trigger("option.loaded");
                });
            });

            App.ajax(RestRoutes.controllers.rest.AddressController.province(true), {
            }).done(function (data, textStatus, jqXHR) {
                f.find("select.shipToProvince").html("<option>--请选择--</option>"+data);
                f.find("select.shipToProvince").trigger("option.loaded");
            });

            //initial values
            var districtId = f.find("select.shipToDistrict").attr('value');
            if (districtId) {
                f.find('select.shipToProvince').on('option.loaded', function(){
                    f.find('select.shipToProvince').val(districtId.substr(0,2)+"0000").trigger('change');
                    f.find('select.shipToProvince').off('option.loaded');
                });
                f.find('select.shipToCity').on('option.loaded', function(){
                    f.find('select.shipToCity').val(districtId.substr(0,4)+"00").trigger('change');
                    f.find('select.shipToCity').off('option.loaded');
                });
                f.find('select.shipToDistrict').on('option.loaded', function(){
                    f.find('select.shipToDistrict').val(districtId).trigger('change');
                    f.find('select.shipToDistrict').off('option.loaded');
                });
            }
        },

        init: function () {
            extend();

            var u = function() {
                    $(this).stop(!0).show().animate({
                        width: 200
                    }).data("flyoutHidden", !1)
                },
                f = function() {
                    $(this).stop(!0).animate({
                        width: 0
                    }, null, null, function() {
                        $(this).hide()
                    }).data("flyoutHidden", !0)
                },
                t = function() {
                    var n = $.trim($(this).parent().find(".search-box").val()).replace(/[^a-zA-Z0-9_\-\s]/g, "");
                    return n && (location.href = App.resolveUrl("~/search/keyword?q=" + encodeURIComponent(n))), !1
                },
                e = function(n) {
                    n.addClass("loading").empty().css("height", 160).css("background", "").show();
                    App.ajax(RestRoutes.controllers.rest.CartController.miniView(), {}).done(function(data) {
                        var i = n.html(data).css("height", "auto").outerHeight();
                        n.css("height", 160).removeClass("loading").animate({
                            height: i
                        }, "fast")
                    })
                },
                i = function(n) {
                    var t = n.data;
                    return t ? t.loadCompleted ? !0 : (t.loadStarted || (t.loadStarted = !0, $(t.container).load(App.resolveUrl(t.url), function() {
                                t.loadCompleted = !0
                            })), setTimeout(function() {
                                $(n.target).trigger("click")
                            }, 50), !1) : !1
                },
                n;
            $.validator.setDefaults(App.forms.validateBootstrapSettings);
            $("#cart-link").flyout("#mini-cart", function(n) {
                var t = $(this);
                t.data("cartCount") > 0 && e(t.find(n))
            }, function(n) {
                $(this).find(n).slideUp("slow")
            });

            $(".nav-global > li:has(.flyout)").flyout(".flyout", function(n) {
                $(this).find(n).stop(!0).slideDown("fast")
            }, function(n) {
                $(this).find(n).stop(!0).slideUp("slow")
            });

            $("#search-desktop").flyout(".search-flyout", function(n) {
                $(this).find(n).each(u).find("input").focus()
            }, function(n) {
                $(this).find(n).not(":has(:focus)").each(f)
            });
            $(".search-mobile").flyout(".search-flyout");

            $("#nav-user-trigger").on("click", function() {
                $(this).closest("li").find(".flyout").toggle()
            });
            $(".search-box").on("enterkeypress", t);
            $(".search-button").on("click", t);

            $('.radio-list').on('click', '.radio-list-item', function () {
                var $this = $(this);
                var input = $this.closest('.radio-list').find('.selected').removeClass('selected').end().find('input').val($this.data('value')).get(0);
                $this.addClass('selected');
                var val = $this.closest('form').data('validator');
                if (val && input) val.element(input);
            });

            $('.popover-trigger-csc').popover({ html: true, trigger: Modernizr.touch ? 'click' : 'hover', delay: 100, placement: 'top', content: function () { return $('.popover-content-csc').children().clone(); } });
            $('.popover-trigger-text').popover({ trigger: Modernizr.touch ? 'click' : 'hover', delay: 100, placement: 'top' });
            $('.popover-trigger-html').popover({ html: true, trigger: Modernizr.touch ? 'click' : 'hover', delay: 100, placement: 'top', content: function () { return $(this).next().children().clone(); } });

            if (n = App.parseQuery().program, $("[data-img-src]").lazyload({
                    data_attribute: "img-src",
                    threshold: 300,
                    failure_limit: 500,
                    progressive: !0,
                    load: function() {
                        $(this).css("background-color", "transparent")
                    }
                }), $("[data-original-title]").tooltip({
                    delay: {
                        show: 300,
                        hide: 100
                    }
                }), $("[data-scrambled]").each(function() {
                    var n = $(this),
                        t = n.data("scrambled"),
                        r = t == "m" ? "mailto:" : t == "t" ? "tel:" : "",
                        i = n.text().split("").reverse().join("");
                    n.text(i);
                    t && n.attr("href", r + i)
                }), $("video[data-vid-src]").each(function() {
                    var t = $(this),
                        n;
                    if (t.is(":visible") && $.isFunction(this.load)) {
                        n = this;
                        n.src = t.data("vidSrc");
                        setTimeout(function() {
                            n.loop = !1
                        }, 12e4);
                        t.on("click", function() {
                            n.paused ? n.play() : n.pause()
                        })
                    }
                }), $("[data-toggle=collapse-next]").length) {
                $(window).on("resize", function() {
                    $("[data-toggle=collapse-next] + .collapse").each(function() {
                        $(this).css("height", "")
                    })
                });
                $(document).on("click.collapse-next.data-api", "[data-toggle=collapse-next]", function(n) {
                    var i = $(this),
                        t;
                    i.children("i").is(":hidden") || (n.preventDefault(), t = i.next(), t.collapse(t.data("bs.collapse") ? "toggle" : $(this).data()), $(this).attr("aria-expanded", t.attr("aria-expanded")))
                })
            }
        }
    };
}();