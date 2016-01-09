var bookAppController = angular.module('bookTrackAppControllers', ['ngMaterial']);

bookAppController.controller('BookTrackCtrl', ['$scope', '$http', '$httpParamSerializer', '$mdDialog',

    function ($scope, $http, $httpParamSerializer, $mdDialog) {

        $http.get('/book-tracker/all').success(function (data) {
            $scope.books = data.books;
            $scope.monthDays = data.monthDays;
            $scope.allDays = data.allDays;

        });

        $scope.addBook = function() {
            $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
            $http.post('/book-tracker/addBook',
                $httpParamSerializer({ book :  $scope.newBookName}) ).success(function(data){
                $scope.books = data.books;
                $scope.newBookName = "";
            })
        };

        $scope.notReadingClick = function(ev, day, book) {
            // Appending dialog to document.body to cover sidenav in docs app
            var dateClicked = new Date(day.date);
            var dateStartReading = new Date(book.startReading);

            //if (dateClicked > dateStartReading) {
            var confirm = $mdDialog.confirm()
                .title('Reading or finished?')
                .textContent('Are you still reading "' + book.name + '" on ' + day.date + ' or have you finished it?')
                .targetEvent(ev)
                .ok("Reading")
                .cancel('Finished')
                .clickOutsideToClose(true);

            $mdDialog.show(confirm).then(function() {
                // Reading
                $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
                 $http.post('/book-tracker/addReading',
                 $httpParamSerializer({ book : book.name, date : day.date}) ).success(function(data){
                    $scope.books = data.books;
                 })
            }, function() {
                // Finished
                $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
                 $http.post('/book-tracker/finishedReading',
                 $httpParamSerializer({ book : book.name, date : day.date}) ).success(function(data){
                    $scope.books = data.books;
                 })
            });
            //}

        };

    }
]);



bookAppController.controller('BookDetailCtrl', ['$scope', '$http', '$httpParamSerializer', '$routeParams', '$window',

    function ($scope, $http, $httpParamSerializer, $routeParams, $window) {
        $scope.bookId = $routeParams.bookId;

        $scope.deleteBook = function() {
            $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
            $http.post('/book-tracker/deleteBook',
                $httpParamSerializer({ book :  $scope.bookId}) ).success(function(data){
                $window.location.href = '/book-tracker';
            })
        }
    }
]);