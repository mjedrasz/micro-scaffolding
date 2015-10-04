'use strict';

angular.module('scaffoldApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


