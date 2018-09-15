using Android.App;
using Android.Content;
using Android.OS;
using Android.Widget;
using Newtonsoft.Json;
using System;
using Android.Support.V7;
using Android.Views;
using Android.Support.V7.App;
using Android.Support.V4.App;
using Android.Graphics;

namespace mobileAppClient.Droid.Messaging
{
    [Activity(Label = "AndrConvoViewController", ParentActivity = typeof(AndrChatViewController), WindowSoftInputMode = SoftInput.AdjustPan)]
    class AndrConvoViewController : AppCompatActivity
    {
        AndrConversation conversation;

        AndrMessageArrayAdapter MessageArrayAdapter;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            Console.WriteLine("opopop");
            SetContentView(Resource.Layout.AndrConvoViewController);

            var toolbar = FindViewById<Toolbar>(Resource.Id.custom_toolbar);
            toolbar.SetTitleTextColor(Color.White);
            toolbar.Title = "";
            SetActionBar(toolbar);
            ActionBar.SetDisplayHomeAsUpEnabled(true);
            toolbar.NavigationOnClick += (sender, e) =>
            {
                NavUtils.NavigateUpFromSameTask(this);
            };



            conversation = JsonConvert.DeserializeObject<AndrConversation>(Intent.GetStringExtra("ConversationJSON"));

            MessageArrayAdapter = new AndrMessageArrayAdapter(this, conversation.messages);

            FindViewById<ListView>(Resource.Id.messages_list_view).Adapter = MessageArrayAdapter;

            //Send button pressed
            FindViewById<Button>(Resource.Id.send_button).Click += (object sender, EventArgs e) =>
            {
                EditText editor = FindViewById<EditText>(Resource.Id.input_edittext);
                String outgoingMessage = editor.Text;
                if(outgoingMessage != "")
                {
                    sendMessage(outgoingMessage.Trim());
                    editor.Text = "";
                }
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