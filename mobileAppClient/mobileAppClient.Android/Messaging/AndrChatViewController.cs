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
    [Activity(Label = "AndrChatViewController")]
    public class AndrChatViewController : Activity
    {
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            // Create your application here
            SetContentView(Resource.Layout.AndrChatViewController);
            Console.WriteLine("abcdefgh");
            List<TextView> messages = new List<TextView>();

            List<AndrConversation> conversations = new List<AndrConversation>();

            AndrConversation testConvo = new AndrConversation();
            testConvo.title = "Test Conversation";
            AndrMessage testMessage1 = new AndrMessage();
            testConvo.AddMessage(AndrMessageType.Incoming, "Incoming message 1");
            testConvo.AddMessage(AndrMessageType.Incoming, "Incoming message 2");
            conversations.Add(testConvo);


            AndrConversation testConvo2 = new AndrConversation();
            conversations.Add(testConvo2);

            ArrayAdapter<AndrConversation> adapter = new ArrayAdapter<AndrConversation>(this, 
                Android.Resource.Layout.SimpleListItem1, conversations);

            FindViewById<ListView>(Resource.Id.messages_listview).Adapter = adapter;

            FindViewById<ListView>(Resource.Id.messages_listview).ItemClick += (object sender, ListView.ItemClickEventArgs e) =>
            {
                Console.WriteLine("Clicked item in position: " + e.Position);
                Intent intent = new Intent(Xamarin.Forms.Forms.Context, typeof(AndrConvoViewController));
                intent.PutExtra("ConversationJSON", JsonConvert.SerializeObject(conversations[e.Position])); 
                Xamarin.Forms.Forms.Context.StartActivity(intent);

            };
        }
    }
}
