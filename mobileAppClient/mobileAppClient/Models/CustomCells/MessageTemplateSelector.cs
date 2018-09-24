using System;
using mobileAppClient;
using Xamarin.Forms;

namespace mobileAppClient
{
    class MessageTemplateSelector : Xamarin.Forms.DataTemplateSelector
    {
        private readonly DataTemplate incomingDataTemplate;
        private readonly DataTemplate outgoingDataTemplate;

        public MessageTemplateSelector()
        {
            this.incomingDataTemplate = new DataTemplate(typeof(IncomingViewCell));
            this.outgoingDataTemplate = new DataTemplate(typeof(OutgoingViewCell));
        }

        protected override DataTemplate OnSelectTemplate(object item, BindableObject container)
        {
            Message currentMessage = (Message) item;
            if (currentMessage.messageType == MessageType.Incoming)
            {
                return this.incomingDataTemplate;
            } else if (currentMessage.messageType == MessageType.Outgoing)
            {
                return this.outgoingDataTemplate;
            }
            else
            {
                throw new ArgumentException("Message has invalid type");
            }
        }

    }
}