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

        public void Add(AndrMessage message)
        {
            messages.Add(message);
        }

        public override string ToString()
        {
            return title;
        }
    }

}