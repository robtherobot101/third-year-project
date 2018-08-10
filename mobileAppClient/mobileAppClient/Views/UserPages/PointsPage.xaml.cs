using mobileAppClient.Models.CustomObjects;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace mobileAppClient
{
	[XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class PointsPage : ContentPage
	{
        private CustomObservableCollection<String> recommendationsList;
        private int savedLives;
        private int helpedLives;

		public PointsPage ()
		{
			InitializeComponent ();
            recommendationsList = new CustomObservableCollection<string>();
            RecommendationsList.ItemsSource = recommendationsList;
            

            savedLivesText.Text = String.Format("You could save {0} lives", calculateSavedLives());
            helpedLivesText.Text = String.Format("and you could also help {0} lives", calculateHelpedLives());
        }

        protected override void OnAppearing()
        {
            refreshDetails();
        }

        private void refreshDetails()
        {
            bool allOrgansDonated = false;
            savedLives = calculateSavedLives();
            helpedLives = calculateHelpedLives();
            
            if (savedLives + helpedLives == 11)
            {
                allOrgansDonated = true;
            }

            recommendationsList.Clear();
            
            if (!allOrgansDonated)
            {
                recommendationsList.Add("You could donate more organs");
            }
            
            if (UserController.Instance.photoObject == null)
            {
                recommendationsList.Add("You could add a profile photo");
            }
            
            recommendationsList.Add("You could add in more details about yourself");
        }

        /// <summary>
        /// Returns the estimated number of saved lives based on organ donations
        /// </summary>
        /// <returns></returns>
        private int calculateSavedLives()
        {
            int savedLives = 0;
            foreach (string item in UserController.Instance.LoggedInUser.organs)
            {
                switch (item)
                {
                    case "LIVER":
                        savedLives++;
                        break;
                    case "KIDNEY":
                        savedLives++;
                        break;
                    case "PANCREAS":
                        savedLives++;
                        break;
                    case "HEART":
                        savedLives++;
                        break;
                    case "LUNG":
                        savedLives++;
                        break;
                    case "INTESTINE":
                        savedLives++;
                        break;
                }
            }
            return savedLives;
        }

        /// <summary>
        /// Returns the estimated number of saved lives based on organ donations
        /// </summary>
        /// <returns></returns>
        private int calculateHelpedLives()
        {
            int helpedLives = 0;
            foreach (string item in UserController.Instance.LoggedInUser.organs)
            {
                switch (item)
                {
                    case "CORNEA":
                        helpedLives++;
                        break;
                    case "EAR":
                        helpedLives++;
                        break;
                    case "SKIN":
                        helpedLives++;
                        break;
                    case "BONE":
                        helpedLives++;
                        break;
                    case "TISSUE":
                        helpedLives++;
                        break;
                }
            }
            return helpedLives;
        }

        async private void RecommendationsList_ItemTapped(object sender, ItemTappedEventArgs e)
        {
            if (e.Item == null)
            {
                return;
            }

            //deselect item
            ((ListView)sender).SelectedItem = null;

            if(e.Item == "You could donate more organs")
            {
                await Navigation.PushAsync(new OrgansPage());
            }
            else if(e.Item == "You could add a profile photo")
            {
                await Navigation.PushAsync(new PhotoSettingsPage());
            }
            else if (e.Item == "You could add in more details about yourself")
            {
                await Navigation.PushAsync(new AttributesPage());
            }
        }
    }
}