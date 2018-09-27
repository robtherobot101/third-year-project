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
using mobileAppClient.Views.Messaging;

namespace mobileAppClient
{
    /*
     * Page which handles a single conversation
     */
	[XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class ConversationPage : ContentPage
	{
        // Details of local participant in chat
	    private int localId { get; set; }

        private bool isClinicianAccessing { get; set; }

	    public Conversation conversation;

        public CustomObservableCollection<Message> conversationMessages;


		public ConversationPage(Conversation conversationToDisplay, int localId)
		{
			InitializeComponent();
		    conversation = conversationToDisplay;
            CheckIfClinicianAccessing();

		    this.localId = localId;

		    Title = conversation.externalName;
            conversationMessages = new CustomObservableCollection<Message>(conversationToDisplay.messages.Reverse());
            conversationMessages.CollectionChanged += ConversationMessages_CollectionChanged;

            MessagesListView.ItemsSource = conversationMessages;

            //MessagesListView.ScrollToLast();
            VSAppCenter.setConversationController(this);
        }


        private async void ConversationMessages_CollectionChanged(object sender, NotifyCollectionChangedEventArgs e)
        {
            //await ScrollViewContainer.ScrollToAsync(0, 100, true);
        }

        /*
         * Whenever the page appears, the page is scrolled to the bottom
         */
        protected async override void OnAppearing()
        {
            base.OnAppearing();
            await ScrollViewContainer.ScrollToAsync(0,100,true);

        }

        /*
         * When the page dissapears, the push notification conversation controller
         * is disabled
         */
        protected override void OnDisappearing()
        {  
            VSAppCenter.setConversationController(null);
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

        /*
         * If a message is tapped, the keyboard is closed
         */
        void Handle_ItemTapped(object sender, Xamarin.Forms.ItemTappedEventArgs e)
        {
            if(Device.RuntimePlatform == Device.iOS) {
                DependencyService.Get<IForceKeyboardDismissalService>().DismissKeyboard();
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