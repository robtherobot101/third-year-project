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

            addTestMessages();
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
        }
	}
}