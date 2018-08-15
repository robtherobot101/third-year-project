using mobileAppClient.odmsAPI;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace mobileAppClient.Views
{
	[XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class DiseaseCuredDeregisterPage : ContentPage
	{
        public WaitingListItem item;
        public SingleWaitingListItemPage parentWaitingListItemPage;
        DateTimeFormatInfo dateTimeFormat = new DateTimeFormatInfo();

        /*
         * Class which handles the de-registering of one waitinglist item
         * and curing one disease.
         */
        public DiseaseCuredDeregisterPage(WaitingListItem item, SingleWaitingListItemPage parentWaitingListItemPage)
		{
            this.item = item;
            this.parentWaitingListItemPage = parentWaitingListItemPage;
			InitializeComponent();
            setupDiseaseList();
		}

        /*
         * Adds all the current, curable diseases to the disease ListView
         */
        public async void setupDiseaseList()
        {
            try
            {
                User user = await new UserAPI().getUser(item.userId, ClinicianController.Instance.AuthToken);
                if (user != null)
                {
                    List<Disease> diseases = user.currentDiseases;
                    DiseasesList.ItemsSource = diseases;
                }
            } catch (HttpRequestException e)
            {
                await DisplayAlert("Connection Error",
                   "Failed to reach the server",
                   "OK");
            }
        }

        /*
         * Cures the selected disease, de-registers the waiting list item
         * and returns to the previous page.
         * If no disease is selected, nothing happens
         * 
         * This is called whenever the confirm button is pressed.
         */ 
        public async void ConfirmButtonClicked(object sender, EventArgs args)
        {
            Disease disease = (Disease)DiseasesList.SelectedItem;
            if (disease == null)
            {
                return;
            }

            try
            {
                User user = await new UserAPI().getUser(item.userId, ClinicianController.Instance.AuthToken);
                if (user == null)
                {
                    await DisplayAlert("Failed to update fetch organ receiver",
                                       "Server Error",
                                       "OK");
                    return;
                }

                user = cureDisease(user, disease);
                user = deregister(user, item, 2);
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
                Console.WriteLine("About to remove modal");
                await Navigation.PopModalAsync();
                await parentWaitingListItemPage.Navigation.PopModalAsync();
            }
            catch (HttpRequestException e)
            {
                await DisplayAlert("Connection Error",
                                   "Failed to reach the server",
                                   "OK");

            }
        }

        /*
         * Handles the back button being clicked, returning the user to 
         * the waiting list items page.
         */
        async void BackButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PopModalAsync();
        }


        public User cureDisease(User user, Disease disease)
        {
            foreach(Disease d in user.currentDiseases)
            {
                if(d.name == disease.name && d.diagnosisDate == disease.diagnosisDate)
                {
                    d.isCured = true;
                }
            }
            return user;
        }

        /*
         * De-registers the given waiting list item in the given
         * user profile.
         */
        public User deregister(User user, WaitingListItem item, int reasonCode)
        {
            foreach (WaitingListItem i in user.waitingListItems)
            {
                if (i.id == item.id)
                {
                    i.organDeregisteredDate = new CustomDate(DateTime.Now);
                    i.organDeregisteredCode = reasonCode;
                }
            }
            return user;
        }
    }
}