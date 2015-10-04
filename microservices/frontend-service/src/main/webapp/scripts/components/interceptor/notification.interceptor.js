 'use strict';

angular.module('scaffoldApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-scaffoldApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-scaffoldApp-params')});
                }
                return response;
            },
        };
    });