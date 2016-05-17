Dominion.Interface.Carousel = (function(Carousel) {
    "use strict";

    Carousel = function(carElem) {
        this.currentTab = 0;
        this.carousel = [];
        this.carElem = carElem;
        this.totalTabs = 0;
        this.elementAmount = 0; 
        this.addCarousel();
        this.addCarouselListeners();
    };

    Carousel.prototype.addCarouselListeners = function() {
        var that = this;
        this.carElem.children('a.arrow.prev').on('click', function(e) {
            e.preventDefault();
            that.prevTab();
        });
        this.carElem.children('a.arrow.next').on('click', function(e) {
            e.preventDefault();
            that.nextTab();
        });
        this.updateArrows();
    };

    Carousel.prototype.nextTab = function() {
        this.hideTab(this.currentTab);
        this.addTab();
        this.showTab(this.currentTab);
        this.updateArrows();
    };

    Carousel.prototype.prevTab = function() {
        this.hideTab(this.currentTab);
        this.subTab();
        this.showTab(this.currentTab);
        this.updateArrows();
    };

    Carousel.prototype.updateArrows = function() {
        this.showPrevArrow();
        this.showNextArrow();

        if(this.currentTab === 0) {
            if(this.currentTab === this.totalTabs){
                this.hideNextArrow();
            }
            this.hidePrevArrow();

        }

        if(this.currentTab === this.totalTabs - 1) {
            this.hideNextArrow();
        }
    };

    Carousel.prototype.subTab = function() {
        this.currentTab--;

        if (this.currentTab < 0) {
            this.currentTab = this.totalTabs - 1;
        }
    };

    Carousel.prototype.addTab = function() {
        this.currentTab++;

        if (this.currentTab >= this.TotalTabs) {
            this.currentTab = 0;
        }
    };

    Carousel.prototype.hideTab = function(tab) {
        for (var card in this.carousel[tab]) {
            this.carousel[tab][card].addClass('hidden');
        }
    };

    Carousel.prototype.showTab = function(tab) {
        for (var card in this.carousel[tab]) {
            this.carousel[tab][card].removeClass('hidden');
        }
    };

    Carousel.prototype.clearCarousel = function() {
        for (var i = 0; i < this.totalTabs; i++) {
            this.carousel[i] = [];
        }
    };

    Carousel.prototype.spreadCards = function() {
        var currCardNo = 0,
            cardSelector = this.carElem.children("ul").children("li"),
            currElem = null;

        for (var currTab = 0; currTab < this.totalTabs; currTab++) {
            while (currCardNo <= this.elementAmount) {
                currElem = cardSelector.eq(currCardNo);

                if (this.carousel[currTab].length < 5) {
                    this.carousel[currTab].push($(currElem));
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
        this.elementAmount = this.carElem.children('ul.cardContainer').children('li').length;
        this.totalTabs = Math.ceil(this.elementAmount / 5);
        this.clearCarousel();
        this.spreadCards();

        for (var i = 0; i <= this.totalTabs; i++) {
            this.hideTab(i);
        }

        this.showTab(this.currentTab);
        this.updateArrows();
    };

    return Carousel;
}(Dominion.Interface.Carousel || {}));
