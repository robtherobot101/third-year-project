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
using Newtonsoft.Json;

namespace mobileAppClient.Droid.Messaging
{
    [Activity(Label = "AndrConvoViewController")]
    class AndrConvoViewController : Activity
    {
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            SetContentView(Resource.Layout.AndrConvoViewController);

            AndrConversation conversation = JsonConvert.DeserializeObject<AndrConversation>(Intent.GetStringExtra("ConversationJSON"));

            ArrayAdapter<AndrMessage> adapter = new ArrayAdapter<AndrMessage>(this,
            Android.Resource.Layout.SimpleListItem1, conversation.messages);

            FindViewById<ListView>(Resource.Id.messages_list_view).Adapter = adapter;
        }
    }
}