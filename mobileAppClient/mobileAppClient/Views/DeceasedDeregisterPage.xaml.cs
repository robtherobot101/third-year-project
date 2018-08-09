using mobileAppClient.odmsAPI;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace mobileAppClient.Views
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class DeceasedDeregisterPage : ContentPage
    {
        private WaitingListItem item;
        private User user;
        private SingleWaitingListItemPage parentWaitingListItemPage;
        public DeceasedDeregisterPage(WaitingListItem item, SingleWaitingListItemPage parentWaitingListItemPage)
        {
            InitializeComponent();
            this.item = item;
            this.parentWaitingListItemPage = parentWaitingListItemPage;
            setupPage();
        }

        /*
         * Handles the back button being clicked, returning the user to 
         * the waiting list items page.
         */
        async void BackButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PopModalAsync();
        }


        public async Task setupPage()
        {
            user = await new UserAPI().getUser(item.userId, ClinicianController.Instance.AuthToken);
            DeathDatePicker.MinimumDate = user.dateOfBirth.ToDateTime();
            DeathDatePicker.MaximumDate = DateTime.Now;
        }

        public async void ConfirmButtonClicked()
        {
            if(user == null)
            {
                await DisplayAlert("Failed to update fetch organ receiver",
                   "Server Error",
                   "OK");
                return;
            }

            DateTime dt = DeathDatePicker.Date.Add(DeathTimePicker.Time);

            user = deregisterAllItems(user, 3);
            user.dateOfDeath = new CustomDateTime(dt);
            HttpStatusCode statusCode = await new UserAPI().UpdateUser(user, ClinicianController.Instance.AuthToken);

            switch (statusCode)
            {
                case HttpStatusCode.Created:
                    await DisplayAlert("",
                    "User details successfully updated",
                    "OK");
                    break;
                case HttpStatusCode.BadRequest:
                    await DisplayAlert("",
                    "User details update failed (400)",
                    "OK");
                    break;
                case HttpStatusCode.ServiceUnavailable:
                    await DisplayAlert("",
                    "Server unavailable, check connection",
                    "OK");
                    break;
                case HttpStatusCode.Unauthorized:
                    await DisplayAlert("",
                    "Unauthorised to modify profile",
                    "OK");
                    break;
                case HttpStatusCode.InternalServerError:
                    await DisplayAlert("",
                    "Server error, please try again (500)",
                    "OK");
                    break;
            }

            await Navigation.PopModalAsync();
            await parentWaitingListItemPage.Navigation.PopModalAsync();
        }

        public User deregisterAllItems(User user, int reasonCode)
        {
            foreach (WaitingListItem item in user.waitingListItems)
            {
                item.organDeregisteredCode = reasonCode;
                item.organDeregisteredDate = new CustomDate(DateTime.Now.Date);
            }
            return user;
        }
    }
}