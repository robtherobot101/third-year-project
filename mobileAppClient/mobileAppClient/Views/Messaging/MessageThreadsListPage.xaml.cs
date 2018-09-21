using System;
using System.Collections.Generic;
using mobileAppClient.Models;
using mobileAppClient.Models.CustomObjects;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace mobileAppClient
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class MessageThreadsListPage : ContentPage
    {
        private int localId { get; set; }
        public CustomObservableCollection<Conversation> conversationList { get; set; }

        public MessageThreadsListPage()
        {
            InitializeComponent();

            this.localId = localId;
            conversationList = new CustomObservableCollection<Conversation>();
            ConversationsListView.ItemsSource = conversationList;
            LoadConversations();
            Title = "Messages";
        }

        async void Handle_Conversation_Tapped(object sender, ItemTappedEventArgs e)
        {
            await Navigation.PushAsync(new ConversationPage((Conversation) e.Item));
        }

        public void LoadConversations()
        {
            // TODO call getConversations
            List<Conversation> apiGotConversations = new List<Conversation>();

            conversationList.AddRange(apiGotConversations);
        }

        private async void NewConversationTapped(object sender, EventArgs e)
        {
            await Navigation.PushAsync(new CreateConversationPage());
        }
    }
}
