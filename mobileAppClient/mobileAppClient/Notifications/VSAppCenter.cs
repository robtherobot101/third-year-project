using Microsoft.AppCenter;
using Microsoft.AppCenter.Analytics;
using Microsoft.AppCenter.Crashes;
using Microsoft.AppCenter.Push;
using Plugin.Toasts;
using System;
using System.Collections.Generic;
using System.Dynamic;
using System.Text;
using Xamarin.Forms;

namespace mobileAppClient.Notifications
{
    class VSAppCenter
    {
        public async static void Setup()
        {

            // This should come before AppCenter.Start() is called
            // Avoid duplicate event registration:
            if (!AppCenter.Configured)
            {
                Push.PushNotificationReceived += (sender, e) =>
                {
                    // If the notification contains message data, handle it as such
                    if (e.CustomData.ContainsKey("conversationId"))
                    {
                        Message message = new Message();
                        message.id = int.Parse(e.CustomData["id"]);
                        message.conversationId = int.Parse(e.CustomData["conversationId"]);
                        message.text = e.CustomData["text"];
                        message.timestamp = new CustomDateTime(DateTime.Parse(e.CustomData["timestamp"]));
                        if (ConversationPage.currentConversation != null && ConversationPage.currentConversation.id == message.conversationId)
                        {
                            ConversationPage.currentConversation.messages.Add(message);
                        }
                        else
                        {
                            DependencyService.Get<IToast>().ShortAlert("You have received a message");
                        }
                    }
                };
            }
            AppCenter.LogLevel = LogLevel.Verbose;
            AppCenter.Start("android=95e48718-8158-4eef-ab58-fac02629e859;" +
                            "ios=14d06e7a-6ff3-4e01-8838-59cbb905dbc2;",
                            typeof(Analytics), typeof(Crashes), typeof(Push));
            var id = await AppCenter.GetInstallIdAsync();
            System.Diagnostics.Debug.WriteLine(id.ToString());

        }
    }
}
