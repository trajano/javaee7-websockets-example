(function() {
	'use strict';

	angular.module('echo-client', [])

	.factory(
			'echoSocket',
			function() {
				var loc = window.location;
				var wsUri;
				if (loc.protocol === "https:") {
					wsUri = "wss:";
				} else {
					wsUri = "ws:";
				}
				wsUri += '//'
						+ loc.host
						+ loc.pathname.substring(0, loc.pathname
								.lastIndexOf('/')) + '/echo';

				return new WebSocket(wsUri);
			})

	.directive('echoClient', function() {
		return {
			templateUrl : 'modules/echo-client/template.html',
			replace : true,
			controller : [ '$scope', '$attrs', 'echoSocket',

			function($scope, $attrs, echoSocket) {
				$scope.message = '';
				$scope.interactions = [];

				echoSocket.onmessage = function(event) {
					$scope.interactions.push({
						direction : 'RECEIVED',
						message : event.data
					});
					$scope.$apply();
				};

				$scope.sendMessage = function() {
					$scope.interactions.push({
						direction : 'SENT',
						message : $scope.message
					});
					echoSocket.send($scope.message);
					$scope.message = '';
				};
			} ]

		};
	});
}());
