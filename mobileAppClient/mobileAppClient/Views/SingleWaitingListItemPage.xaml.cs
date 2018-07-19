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
            RegisteredDateEntry.Text = "Registered on " + waitingListItem.OrganRegisteredDate.Day + ", " + waitingListItem.OrganRegisteredDate.Month + ", " + waitingListItem.OrganRegisteredDate.Year;
            DeregisteredDateEntry.Text =
                waitingListItem.OrganDeregisteredDate != null ? 
                                     "Deregistered on " + waitingListItem.OrganDeregisteredDate.Day + ", " + waitingListItem.OrganDeregisteredDate.Month + ", " + waitingListItem.OrganDeregisteredDate.Year
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
