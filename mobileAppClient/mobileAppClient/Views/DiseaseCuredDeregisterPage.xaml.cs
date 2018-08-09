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
	public partial class DiseaseCuredDeregisterPage : ContentPage
	{
        public WaitingListItem item;
        public SingleWaitingListItemPage parentWaitingListItemPage;
		public DiseaseCuredDeregisterPage(WaitingListItem item, SingleWaitingListItemPage parentWaitingListItemPage)
		{
            this.item = item;
            this.parentWaitingListItemPage = parentWaitingListItemPage;
			InitializeComponent();
            setupDiseaseList();
		}

        public async void setupDiseaseList()
        {
            User user = await new UserAPI().getUser(item.userId, ClinicianController.Instance.AuthToken);
            if(user != null)
            {
                List<Disease> diseases = user.currentDiseases;
                foreach(Disease item in diseases)
                {
                    item.CellText = item.name;
                    item.CellColour = Color.Blue;
                }
                DiseasesList.ItemsSource = diseases;
            }
        }

        public async void ConfirmButtonClicked()
        {
            Disease disease = (Disease)DiseasesList.SelectedItem;
            if (disease == null)
            {
                return;
            }

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
                if(d.id == disease.id)
                {
                    d.isCured = true;
                }
            }
            return user;
        }

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