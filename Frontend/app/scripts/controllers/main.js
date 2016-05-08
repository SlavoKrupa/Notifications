'use strict';

/**
 * @ngdoc function
 * @name Notifications.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the Notifications
 */
angular.module('Notifications')
  .service('webSocketFactory',function($websocket) {
    var callBack = function(){};
    var errorCallBack =  function(){};
    function sharedCallback(source,message) {
      var receivedData = JSON.parse(message.data);
      receivedData.db_creation = new Date(receivedData.db_creation);
      receivedData.backend_receiving = new Date(receivedData.backend_receiving);
      receivedData.frontend_receiving= new Date();
      var newItem = {
        source: source,
        DB2BE: receivedData.backend_receiving - receivedData.db_creation,
        BE2FE: receivedData.frontend_receiving - receivedData.backend_receiving
      };
          callBack(newItem);
    }
    // Open a WebSocket connection
    var webSockets = {};
    var urls = {
      'C#' : 'ws://127.0.0.1:8080/CSharp',
      'Java' : 'ws://127.0.0.1:8082/java',
      'Python': 'ws://127.0.0.1:8081/Python'
    };

    return {
      setCallBack: function(newCallback){
        if(typeof newCallback === 'function') {
          callBack=newCallback;
        } else {
          throw 'Callback must be function.';
        }

      },
      openWs:function(language) {
        webSockets[language] = $websocket (urls[language]);
        webSockets[language].onOpen(function() {console.log('websocket to ' + language+' opened!');});
        webSockets[language].onClose(function() {
          console.log('websocket to ' + language+' closed!');
          errorCallBack(language);
        });
        webSockets[language].onError(function() {
          console.log('Error on ' + language+' socket!');
          errorCallBack(language);
        });
        webSockets[language].onMessage(function(message) {sharedCallback(language,message);});



      },
      closeWs:function(language) {
        webSockets[language].close(true);
      },
      setWebSocketErrorCallback:function (newCallback) {
        if(typeof newCallback === 'function') {
          errorCallBack=newCallback;
        } else {
          throw 'Callback must be function.';
        }

      }
    };
  })
  .controller('MainCtrl',['webSocketFactory','$scope',function (webSocketFactory,$scope) {
    $scope.SUPPORTED_LANGUAGES = ['C#','Java','Python'];
    webSocketFactory.setCallBack( function(newData) {
      var allData = $scope.results[newData.source];
      allData.lastOccurrences.push(newData);
      if(allData.lastOccurrences.length > 10 )
      {
        allData.lastOccurrences.pop();
      }
      var newCount = allData.count + 1;
      var newAverageDB2BE = (allData.averageDB2BE * allData.count + newData.DB2BE ) / newCount;
      var newAverageBE2FE = (allData.averageBE2FE * allData.count + newData.BE2FE ) / newCount;
      allData.count = newCount;
      allData.averageBE2FE = newAverageBE2FE;
      allData.averageDB2BE = newAverageDB2BE;

  	});
    webSocketFactory.setWebSocketErrorCallback(function(language) {
      $scope.results[language].isRunning = OFF_CLASS;

    });

    var OFF_CLASS = 'btn-danger';
    var ON_CLASS = 'btn-success';
    $scope.results = {};
    $scope.clearData= function () {
      angular.forEach($scope.SUPPORTED_LANGUAGES, function(language) {
        $scope.results[language] = {
          isRunning: isOnline (language) ? ON_CLASS : OFF_CLASS,
          count:0,
          averageDB2BE:0,
          averageBE2FE:0,
          lastOccurrences:[]
        };
      });
    };
    $scope.clearData();

    function isOnline(language){
          return angular.isDefined($scope.results[language]) &&  $scope.results[language].isRunning === ON_CLASS;
          }

    $scope.switchWebsocket = function(language) {
      var isTurnedOn = isOnline(language);
      if(isTurnedOn) {
        webSocketFactory.closeWs(language);
        $scope.results[language].isRunning = OFF_CLASS;
      } else {
        webSocketFactory.openWs(language);
        $scope.results[language].isRunning = ON_CLASS;
      }
    };


  }]);
