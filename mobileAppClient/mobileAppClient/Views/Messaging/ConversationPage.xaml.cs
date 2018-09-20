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
	public partial class ConversationPage : ContentPage
	{
        // Id of local participant in chat
	    private int localId { get; set; }

	    // Id of the other participant in chat
	    private int externalId { get; set; }

	    private Conversation conversation;
        private CustomObservableCollection<Message> conversationMessages;

		public ConversationPage(Conversation conversationToDisplay)
		{
			InitializeComponent ();
		    conversation = conversationToDisplay;
            Title = "Test Convo";

            externalId = conversation.members.Except(new List<int>(localId)).First();

            conversationMessages = new CustomObservableCollection<Message>();
            MessagesListView.ItemsSource = conversationMessages;
		    MessagesListView.ItemTapped += OnMessageTapped;

            populateMessages();
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

        private void populateMessages()
        {
            foreach (Message currentMessage in conversation.messages)
            {
                currentMessage.SetType(localId);
                conversationMessages.Add(currentMessage);
            }
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

            // TODO API CALL TO SEND THE MESSAGE

            Message newMessage = new Message
            {
                text = InputValidation.Trim(chatTextInput.Text),
                messageType = MessageType.Outgoing,
                timestamp = new CustomDateTime(DateTime.Now)
	        };

            conversationMessages.Add(newMessage);
            MessagesListView.ScrollTo(newMessage, ScrollToPosition.End, true);
	        chatTextInput.Text = "";

	    }
	}
}