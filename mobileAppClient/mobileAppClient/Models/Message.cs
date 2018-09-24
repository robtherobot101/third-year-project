using System;
using Newtonsoft.Json;

namespace mobileAppClient
{
    public class Message : IEquatable<Message>
    {
        // Unique message ID
        public int id { get; set; }
        public string text { get; set; }
        public CustomDateTime timestamp { get; set; }
        // Senders account ID
        public int userId { get; set; }
        public int conversationId { get; set; }

        [JsonIgnore]
        public MessageType messageType { get; set; }
        [JsonIgnore]
        public DateTime timestampDateTime
        {
            get { return timestamp.ToDateTimeWithSeconds(); }
        }

        public override bool Equals(object obj)
        {
            return Equals(obj as Message);
        }

        public bool Equals(Message other)
        {
            return other != null &&
                   id == other.id;
        }

        public override int GetHashCode()
        {
            return 1877310944 + id.GetHashCode();
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
