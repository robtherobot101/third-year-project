using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using mobileAppClient.odmsAPI;

namespace mobileAppClient.Models
{
    public class Conversation
    {
        // Unique conversation ID
        public int id { get; set; }
        public List<Message> messages { get; set; }
        public List<int> members { get; set; }

        public string localName { get; set; }
        public string externalName { get; set; }
        public Dictionary<int, String> memberNames { get; set; }

        public string lastMessage
        {
            get { return messages.Count > 0 ? messages.Last().text : ""; }
        }

        public void getParticipantNames(int localId)
        {
            // Gets the complement of the members list in respect to the local id -> the external participants id
            members.Remove(localId);
            localName = memberNames[localId];
            externalName = memberNames[members[0]];
        } 
    }
}
