using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using mobileAppClient.Models;
using Xamarin.Forms;
using mobileAppClient.Notifications;
using Xamarin.Forms.Xaml;
using mobileAppClient.Models.CustomObjects;
using mobileAppClient.odmsAPI;

namespace mobileAppClient
{
	[XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class ConversationPage : ContentPage
	{
        // Details of local participant in chat
	    private int localId { get; set; }

        private bool isClinicianAccessing { get; set; }

	    public Conversation conversation;

        private CustomObservableCollection<Message> conversationMessages;


		public ConversationPage(Conversation conversationToDisplay, int localId)
		{
			InitializeComponent();
		    conversation = conversationToDisplay;
            CheckIfClinicianAccessing();

		    this.localId = localId;

		    Title = conversation.externalName;
            conversationMessages = conversation.messages;
            conversationMessages.CollectionChanged += ConversationMessages_CollectionChanged;

            MessagesListView.ItemsSource = conversationMessages;

            //MessagesListView.ScrollToLast();
            VSAppCenter.seConversationController(this);
        }

        private void ConversationMessages_CollectionChanged(object sender, NotifyCollectionChangedEventArgs e)
        {
            //MessagesListView.ScrollToLast();
        }

        protected override void OnDisappearing()
        {  
            VSAppCenter.seConversationController(null);
        }


        /// <summary>
        /// Checks whether the clinician is viewing this page, important for fetching the correct profiles of participants
        /// </summary>
        private void CheckIfClinicianAccessing()
	    {
	        if (ClinicianController.Instance.isLoggedIn())
	        {
	            isClinicianAccessing = true;
	        }
	    }

        /// <summary>
        /// Handles the sending of a message
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
	    private async void Handle_SendMessage(object sender, EventArgs e)
        {
            if (string.IsNullOrEmpty(chatTextInput.Text))
            {
                return;
            }

            string messageContentsToSend = InputValidation.Trim(chatTextInput.Text);

            HttpStatusCode messageStatus = await new MessagingAPI().SendMessage(localId, conversation.id,
                messageContentsToSend, isClinicianAccessing);

            if (messageStatus != HttpStatusCode.Created)
            {
                await DisplayAlert("", "Failed to send message", "OK");
                return;
            }

            Message newMessage = new Message
            {
                text = messageContentsToSend,
                messageType = MessageType.Outgoing,
                timestamp = new CustomDateTime(DateTime.Now)
	        };

            conversationMessages.Insert(0, newMessage);
	        chatTextInput.Text = "";
            chatTextInput.Keyboard = null;
        }
	}
}