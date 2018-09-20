using System;
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

        public MessageType GetType(int readersAccountID)
        {
            if (readersAccountID == this.id)
            {
                return MessageType.Outgoing;
            }
            else
            {
                return MessageType.Incoming;
            }
        }
    }
}
