using System;
using Newtonsoft.Json;

namespace Notifications.Helpers
{
    /// <summary>
    ///     Represents simple notification from database with added 'backend_receiving' property
    /// </summary>
    public class Notification
    {
        /// <summary>
        ///     Id of notification
        /// </summary>
        public long Id { get; set; }

        /// <summary>
        ///     Time when notification was created in database
        /// </summary>
        [JsonProperty(PropertyName = "db_creation")]
        public DateTime CreationTime { get; set; }

        /// <summary>
        ///     Time when notofication was received by backend
        /// </summary>
        [JsonProperty(PropertyName = "backend_receiving")]
        public DateTime ReceivingTimeBackend { get; set; }

        public override string ToString()
        {
            return $"Id: {Id}\nCreationTime: {CreationTime}\nReceivingTime: {ReceivingTimeBackend}\n\n";
        }
    }
}