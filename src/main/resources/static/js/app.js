/**
 * Created by Geoff on 3/1/2015.
 */
angular.module('ignitionApp', [
    'ngRoute',
    'ignitionControllers'
])
    .config(function ($routeProvider) {
        $routeProvider
            .when('/start', {
                templateUrl: 'views/getting-started.html',
                controller: 'GettingStartedCtrl'
            })
            .otherwise({
                redirectTo: '/start'
            });
    });