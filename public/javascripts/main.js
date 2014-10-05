(function () {
    var container = document.querySelector('#placeholder-items');
    var button = document.querySelector('#add-more-items');
    var submit = document.querySelector('#createPlaceholder');
    var width = document.querySelector('#width');
    var height = document.querySelector('#height');
    var msnry = new Masonry(container, {
        columnWidth: 5
    });

    eventie.bind( button, 'click', function() {
        // create new item elements
        var elems = [];
        var fragment = document.createDocumentFragment();
        for ( var i = 0; i < 3; i++ ) {
            var elem = document.createElement('img');
            var x = parseInt(1 + Math.random() * 100) * 4;
            var y = parseInt(1 + Math.random() * 100) * 4;
            elem.src = '/' + x + 'x' + y;
            elem.width = x;
            elem.height = y;
            fragment.appendChild( elem );
            elems.push( elem );
        }
        // prepend elements to container
        container.insertBefore( fragment, container.firstChild );
        // add and lay out newly prepended elements
        msnry.prepended( elems );
    });

    eventie.bind(submit, 'click', function () {
        document.location.href = '/' + width.value + 'x' + height.value;
    });
})();