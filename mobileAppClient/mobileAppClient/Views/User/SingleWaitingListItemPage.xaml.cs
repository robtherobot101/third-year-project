using System;
using System.Collections.Generic;

using Xamarin.Forms;
using System.Globalization;
using mobileAppClient.odmsAPI;
using mobileAppClient.Views;
using System.Net;
using System.Net.Http;
using mobileAppClient.Models;
using System.Threading.Tasks;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the single waiting list item page for 
     * showing the details of a single waiting list item of a user.
     */ 
    public partial class SingleWaitingListItemPage : ContentPage
    {
        DateTimeFormatInfo dateTimeFormat = new DateTimeFormatInfo();
        WaitingListItem item;
        User user;
        /*
         * Constructor which initialises the entries of the waiting list items listview.
         */ 
        public SingleWaitingListItemPage(WaitingListItem waitingListItem, Boolean showDeregisterButton)
        {
            InitializeComponent();
            Title = "Waiting list item";
            this.item = waitingListItem;
            OrganTypeEntry.Text = OrganExtensions.ToString(waitingListItem.organType);
            RegisteredDateEntry.Text = waitingListItem.organRegisteredDate.day + " of " + dateTimeFormat.GetAbbreviatedMonthName(waitingListItem.organRegisteredDate.month) + ", " + waitingListItem.organRegisteredDate.year;
            DeregisteredDateEntry.Text =
                waitingListItem.organDeregisteredDate != null ?
                                     waitingListItem.organDeregisteredDate.day + " of " + dateTimeFormat.GetAbbreviatedMonthName(waitingListItem.organDeregisteredDate.month) + ", " + waitingListItem.organDeregisteredDate.year
                                     : "N/A";
            //DeregisterCodeEntry.Text =
            //waitingListItem.OrganDeregisteredCode != 0 ? waitingListItem.OrganDeregisteredCode.ToString() : "N/A";

            DeregisterButton.IsVisible = showDeregisterButton;

            IDEntry.Text = waitingListItem.id.ToString();


        }

        protected override async void OnAppearing()
        {
            if (ClinicianController.Instance.isLoggedIn())
            {
                user = await new UserAPI().getUser(item.userId, ClinicianController.Instance.AuthToken);
                UserName.Text = user.FullName.ToString();
            }
            else
            {
                user = await new UserAPI().getUser(item.userId, UserController.Instance.AuthToken);
                UserName.Text = user.FullName.ToString();
            }
        }

        /*
         * Handles the back button being clicked, returning the user to 
         * the waiting list items page.
         */ 
        async void BackButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PopAsync();
        }

        /*
         * Shows a dialog which allows a user to select a reason for de-registering
         * the selected WaitingListItem.
         */
        public async void showDeregisteringOptions(WaitingListItem item)
        {
            var action = await DisplayActionSheet("Select the reason code: ", "Cancel", "", "1: Error while registering", "2: Disease Cured", "3: Receiver Deceased", "4: Successful Transplant");
            if (action == "1: Error while registering")
            {
                await deregister(item, 1);
                await Navigation.PopAsync();
                MessagingCenter.Send<ContentPage>(this, "REFRESH_WAITING_LIST_ITEMS");
            }
            else if (action == "2: Disease Cured")
            {
                try
                {
                    User user = await new UserAPI().getUser(item.userId, ClinicianController.Instance.AuthToken);
                    if (user != null && user.currentDiseases.Count > 0)
                    {
                        await Navigation.PushModalAsync(new DiseaseCuredDeregisterPage(item, this));
                    }
                    else
                    {
                        await DisplayAlert("Alert",
                            "There are no un-cured diseases for this user",
                            "OK");
                    }
                } catch (HttpRequestException)
                {
                    await DisplayAlert("Connection Error",
                                       "Failed to reach the server",
                                       "OK");
                }
            }
            else if (action == "3: Receiver Deceased")
            {
                await Navigation.PushModalAsync(new DeceasedDeregisterPage(item, this));
            }
            else if (action == "4: Successful Transplant")
            {
                await deregister(item, 1);
                await Navigation.PopAsync();
                MessagingCenter.Send<ContentPage>(this, "REFRESH_WAITING_LIST_ITEMS");
            }
        }

        /*
         * Shows a dialog which allows a user to select a reason for de-registering
         * the selected WaitingListItem.
         */
        public void DeregisterButtonClicked(object sender, EventArgs args)
        {
            showDeregisteringOptions(item);
        }

        /*
         * De-registers the given WaitingListItem and saves the changes
         * to the server.
         */
        public async Task deregister(WaitingListItem item, int reasonCode)
        {


            try
            {
                item.organDeregisteredDate = new CustomDate(DateTime.Now);
                item.organDeregisteredCode = reasonCode;
                //
                HttpStatusCode code = await new TransplantListAPI().updateItem(item);
                if (code != HttpStatusCode.Created)
                {
                    await DisplayAlert(
                            "Failed to de-register item",
                            "Server error",
                            "OK");
                }
            }
            catch (HttpRequestException)
            {
                await DisplayAlert("Connection Error",
                   "Failed to reach the server",
                   "OK");
            }
        }
    }
}
