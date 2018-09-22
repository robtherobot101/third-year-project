using System;
using Newtonsoft.Json;

namespace mobileAppClient
{
    public class Message
    {
        // Unique message ID
        public int id { get; set; }
        public string text { get; set; }
        public CustomDateTime timestamp { get; set; }
        // Senders account ID
        public int userId { get; set; }

        [JsonIgnore]
        public MessageType messageType { get; set; }
        [JsonIgnore]
        public DateTime timestampDateTime
        {
            get { return timestamp.ToDateTime(); }
        }

        public void SetType(int readersAccountID)
        {
            if (readersAccountID == this.userId)
            {
                this.messageType = MessageType.Outgoing;
            }
            else
            {
                this.messageType = MessageType.Incoming;
            }
        }
    }
}
