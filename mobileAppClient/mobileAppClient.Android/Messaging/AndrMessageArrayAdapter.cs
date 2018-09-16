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
    class AndrMessageArrayAdapter : BaseAdapter<Message>
    {
        List<Message> messages;
        Activity context;
        private TextView chatText;

        public AndrMessageArrayAdapter(Activity context, List<Message> messages) : base()
        {
            this.context = context;
            this.messages = messages;
        }

        public override long GetItemId(int position)
        {
            return position;
        }

        public override Message this[int position]
        {
            get { return messages[position]; }
        }

        public override int Count
        {
            get { return messages.Count; }
            
        }

        public void add(Message message)
        {
            messages.Add(message);
        }

        public override View GetView(int position, View convertView, ViewGroup parent)
        {
            View view = convertView;
            Message message = messages[position];
            if (view == null)
                view = context.LayoutInflater.Inflate(Android.Resource.Layout.SimpleListItem1, null);

            LayoutInflater inflater = (LayoutInflater)context.GetSystemService(Context.LayoutInflaterService);
            if(message.Type == MessageType.Incoming)
            {
                view = inflater.Inflate(Resource.Layout.AndrIncomingMessage, parent, false);
            }
            else
            {
                view = inflater.Inflate(Resource.Layout.AndrOutgoingMessage, parent, false);
            }
            chatText = (TextView)view.FindViewById<TextView>(Resource.Id.msgr);
            chatText.Text = messages[position].Text;

            return view;
        }
    }
}