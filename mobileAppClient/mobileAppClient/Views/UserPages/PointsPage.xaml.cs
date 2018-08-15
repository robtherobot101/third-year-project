using mobileAppClient.Models.CustomObjects;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
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
	    private List<Image> people;

		public PointsPage ()
		{
			InitializeComponent ();
            recommendationsList = new CustomObservableCollection<string>();
            RecommendationsList.ItemsSource = recommendationsList;
		    people = new List<Image>();

//		    for (int i = 1; i < 36; i++)
//		    {
//		        
//
//                people.Add(image);
//		    }

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

            savedPeopleGrid.Children.Clear();
            helpedPeopleGrid.Children.Clear();

            Random rnd = new Random();
            for (int i = 0; i < savedLives; i++)
            {
                int humanToChoose = rnd.Next(1, 35);
                var image = new Image { Source = $"human{humanToChoose}.png" };
                image.Scale = 0.5;

                savedPeopleGrid.Children.Add(image);
            }

            for (int i = 0; i < helpedLives; i++)
            {
                int humanToChoose = rnd.Next(1, 35);
                var image = new Image { Source = $"human{humanToChoose}.png" };
                image.Scale = 0.5;

                helpedPeopleGrid.Children.Add(image);
            }

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

            savedLivesText.Text = String.Format("You could save {0} lives", calculateSavedLives());
            helpedLivesText.Text = String.Format("and you could also help {0} lives", calculateHelpedLives());
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