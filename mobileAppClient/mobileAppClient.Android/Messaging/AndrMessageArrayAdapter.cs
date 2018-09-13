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
    class AndrMessageArrayAdapter : BaseAdapter<AndrMessage>
    {
        List<AndrMessage> messages;
        Activity context;
        private TextView chatText;

        public AndrMessageArrayAdapter(Activity context, List<AndrMessage> messages) : base()
        {
            this.context = context;
            this.messages = messages;
        }

        public override long GetItemId(int position)
        {
            return position;
        }

        public override AndrMessage this[int position]
        {
            get { return messages[position]; }
        }

        public override int Count
        {
            get { return messages.Count; }
            
        }

        public void add(AndrMessage message)
        {
            messages.Add(message);
        }

        public override View GetView(int position, View convertView, ViewGroup parent)
        {
            View view = convertView;
            AndrMessage message = messages[position];
            if (view == null)
                view = context.LayoutInflater.Inflate(Android.Resource.Layout.SimpleListItem1, null);

            LayoutInflater inflater = (LayoutInflater)context.GetSystemService(Context.LayoutInflaterService);
            if(message.type == AndrMessageType.Incoming)
            {
                view = inflater.Inflate(Resource.Layout.AndrIncomingMessage, parent, false);
            }
            else
            {
                view = inflater.Inflate(Resource.Layout.AndrOutgoingMessage, parent, false);
            }
            chatText = (TextView)view.FindViewById<TextView>(Resource.Id.msgr);
            chatText.Text = messages[position].text;

            return view;
        }
    }
}