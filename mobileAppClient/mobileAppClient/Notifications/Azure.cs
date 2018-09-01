using Microsoft.WindowsAzure.MobileServices;
using System;
using System.Collections.Generic;
using System.Text;

namespace mobileAppClient.Notifications
{
    public class Azure
    {
        public static Azure Instance = new Azure();
        MobileServiceClient client;

        private Azure()
        {
            this.client = new MobileServiceClient("https://transcend.azurewebsites.net");
        }

        public MobileServiceClient CurrentClient
        {
            get { return client; }
        }
    }
}
