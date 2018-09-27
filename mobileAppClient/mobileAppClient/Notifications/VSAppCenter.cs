using Microsoft.AppCenter;
using Microsoft.AppCenter.Analytics;
using Microsoft.AppCenter.Crashes;
using Microsoft.AppCenter.Push;
using Plugin.Toasts;
using System;
using System.Collections.Generic;
using System.Dynamic;
using System.Text;
using System.Threading.Tasks;
using mobileAppClient.Models;
using Xamarin.Forms;

namespace mobileAppClient.Notifications
{
    /*
     * Class for handling push notifications with the AppCenter
     */
    public static class VSAppCenter
    {
        static MessageThreadsListPage messageThreadsListPageController;
        static ConversationPage conversationController;

        public static async void Setup()
        {

            // This should come before AppCenter.Start() is called
            // Avoid duplicate event registration:
            if (!AppCenter.Configured)
            {
                Push.PushNotificationReceived += async (sender, e) =>
                {
                    // If the notification contains message data, handle it as such
                    if (e.CustomData.ContainsKey("conversationId"))
                    {
                        Message notifiedMessage = new Message();
                        notifiedMessage.id = int.Parse(e.CustomData["id"]);
                        notifiedMessage.conversationId = int.Parse(e.CustomData["conversationId"]);
                        notifiedMessage.text = e.CustomData["text"];
                        notifiedMessage.timestamp = new CustomDateTime(DateTime.Parse(e.CustomData["timestamp"]));
                        
                        if (conversationController != null)
                        {
                            if (conversationController.conversation != null && conversationController.conversation.id == notifiedMessage.conversationId) 
                            {
                                if(!conversationController.conversationMessages.Contains(notifiedMessage)) {
                                    conversationController.conversationMessages.Insert(0, notifiedMessage);
                                }
                            }
                        }
                        else
                        {
                            if (messageThreadsListPageController != null)
                            {
                                List<Conversation> localConversation = new List<Conversation>(messageThreadsListPageController.conversationList);
                                Conversation conversationToUpdate = localConversation.Find(conversation =>
                                    conversation.id == notifiedMessage.conversationId);

                                if (conversationToUpdate != null)
                                {
                                    conversationToUpdate.messages.Insert(0, notifiedMessage);
                                    messageThreadsListPageController.conversationList.Clear();
                                    messageThreadsListPageController.conversationList.AddRange(localConversation);
                                }
                                else
                                {
                                    await messageThreadsListPageController.ReloadConversations();
                                }
                            }
                                                        
                        }

                        DependencyService.Get<IToast>().ShortAlert("You have received a message");
                        

                    }
                };
            }
            AppCenter.Start("android=95e48718-8158-4eef-ab58-fac02629e859;" +
                            "ios=14d06e7a-6ff3-4e01-8838-59cbb905dbc2;",
                            typeof(Analytics), typeof(Crashes), typeof(Push));
            var id = await AppCenter.GetInstallIdAsync();
            System.Diagnostics.Debug.WriteLine(id.ToString());
        }

        public static void setConversationListController(MessageThreadsListPage messageThreadsListPageController)
        {
            VSAppCenter.messageThreadsListPageController = messageThreadsListPageController;
        }

        public static void setConversationController(ConversationPage conversationController)
        {
            VSAppCenter.conversationController = conversationController;
        }
    }
}
