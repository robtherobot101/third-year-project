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
using mobileAppClient.Views;
using Xamarin.Forms;

[assembly: Dependency(typeof(mobileAppClient.Droid.Messaging.AndrMessagingEntryClass))]
namespace mobileAppClient.Droid.Messaging
{
    [Activity(Label = "AndrMessagingEntryClass", MainLauncher = false)]
    class AndrMessagingEntryClass : CustomMessagingInterface
    {
        public void CreateMessagingPage()
        {
            Intent intent = new Intent(Forms.Context, typeof(AndrChatViewController));
            Forms.Context.StartActivity(intent);
            
            
        }
    }
}