Dominion.Interface.Carousel = (function(Carousel) {
    Carousel = function(carElem) {
        this.currentTab = 0;
        this.carousel = [];
        this.carElem = carElem;
    };

    Carousel.prototype.addCarouselListeners = function(totalTabs) {
        var that = this;
        this.carElem.children('a.arrow.prev').on('click', function() {
            that.nextTab(totalTabs);
        });
        this.carElem.children('a.arrow.next').on('click', function() {
            that.prevTab(totalTabs);
        });
        this.updateArrows(totalTabs);
    };

    Carousel.prototype.nextTab = function(totalTabs) {
        this.hideTab(this.currentTab);
        this.addTab(totalTabs);
        this.showTab(this.currentTab);
        this.updateArrows(totalTabs);
    };

    Carousel.prototype.prevTab = function(totalTabs) {
        this.hideTab(this.currentTab);
        this.subTab(totalTabs);
        this.showTab(this.currentTab);
        this.updateArrows(totalTabs);
    };

    Carousel.prototype.updateArrows = function(totalTabs) {
        this.showPrevArrow();
        this.showNextArrow();
        if(this.currentTab === 0) this.hidePrevArrow();
        if(this.currentTab === totalTabs) this.hideNextArrow();
    };

    Carousel.prototype.subTab = function(totalTabs) {
        this.currentTab--;
        if (this.currentTab < 0)
            this.currentTab = totalTabs - 1;
    };

    Carousel.prototype.addTab = function(totalTabs) {
        this.currentTab++;
        if (this.currentTab >= totalTabs)
            this.currentTab = 0;
    };

    Carousel.prototype.addCarousel = function() {
        var elementAmount = this.carElem.children('#handPile').children('li.card').length;
        var totalTabs = Math.ceil(elementAmount / 5);
        this.clearCarousel(totalTabs);
        this.spreadCards(totalTabs, elementAmount);
        for (var i = 1; i <= totalTabs; i++) {
            this.hideTab(i);
        }
        this.showTab(this.currentTab);
        this.addCarouselListeners(totalTabs);
        console.log('Carousel Added:', this.carElem);
        console.log('CarArray :', this.carousel);
        console.log('CurTab:', this.currentTab);
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

    Carousel.prototype.showPrevArrow = function() {
        this.carElem.children('a.arrow.prev').css('opacity', '1');
        this.carElem.children('a.arrow.prev').css('pointer-events', 'all');
        console.log('showPrevArrow');
    };

    Carousel.prototype.showNextArrow = function() {
        this.carElem.children('a.arrow.next').css('opacity', '1');
        this.carElem.children('a.arrow.next').css('pointer-events', 'all');
        console.log('showNextArrow');
    };

    Carousel.prototype.hidePrevArrow = function() {
        this.carElem.children('a.arrow.prev').css('opacity', '0');
        this.carElem.children('a.arrow.prev').css('pointer-events', 'none');
        console.log('hidePrevArrow');
    };

    Carousel.prototype.hideNextArrow = function() {
        this.carElem.children('a.arrow.next').css('opacity', '0');
        this.carElem.children('a.arrow.next').css('pointer-events', 'none');
        console.log('hideNextArrow');
    };

    Carousel.prototype.clearCarousel = function(totalTabs) {
        for (var i = 0; i < totalTabs; i++)
            this.carousel[i] = [];
    };

    Carousel.prototype.spreadCards = function(totalTabs, amount) {
        var j = 0;
        for (var k = 0; k < totalTabs; k++) {
            while (j <= amount) {
                var currElem = this.carElem.children('#handPile').children('li.card').eq(j);
                if (this.carousel[k].length < 5)
                    this.carousel[k].push($(currElem));
                else break;
                j++;
            }
        }
    };

    return Carousel;
}(Dominion.Interface.Carousel || {}));
