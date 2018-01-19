var PageIndex = function () {

    var initLayerSlider = function () {
        $('#layerslider').layerSlider({
            skin : '../../assets/shared/plugins/layerslider/skins/fullwidth',
            thumbnailNavigation : 'hover',
            hoverPrevNext : false,
            responsive : false,
            responsiveUnder : 960,
            sublayerContainer : 960
        });
    }

    return {
        init: function () {
            initLayerSlider()
        }
    };

}();