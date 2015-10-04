'use strict';

angular.module('scaffoldApp')
    .factory('ConfigurationService', function ($rootScope, $filter, $http) {
        return {
            get: function() {
                return $http.get('configprops').then(function (response) {
                    var properties = [];
                    angular.forEach(response.data, function (data) {
                        properties.push(data);
                    });
                    var orderBy = $filter('orderBy');
                    return orderBy(properties, 'prefix');
                });
            }
        };
    });
