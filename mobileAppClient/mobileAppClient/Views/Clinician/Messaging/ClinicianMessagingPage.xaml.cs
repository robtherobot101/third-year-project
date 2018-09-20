using mobileAppClient.Models;
using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using mobileAppClient.Models.CustomObjects;
using Xamarin.Forms;
using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class ClinicianMessagingPage : ContentPage
    {

        public CustomObservableCollection<Conversation> conversationList { get; set; }

        public ClinicianMessagingPage()
        {
            InitializeComponent();
            conversationList = new CustomObservableCollection<Conversation>();
            ConversationsListView.ItemsSource = conversationList;
            LoadConversations();
            Title = "Messages";
        }

        async void Handle_Conversation_Tapped(object sender, ItemTappedEventArgs e)
        {
            // New way:
            await Navigation.PushAsync(new ClinicianSimpleConversationPage((Conversation) e.Item));

            // Old way:
            //DependencyService.Get<CustomMessagingInterface>().CreateMessagingPage((Conversation)e.Item);
        }

        public void LoadConversations()
        {
            Conversation testConvo1 = new Conversation
            {
                conversationTitle = "Test 1",
                id = 0
            };

            Conversation testConvo2 = new Conversation
            {
                conversationTitle = "Test 2",
                id = 1
            };

            conversationList.Add(testConvo1);
            conversationList.Add(testConvo2);

        }

        private async void NewConversationTapped(object sender, EventArgs e)
        {
            await Navigation.PushAsync(new CreateConversationPage());
        }
    }
}
