var Admin = function () {


    return {

        dropZone: function () {
            Dropzone.options.imageDropzone = {
                init: function() {
                }
            }
        },

        userSelect2: function (element) {
            var r = AdminRoutes.controllers.admin.user.UserController.index()
            element.select2({
                placeholder: "请输入关键字",
                minimumInputLength: 1,
                ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
                    url: r.url,
                    type: r.type,
                    dataType: 'json',
                    data: function (term, page) {
                        return {
                            "f.name": term,
                            page_limit: 10
                        };
                    },
                    results: function (data, page) { // parse the results into the format expected by Select2.
                        // since we are using custom formatting functions we do not need to alter remote JSON data
                        return {
                            results: data.records
                        };
                    }
                },
                initSelection: function (element, callback) {
                    // the input tag has a value attribute preloaded that points to a preselected movie's id
                    // this function resolves that id attribute to an object that select2 can render
                    // using its formatResult renderer - that way the movie name is shown preselected
                    var id = $(element).val();
                    if (id !== "") {
                        $.ajax({
                            url: r.url,
                            type: r.type,
                            data: {
                                "f.id": id
                            },
                            dataType: "json"
                        }).done(function (data) {
                            callback(data.records[0]);
                        });
                    }
                },
                formatResult: function (record) {
                    var markup = "<table><tr>";
                    markup += "<td><h5>" + record.name + "</h5>";
                    if (record.mobile !== undefined) {
                        markup += "<div>" + record.mobile + "</div>";
                    }
                    markup += "</td></tr></table>"
                    return markup;
                }, // omitted for brevity, see the source of this page
                formatSelection: function (record) {
                    return record.name;
                }, // omitted for brevity, see the source of this page
                dropdownCssClass: "bigdrop", // apply css that makes the dropdown taller
                escapeMarkup: function (m) {
                    return m;
                } // we do not want to escape markup since we are displaying html in results
            });
        },

        saleSelect2: function (element) {
            var r = AdminRoutes.controllers.admin.mall.SaleController.index()
            element.select2({
                placeholder: "请输入关键字",
                minimumInputLength: 1,
                ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
                    url: r.url,
                    type: r.type,
                    dataType: 'json',
                    data: function (term, page) {
                        return {
                            "f.snum": term,
                            page_limit: 10
                        };
                    },
                    results: function (data, page) { // parse the results into the format expected by Select2.
                        // since we are using custom formatting functions we do not need to alter remote JSON data
                        return {
                            results: data.records
                        };
                    }
                },
                initSelection: function (element, callback) {
                    // the input tag has a value attribute preloaded that points to a preselected movie's id
                    // this function resolves that id attribute to an object that select2 can render
                    // using its formatResult renderer - that way the movie name is shown preselected
                    var id = $(element).val();
                    if (id !== "") {
                        $.ajax({
                            url: r.url,
                            type: r.type,
                            data: {
                                "f.id": id
                            },
                            dataType: "json"
                        }).done(function (data) {
                            callback(data.records[0]);
                        });
                    }
                },
                formatResult: function (record) {
                    var markup = "<table><tr>";
                    markup += "<td><h5>" + record.saleNumber + "</h5>";
                    markup += "</td></tr></table>"
                    return markup;
                }, // omitted for brevity, see the source of this page
                formatSelection: function (record) {
                    return record.saleNumber;
                }, // omitted for brevity, see the source of this page
                dropdownCssClass: "bigdrop", // apply css that makes the dropdown taller
                escapeMarkup: function (m) {
                    return m;
                } // we do not want to escape markup since we are displaying html in results
            });
        },

        productSelect2: function (element) {
            var r = AdminRoutes.controllers.admin.product.ProductController.index()
            element.select2({
                placeholder: "请输入关键字",
                minimumInputLength: 1,
                ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
                    url: r.url,
                    type: r.type,
                    dataType: 'json',
                    data: function (term, page) {
                        return {
                            "f.mku": term,
                            page_limit: 10
                        };
                    },
                    results: function (data, page) { // parse the results into the format expected by Select2.
                        // since we are using custom formatting functions we do not need to alter remote JSON data
                        return {
                            results: data.records
                        };
                    }
                },
                initSelection: function (element, callback) {
                    // the input tag has a value attribute preloaded that points to a preselected movie's id
                    // this function resolves that id attribute to an object that select2 can render
                    // using its formatResult renderer - that way the movie name is shown preselected
                    var id = $(element).val();
                    if (id !== "") {
                        $.ajax({
                            url: r.url,
                            type: r.type,
                            data: {
                                "f.id": id
                            },
                            dataType: "json"
                        }).done(function (data) {
                            callback(data.records[0]);
                        });
                    }
                },
                formatResult: function (record) {
                    var markup = "<table><tr>";
                    markup += "<td><h5>" + record.mku + "</h5>";
                    markup += "<div>" + record.name + "</div>";
                    markup += "</td></tr></table>"
                    return markup;
                }, // omitted for brevity, see the source of this page
                formatSelection: function (record) {
                    return record.mku + " " + record.name;
                }, // omitted for brevity, see the source of this page
                dropdownCssClass: "bigdrop", // apply css that makes the dropdown taller
                escapeMarkup: function (m) {
                    return m;
                } // we do not want to escape markup since we are displaying html in results
            });
        },


        skuSelect2: function (element) {
            var r = AdminRoutes.controllers.admin.product.SkuController.index()
            element.select2({
                placeholder: "请输入关键字",
                minimumInputLength: 1,
                ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
                    url: r.url,
                    type: r.type,
                    dataType: 'json',
                    data: function (term, page) {
                        return {
                            "f.sku": term,
                            page_limit: 10
                        };
                    },
                    results: function (data, page) { // parse the results into the format expected by Select2.
                        // since we are using custom formatting functions we do not need to alter remote JSON data
                        return {
                            results: data.records
                        };
                    }
                },
                initSelection: function (element, callback) {
                    // the input tag has a value attribute preloaded that points to a preselected movie's id
                    // this function resolves that id attribute to an object that select2 can render
                    // using its formatResult renderer - that way the movie name is shown preselected
                    var id = $(element).val();
                    if (id !== "") {
                        $.ajax({
                            url: r.url,
                            type: r.type,
                            data: {
                                "f.id": id
                            },
                            dataType: "json"
                        }).done(function (data) {
                            callback(data.records[0]);
                        });
                    }
                },
                formatResult: function (record) {
                    var markup = "<table><tr>";
                    markup += "<td><h5>" + record.sku + "</h5>";
                    markup += "<div>" + record.title + "</div>";
                    markup += "</td></tr></table>"
                    return markup;
                }, // omitted for brevity, see the source of this page
                formatSelection: function (record) {
                    return record.sku;
                }, // omitted for brevity, see the source of this page
                dropdownCssClass: "bigdrop", // apply css that makes the dropdown taller
                escapeMarkup: function (m) {
                    return m;
                } // we do not want to escape markup since we are displaying html in results
            });
        },


        productAttributeSetSelect2: function (element) {
            var r = AdminRoutes.controllers.admin.product.AttributeSetController.index()
            element.select2({
                placeholder: "请输入关键字",
                minimumInputLength: 1,
                ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
                    url: r.url,
                    type: r.type,
                    dataType: 'json',
                    data: function (term, page) {
                        return {
                            "f.name": term,
                            page_limit: 10
                        };
                    },
                    results: function (data, page) { // parse the results into the format expected by Select2.
                        // since we are using custom formatting functions we do not need to alter remote JSON data
                        return {
                            results: data.records
                        };
                    }
                },
                initSelection: function (element, callback) {
                    // the input tag has a value attribute preloaded that points to a preselected movie's id
                    // this function resolves that id attribute to an object that select2 can render
                    // using its formatResult renderer - that way the movie name is shown preselected
                    var id = $(element).val();
                    if (id !== "") {
                        $.ajax({
                            url: r.url,
                            type: r.type,
                            data: {
                                "f.id": id
                            },
                            dataType: "json"
                        }).done(function (data) {
                            callback(data.records[0]);
                        });
                    }
                },
                formatResult: function (record) {
                    return record.name;
                }, // omitted for brevity, see the source of this page
                formatSelection: function (record) {
                    return record.name;
                }, // omitted for brevity, see the source of this page
                dropdownCssClass: "bigdrop", // apply css that makes the dropdown taller
                escapeMarkup: function (m) {
                    return m;
                } // we do not want to escape markup since we are displaying html in results
            });
        },

        productAttributeValueSetSelect2: function (element) {
            var r = AdminRoutes.controllers.admin.product.AttributeValueSetController.index()
            element.select2({
                placeholder: "请输入关键字",
                minimumInputLength: 1,
                ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
                    url: r.url,
                    type: r.type,
                    dataType: 'json',
                    data: function (term, page) {
                        return {
                            "f.name": term,
                            page_limit: 10
                        };
                    },
                    results: function (data, page) { // parse the results into the format expected by Select2.
                        // since we are using custom formatting functions we do not need to alter remote JSON data
                        return {
                            results: data.records
                        };
                    }
                },
                initSelection: function (element, callback) {
                    // the input tag has a value attribute preloaded that points to a preselected movie's id
                    // this function resolves that id attribute to an object that select2 can render
                    // using its formatResult renderer - that way the movie name is shown preselected
                    var id = $(element).val();
                    if (id !== "") {
                        $.ajax({
                            url: r.url,
                            type: r.type,
                            data: {
                                "f.id": id
                            },
                            dataType: "json"
                        }).done(function (data) {
                            callback(data.records[0]);
                        });
                    }
                },
                formatResult: function (record) {
                    return record.name;
                }, // omitted for brevity, see the source of this page
                formatSelection: function (record) {
                    return record.name;
                }, // omitted for brevity, see the source of this page
                dropdownCssClass: "bigdrop", // apply css that makes the dropdown taller
                escapeMarkup: function (m) {
                    return m;
                } // we do not want to escape markup since we are displaying html in results
            });
        },

        productAttributeSelect2: function (element) {
            var r = AdminRoutes.controllers.admin.product.AttributeController.index()
            element.select2({
                placeholder: "请输入关键字",
                minimumInputLength: 1,
                ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
                    url: r.url,
                    type: r.type,
                    dataType: 'json',
                    data: function (term, page) {
                        return {
                            "f.name": term,
                            page_limit: 10
                        };
                    },
                    results: function (data, page) { // parse the results into the format expected by Select2.
                        // since we are using custom formatting functions we do not need to alter remote JSON data
                        return {
                            results: data.records
                        };
                    }
                },
                initSelection: function (element, callback) {
                    // the input tag has a value attribute preloaded that points to a preselected movie's id
                    // this function resolves that id attribute to an object that select2 can render
                    // using its formatResult renderer - that way the movie name is shown preselected
                    var id = $(element).val();
                    if (id !== "") {
                        $.ajax({
                            url: r.url,
                            type: r.type,
                            data: {
                                "f.id": id
                            },
                            dataType: "json"
                        }).done(function (data) {
                            callback(data.records[0]);
                        });
                    }
                },
                formatResult: function (record) {
                    return record.name;
                }, // omitted for brevity, see the source of this page
                formatSelection: function (record) {
                    return record.name;
                }, // omitted for brevity, see the source of this page
                dropdownCssClass: "bigdrop", // apply css that makes the dropdown taller
                escapeMarkup: function (m) {
                    return m;
                } // we do not want to escape markup since we are displaying html in results
            });
        },

        productAttributeOptionSelect2: function (element, attributeId) {
            var r = AdminRoutes.controllers.admin.product.AttributeOptionController.index()
            element.select2({
                placeholder: "请输入关键字",
                ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
                    url: r.url,
                    type: r.type,
                    dataType: 'json',
                    data: function (term, page) {
                        return {
                            "f.name": term,
                            "f.attid": attributeId,
                            page_limit: 10
                        };
                    },
                    results: function (data, page) { // parse the results into the format expected by Select2.
                        // since we are using custom formatting functions we do not need to alter remote JSON data
                        return {
                            results: data.records
                        };
                    }
                },
                initSelection: function (element, callback) {
                    // the input tag has a value attribute preloaded that points to a preselected movie's id
                    // this function resolves that id attribute to an object that select2 can render
                    // using its formatResult renderer - that way the movie name is shown preselected
                    var id = $(element).val();
                    if (id !== "") {
                        $.ajax({
                            url: r.url,
                            type: r.type,
                            data: {
                                "f.id": id
                            },
                            dataType: "json"
                        }).done(function (data) {
                            callback(data.records[0]);
                        });
                    }
                },
                formatResult: function (record) {
                    return record.name;
                }, // omitted for brevity, see the source of this page
                formatSelection: function (record) {
                    return record.name;
                }, // omitted for brevity, see the source of this page
                dropdownCssClass: "bigdrop", // apply css that makes the dropdown taller
                escapeMarkup: function (m) {
                    return m;
                } // we do not want to escape markup since we are displaying html in results
            });
        },

        select2: function (node) {
            node.select2({
                formatNoMatches: function () {
                    return "无"
                },
                placeholder: "选择...",
                allowClear: true
            });
        },

        //main function
        init: function () {

            //delete button
            $(".btn-delete").click(function(){
                var self = $(this);
                App.messageBox("删除记录","删除","人生不能从来，删除不可逆转，请三思而后行！", function() {
                    $.ajax({
                        url : self.data("target"),
                        type: "DELETE"
                    }).done(function(e) {
                        if(self.data("redirect")) {
                            location.href = self.data("redirect")
                        }
                        else {
                            self.closest('div, tr').fadeOut();
                        }
                    }).fail(function () {
                        App.messageBox("删除失败", "知道了", "可能原因：1.记录不存在 2.无权限 3.该记录已经关联其他记录。如果不能解决, 请截屏发至管理员邮箱.")
                    });
                });
                return false;
            });

            //date picker
            $('.date-picker').datepicker({
                autoclose: true
            });
            $('body').removeClass("modal-open"); // fix bug when inline picker is used in modal

            //wysiwyg editor
            $('.wysihtml5').wysihtml5({
            });

            //select2
            Admin.select2($("select"));
        }
    };

}();