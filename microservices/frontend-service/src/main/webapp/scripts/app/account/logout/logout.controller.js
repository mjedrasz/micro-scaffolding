'use strict';

angular.module('scaffoldApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
