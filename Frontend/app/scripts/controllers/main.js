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
    function sharedCallback(source,message) {
      var receivedData = JSON.parse(message.data);
      console.log(receivedData);
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
    var cSharpDataStream = $websocket ('ws://127.0.0.1:8080/CSharp');
    cSharpDataStream.onOpen(function() {console.log('websocket to C# opened!')});
    cSharpDataStream.onError(function() {console.log('error on C# websocket!')});
    cSharpDataStream.onMessage(function(message) {sharedCallback('C#',message);});
    var pythonDataStream = $websocket ('ws://127.0.0.1:8081/Python');
    pythonDataStream.onMessage(function(message) {sharedCallback('Python',message);});
    pythonDataStream.onOpen(function() {console.log('websocket to Python opened!')});
    // uncomment when java is done
    // var javaDataStream = $websocket ('wss://127.0.0.1/Java');
    // javaDataStream.onMessage(function(message) {sharedCallback('Java',message);});



    return {
      setCallBack: function(newCallback){
        if(typeof newCallback === 'function') {
          callBack=newCallback;
        } else {
          throw 'Callback must be function.';
        }

      }
    };
  })
  .controller('MainCtrl',['webSocketFactory','$scope',function (webSocketFactory,$scope) {
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

    $scope.results =  {
      'C#' : {
        count:0,
        averageDB2BE:0,
        averageBE2FE:0,
        lastOccurrences:[]
      },
      'Java' : {
        count:0,
        averageDB2BE:0,
        averageBE2FE:0,
        lastOccurrences:[]
      },
      'Python' : {
        count:0,
        averageDB2BE:0,
        averageBE2FE:0,
        lastOccurrences:[]
      }


    };


  }]);
