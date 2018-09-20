using mobileAppClient.Models;
using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using Xamarin.Forms;
using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class ClinicianMessagingPage : ContentPage
    {
        public ClinicianMessagingPage()
        {
            InitializeComponent();

            loadMessages();
        }

        async void Handle_Conversation_Tapped(object sender, Xamarin.Forms.ItemTappedEventArgs e)
        {
            DependencyService.Get<CustomMessagingInterface>().CreateMessagingPage((Conversation)e.Item);
        }

        public void loadMessages()
        {
            Conversation testConvo = new Conversation();
            testConvo.title = "Test Conversation";

            List<Conversation> conversations = new List<Conversation>();
            conversations.Add(testConvo);
            ConversationsListView.ItemsSource = conversations;
        }
    }
}
