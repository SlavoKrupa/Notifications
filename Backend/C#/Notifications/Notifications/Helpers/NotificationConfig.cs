using System;
using System.Net;
using Newtonsoft.Json;
using Notifications.DbContext;
using Npgsql;
using WebSocketSharp.Server;
using Newtonsoft.Json.Converters;

namespace Notifications.Helpers
{
    public class NotificationConfig
    {
        private static WebSocketServer _webSocketServer;

        /// <summary>
        ///     Registers listener to channel
        /// </summary>
        /// <remarks>
        ///     Creates and maintain database connection. Starts listening on <c>SIMPLE_NOTIFY_CHANNEL</c> channel
        /// </remarks>
        public static void StartListen()
        {
            var context = new ApplicationDatabaseContext();

            var conn = context.Database.Connection as NpgsqlConnection;
            conn.Open();
            conn.Notification += OnNotification;

            using (var command = new NpgsqlCommand("listen \"SIMPLE_NOTIFY_CHANNEL\";", conn))
            {
                command.ExecuteNonQuery();
            }
        }

        /// <summary>
        ///     Starts WebSocket server on localhost port 8080.
        /// </summary>
        /// <remarks>
        ///     Also endpoint with <c>/CSharp</c> path is registered
        /// </remarks>
        public static void StartWebSocketServer()
        {
            _webSocketServer = new WebSocketServer(IPAddress.Parse("127.0.0.1"), 8080);
            _webSocketServer.AddWebSocketService<Echo>("/CSharp");
            _webSocketServer.Start();
        }

        private static void OnNotification(object sender, NpgsqlNotificationEventArgs e)
        {
            var notification = JsonConvert.DeserializeObject<Notification>(e.AdditionalInformation);
            // yes ugly timezone hack :(
            notification.CreationTime = notification.CreationTime.AddHours(-2);
            notification.ReceivingTimeBackend = DateTime.Now;
            _webSocketServer.WebSocketServices["/CSharp"].Sessions.Broadcast(JsonConvert.SerializeObject(notification, new IsoDateTimeConverter()));
        }

        private class Echo : WebSocketBehavior
        {
        }
    }
}