using Microsoft.AppCenter;
using Microsoft.AppCenter.Analytics;
using Microsoft.AppCenter.Crashes;
using Microsoft.AppCenter.Push;
using System;
using System.Collections.Generic;
using System.Text;
using Xamarin.Forms;

namespace mobileAppClient.Notifications
{
    class VSAppCenter
    {
        public static void Setup()
        {
            AppCenter.Start("android=95e48718-8158-4eef-ab58-fac02629e859;" +
                            "ios=14d06e7a-6ff3-4e01-8838-59cbb905dbc2;",
                            typeof(Analytics), typeof(Crashes), typeof(Push));
        }
    }
}
