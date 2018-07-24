using System;
using System.Collections.Generic;

using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class SingleWaitingListItemPage : ContentPage
    {
        public SingleWaitingListItemPage(WaitingListItem waitingListItem)
        {
            InitializeComponent();
            OrganTypeEntry.Text = waitingListItem.OrganType;
            RegisteredDateEntry.Text = waitingListItem.OrganRegisteredDate.day + ", " + waitingListItem.OrganRegisteredDate.month + ", " + waitingListItem.OrganRegisteredDate.year;
            DeregisteredDateEntry.Text =
                waitingListItem.OrganDeregisteredDate != null ? 
                                     waitingListItem.OrganDeregisteredDate.day + ", " + waitingListItem.OrganDeregisteredDate.month + ", " + waitingListItem.OrganDeregisteredDate.year
                                     : "N/A";
            DeregisterCodeEntry.Text =
                waitingListItem.OrganDeregisteredCode != 0 ? waitingListItem.OrganDeregisteredCode.ToString() : "N/A";

            IDEntry.Text = waitingListItem.Id.ToString();
        }

        async void BackButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PopModalAsync();
        }
    }
}
