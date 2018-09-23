using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Remoting.Messaging;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using mobileAppClient.Notifications;
using Your.Namespace;

[assembly: Xamarin.Forms.Dependency(typeof(MessageAndroid))]
    namespace Your.Namespace
    {
        public class MessageAndroid : IToast
        {
            public void LongAlert(string message)
            {
                Toast.MakeText(Application.Context, message, ToastLength.Long).Show();
            }

            public void ShortAlert(string message)
            {
                Toast.MakeText(Application.Context, message, ToastLength.Short).Show();
            }
        }
    }