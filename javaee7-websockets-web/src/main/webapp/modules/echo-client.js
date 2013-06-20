(function() {
	'use strict';

	angular.module('echo-client', [])

	.directive('echoClient', function() {
		return {
			templateUrl : 'modules/echo-client/template.html',
			replace : true,
			controller : [ '$scope', '$attrs',

			function($scope, $attrs) {
				$scope.message = '';
				$scope.interactions = [];

				var loc = window.location;
				var wsUri;
				if (loc.protocol === 'https:') {
					wsUri = 'wss:';
				} else {
					wsUri = 'ws:';
				}
				wsUri += '//' + loc.host + loc.pathname.substring(0, loc.pathname.lastIndexOf('/')) + '/echo';

				var websocket = new WebSocket(wsUri);

				websocket.onmessage = function(event) {
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
					websocket.send($scope.message);
					$scope.message = '';
				};
			} ]

		};
	});
}());
