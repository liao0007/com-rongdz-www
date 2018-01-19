var PageBooking = function () {

    var f = $('#bookingForm')

    return {
        init: function () {
            f.validate(App.forms.validateBootstrapSettings);
        }
    };

}();