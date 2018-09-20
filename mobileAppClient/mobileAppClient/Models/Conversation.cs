using System;
using System.Collections.Generic;
using System.Text;

namespace mobileAppClient.Models
{
    public class Conversation
    {
        // Unique conversation ID
        public int id { get; set; }
        public List<Message> messages { get; set; }
        public List<int> members { get; set; }
    }
}
