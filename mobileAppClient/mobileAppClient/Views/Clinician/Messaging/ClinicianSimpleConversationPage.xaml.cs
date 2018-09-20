using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using mobileAppClient.Models;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using mobileAppClient.Models.CustomObjects;

namespace mobileAppClient
{
	[XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class ClinicianSimpleConversationPage : ContentPage
	{
	    private Conversation conversationKey;
        private CustomObservableCollection<Message> conversationMessages;

		public ClinicianSimpleConversationPage(Conversation conversationToDisplay)
		{
			InitializeComponent ();
		    conversationKey = conversationToDisplay;
            Title = conversationKey.conversationTitle;

            conversationMessages = new CustomObservableCollection<Message>();
            MessagesListView.ItemsSource = conversationMessages;
		    MessagesListView.ItemTapped += OnMessageTapped;

            addTestMessages();
        }

        /// <summary>
        /// When a message is tapped fire this event
        /// Just deselects the item immediately, giving the appearance of not being tappable
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        public void OnMessageTapped(object sender, ItemTappedEventArgs e) {
	        if (e.Item == null) return;
	        ((ListView)sender).SelectedItem = null;
	    }

        private void addTestMessages()
        {
            conversationMessages.Add(new Message
            {
                Text = "Hello Buzz!",
                Type = MessageType.Incoming,
                MessagDateTime = DateTime.Now
            });

            conversationMessages.Add(new Message
            {
                Text = "Howdy howdy!",
                Type = MessageType.Outgoing,
                MessagDateTime = DateTime.Now
            });

            conversationMessages.Add(new Message
            {
                Text = "Whats cooking good lookin ;)",
                Type = MessageType.Incoming,
                MessagDateTime = DateTime.Now
            });
        }

        /// <summary>
        /// Handles the sending of a message
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
	    private void Handle_SendMessage(object sender, EventArgs e)
	    {
	        if (string.IsNullOrEmpty(chatTextInput.Text))
	        {
	            return;
	        }

	        Message newMessage = new Message
	        {
	            Text = InputValidation.Trim(chatTextInput.Text),
	            Type = MessageType.Outgoing,
	            MessagDateTime = DateTime.Now
	        };

            conversationMessages.Add(newMessage);
            MessagesListView.ScrollTo(newMessage, ScrollToPosition.End, true);
	        chatTextInput.Text = "";

	    }
	}
}