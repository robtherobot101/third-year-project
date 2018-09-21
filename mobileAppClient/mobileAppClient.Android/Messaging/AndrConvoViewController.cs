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
using System.Collections.Generic;

namespace mobileAppClient.Droid.Messaging
{
    [Activity(Label = "AndrConvoViewController", ParentActivity = typeof(MainActivity), WindowSoftInputMode = SoftInput.AdjustPan)]
    class AndrConvoViewController : AppCompatActivity
    {
        List<Message> messages;
        AndrMessageArrayAdapter MessageArrayAdapter;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            SetContentView(Resource.Layout.AndrConvoViewController);

            var toolbar = FindViewById<Toolbar>(Resource.Id.custom_toolbar);
            toolbar.SetTitleTextColor(Color.White);
            toolbar.Title = "Conversation";
            SetActionBar(toolbar);
            ActionBar.SetDisplayHomeAsUpEnabled(true);

            toolbar.NavigationOnClick += (sender, e) =>
            {
                this.Finish();
            };

            messages = new List<Message> {
                new Message { Type = MessageType.Incoming, Text = "Hello!" },
                new Message { Type = MessageType.Outgoing, Text = "Hi!" },
                new Message { Type = MessageType.Incoming, Text = "Do you know about Xamarin?" },
                new Message { Type = MessageType.Outgoing, Text = "Yes! Sure!" },
                new Message { Type = MessageType.Incoming, Text = "And what do you think?" },
                new Message { Type = MessageType.Outgoing, Text = "I think it is the best way to develop mobile applications." },
                new Message { Type = MessageType.Incoming, Text = "Wow :-)" },
                new Message { Type = MessageType.Outgoing, Text = "Yep. Check it out\nhttp://xamarin.com" },
                new Message { Type = MessageType.Incoming, Text = "Will do. Thanks" }
            };

            MessageArrayAdapter = new AndrMessageArrayAdapter(this, messages);

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
            MessageArrayAdapter.add(new Message { Type = MessageType.Incoming, Text = text });
            MessageArrayAdapter.NotifyDataSetChanged();
        }

        public void sendMessage(string text)
        {
            MessageArrayAdapter.add(new Message { Type = MessageType.Outgoing, Text = text });
            MessageArrayAdapter.NotifyDataSetChanged();
        }
    }
}