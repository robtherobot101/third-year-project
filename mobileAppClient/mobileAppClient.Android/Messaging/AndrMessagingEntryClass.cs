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
using mobileAppClient.Models;
using mobileAppClient.Views;
using Xamarin.Forms;

[assembly: Dependency(typeof(mobileAppClient.Droid.Messaging.AndrMessagingEntryClass))]
namespace mobileAppClient.Droid.Messaging
{
    [Activity(Label = "AndrMessagingEntryClass", MainLauncher = false)]
    class AndrMessagingEntryClass : CustomMessagingInterface
    {
        public void CreateMessagingPage(Conversation c)
        {
            Intent intent = new Intent(Forms.Context, typeof(AndrConvoViewController));
            intent.PutExtra("convo_id", c.id); // Pass the conversation id so that messages can be fetched from server.
            Forms.Context.StartActivity(intent);
        }
    }
}