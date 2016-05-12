Dominion.Interface.Carousel = (function(Carousel) {
    "use strict";

    var that = null;

    Carousel = function(carElem) {
        this.currentTab = 0;
        this.carousel = [];
        this.carElem = carElem;
        this.totalTabs = 0;
        this.elementAmount = 0;
        that = this;
        addCarouselListeners(this.totalTabs);
    };

    var addCarouselListeners = function() {
        that.carElem.children('a.arrow.prev').on('click', function() {
            prevTab();
            console.log('prevTab called');
        });
        that.carElem.children('a.arrow.next').on('click', function() {
            nextTab();
            console.log('nextTab called');
        });
        updateArrows();
    };

    var nextTab = function() {
        hideTab(that.currentTab);
        addTab();
        showTab(that.currentTab);
        updateArrows();
    };

    var prevTab = function() {
        hideTab(that.currentTab);
        subTab();
        showTab(that.currentTab);
        updateArrows();
    };

    var updateArrows = function() {
        that.showPrevArrow();
        that.showNextArrow();

        if(that.currentTab === 0) {
            that.hidePrevArrow();
        }

        if(that.currentTab === that.totalTabs - 1) {
            that.hideNextArrow();
        }
    };

    var subTab = function() {
        that.currentTab--;

        if (that.currentTab < 0) {
            that.currentTab = that.totalTabs - 1;
        }
    };

    var addTab = function() {
        that.currentTab++;

        if (that.currentTab >= that.TotalTabs) {
            that.currentTab = 0;
        }
    };

    var hideTab = function(tab) {
        for (var card in that.carousel[tab]) {
            that.carousel[tab][card].addClass('hidden');
        }
    };

    var showTab = function(tab) {
        for (var card in that.carousel[tab]) {
            that.carousel[tab][card].removeClass('hidden');
        }
    };

    var clearCarousel = function() {
        for (var i = 0; i < that.totalTabs; i++) {
            that.carousel[i] = [];
        }
    };

    var spreadCards = function() {
        var currCardNo = 0,
            cardSelector = that.carElem.children("#handPile").children("li.card"),
            currElem = null;

        for (var currTab = 0; currTab < that.totalTabs; currTab++) {
            while (currCardNo <= that.elementAmount) {
                currElem = cardSelector.eq(currCardNo);

                if (that.carousel[currTab].length < 5) {
                    that.carousel[currTab].push($(currElem));
                } else {
                    break;
                }

                currCardNo++;
            }
        }
    };

    Carousel.prototype.showPrevArrow = function() {
        this.carElem.children('a.arrow.prev').css('opacity', '1');
        this.carElem.children('a.arrow.prev').css('pointer-events', 'all');
    };

    Carousel.prototype.showNextArrow = function() {
        this.carElem.children('a.arrow.next').css('opacity', '1');
        this.carElem.children('a.arrow.next').css('pointer-events', 'all');
    };

    Carousel.prototype.hidePrevArrow = function() {
        this.carElem.children('a.arrow.prev').css('opacity', '0');
        this.carElem.children('a.arrow.prev').css('pointer-events', 'none');
    };

    Carousel.prototype.hideNextArrow = function() {
        this.carElem.children('a.arrow.next').css('opacity', '0');
        this.carElem.children('a.arrow.next').css('pointer-events', 'none');
    };

    Carousel.prototype.addCarousel = function() {
        this.elementAmount = this.carElem.children('#handPile').children('li.card').length;
        this.totalTabs = Math.ceil(this.elementAmount / 5);
        clearCarousel();
        spreadCards();

        for (var i = 1; i <= this.totalTabs; i++) {
            hideTab(i);
        }

        showTab(this.currentTab);
    };

    return Carousel;
}(Dominion.Interface.Carousel || {}));
