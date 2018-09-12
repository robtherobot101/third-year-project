using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;

namespace mobileAppClient.Droid.Messaging
{
    class AndrConversation
    {
        public List<AndrMessage> messages = new List<AndrMessage>();
        public string title;

        public void AddMessage(AndrMessageType type, string text)
        {
            AndrMessage newMessage = new AndrMessage();
            newMessage.text = text;
            newMessage.type = type;
            messages.Add(newMessage);

        }

        public override string ToString()
        {
            return title;
        }
    }

}