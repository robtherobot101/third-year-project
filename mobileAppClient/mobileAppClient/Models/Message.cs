using System;
using Newtonsoft.Json;

namespace mobileAppClient
{
    /*
     * Class which holds informatition about a specific message
     */
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

        /*
         * Compares two Messages
         */
        public override bool Equals(object obj)
        {
            return Equals(obj as Message);
        }

        /*
         * Compares two Messages
         */
        public bool Equals(Message other)
        {
            return other != null &&
                id == other.id;
        }


        public override int GetHashCode()
        {
            return 1877310944 + id.GetHashCode();
        }

        /*
         * Defines the type of message
         */
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
