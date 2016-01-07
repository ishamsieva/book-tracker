var bookApp = angular.module('bookTrackApp', ['ngMaterial', 'ngRoute', 'bookTrackAppControllers']);

bookApp.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.
        when('/book-list', {
            templateUrl: 'partials/book-list.html',
            controller: 'BookTrackCtrl'
        }).
        when('/books/:bookId', {
            templateUrl: 'partials/book-detail.html',
            controller: 'BookDetailCtrl'
        }).
        otherwise({
            redirectTo: '/book-list'
        });
    }
]);