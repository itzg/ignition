/**
 * Created by Geoff on 3/1/2015.
 */

angular.module('ignitionControllers', [])
    .controller('OuterCtrl', function ($scope) {

    })

    .controller('GettingStartedCtrl', function ($scope, $http) {
        $scope.channel = "stable";
        $scope.channels = ["stable", "beta", "alpha"];

        $scope.pull = function (distro) {
            $http.post('/config/images/' + distro + '/' + $scope.channel)
                .error(function (data, status, headers, config) {
                    console.warn('Failed to start image pull');
                });
        }
    })
;