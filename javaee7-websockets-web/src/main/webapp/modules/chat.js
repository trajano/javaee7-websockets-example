(function() {
	'use strict';

	angular.module('chat', [])

	.factory(
			'chatSocket',
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
								.lastIndexOf('/')) + '/chat';
				var webSocket = new WebSocket(wsUri);

				window.onbeforeunload = function() {
					websocket.onclose = function() {
					}; // disable onclose handler first
					websocket.close();
				};

				return webSocket;
			})

	.directive('chat', function() {
		return {
			templateUrl : 'modules/chat/template.html',
			replace : true,
			controller : [ '$scope', '$attrs', 'chatSocket',

			function($scope, $attrs, echoSocket) {

				$scope.sessionIds = [];
				$scope.messages = [];
				$scope.textToSend = '';
				$scope.toSessionId = null;

				echoSocket.onmessage = function(event) {
					var message = angular.fromJson(event.data);
					message.direction = 'RECEIVED';
					if (message.sessionIds) {
						$scope.sessionIds = message.sessionIds;
						console.warn(message.sessionIds);
					} else if (message.text) {
						console.warn(message);
						$scope.messages.push(message);
					}
					$scope.$apply();
				};

				$scope.sendMessage = function() {
					var message = {
						direction : 'SENT',
						text : $scope.textToSend,
					};
					if ($scope.toSessionId) {
						message.to = $scope.toSessionId;
					}
					$scope.messages.push(message);
					echoSocket.send(angular.toJson(message));
					$scope.textToSend = '';
				};

			} ]

		};
	});
}());
