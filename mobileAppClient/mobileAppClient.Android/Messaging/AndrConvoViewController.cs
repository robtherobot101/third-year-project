using Android.App;
using Android.Content;
using Android.OS;
using Android.Widget;
using Newtonsoft.Json;
using System;
using Android.Support.V7;
using Android.Views;
using Android.Support.V7.App;

namespace mobileAppClient.Droid.Messaging
{
    [Activity(Label = "AndrConvoViewController")]
    class AndrConvoViewController : AppCompatActivity
    {
        AndrConversation conversation;

        AndrMessageArrayAdapter MessageArrayAdapter;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);


            Toolbar toolbar = FindViewById<Toolbar>(Resource.Id.toolbar);

            //SetActionBar(toolbar);

            //this.ActionBar.SetDisplayHomeAsUpEnabled(true);

            SetContentView(Resource.Layout.AndrConvoViewController);

            conversation = JsonConvert.DeserializeObject<AndrConversation>(Intent.GetStringExtra("ConversationJSON"));

            AndrMessageArrayAdapter ArrayAdapter = new AndrMessageArrayAdapter(this, conversation.messages);

            FindViewById<ListView>(Resource.Id.messages_list_view).Adapter = MessageArrayAdapter;

            //Send button pressed
            FindViewById<Button>(Resource.Id.send_button).Click += (object sender, EventArgs e) =>
            {
                EditText editor = FindViewById<EditText>(Resource.Id.input_edittext);
                String outgoingMessage = editor.Text;
                sendMessage(outgoingMessage);
                editor.Text = "";
            };
        }

        public void receiveMessage(string text)
        {
            MessageArrayAdapter.add(new AndrMessage(AndrMessageType.Incoming, text));
            MessageArrayAdapter.NotifyDataSetChanged();
        }

        public void sendMessage(string text)
        {
            MessageArrayAdapter.add(new AndrMessage(AndrMessageType.Outgoing, text));
            MessageArrayAdapter.NotifyDataSetChanged();
        }
    }
}