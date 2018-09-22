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

	    private Conversation conversation;
        private CustomObservableCollection<Message> conversationMessages;

	    private Timer t;

		public ConversationPage(Conversation conversationToDisplay, int localId)
		{
			InitializeComponent();
		    conversation = conversationToDisplay;
            CheckIfClinicianAccessing();

		    this.localId = localId;

		    Title = conversation.externalName;

            conversationMessages = new CustomObservableCollection<Message>();

            MessagesListView.ItemsSource = conversationMessages;
		    MessagesListView.ItemTapped += OnMessageTapped;

            populateMessages();

            StartTimer(4000);
        }

	    private void StartTimer(int interval)
	    {
            t = new Timer(timerTick, null, 100, interval);
	    }

	    public async void timerTick(object o)
	    {
	        Tuple<HttpStatusCode, Conversation> refreshedConversation = await new MessagingAPI().GetConversation(localId, conversation.id, isClinicianAccessing);
	        if (refreshedConversation.Item1 == HttpStatusCode.OK)
	        {
	            List<Message> newConversationMessages = refreshedConversation.Item2.messages;
                List<Message> mergedConversations = new List<Message>(conversationMessages.Union(newConversationMessages));
                if (mergedConversations.Count > conversation.messages.Count)
                {
                    conversationMessages.AddRange(newConversationMessages.Except(conversationMessages));
                    if (conversationMessages.Count > 0)
                    {
                        MessagesListView.ScrollTo(conversationMessages.Last(), ScrollToPosition.End, true);
                    }
                }
	        }
	    }

	    protected async override void OnDisappearing()
	    {
	        t.Dispose();
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
            conversationMessages.Clear();
            foreach (Message currentMessage in conversation.messages)
            {
                currentMessage.SetType(localId);
                conversationMessages.Add(currentMessage);
            }

            if (conversationMessages.Count > 0)
            {
                MessagesListView.ScrollTo(conversationMessages.Last(), ScrollToPosition.End, true);
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

            conversationMessages.Add(newMessage);
            MessagesListView.ScrollTo(newMessage, ScrollToPosition.End, true);
	        chatTextInput.Text = "";
	    }
	}
}