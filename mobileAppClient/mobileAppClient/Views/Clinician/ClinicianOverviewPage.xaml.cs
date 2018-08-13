using CarouselView.FormsPlugin.Abstractions;
using Microsoft.Toolkit.Parsers.Rss;
using mobileAppClient.Models;
using mobileAppClient.Models.CustomObjects;
using mobileAppClient.odmsAPI;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Net;
using System.Windows.Input;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace mobileAppClient.Views
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class ClinicianOverviewPage : ContentPage
	{
        public ClinicianOverviewPage()
        {
            InitializeComponent();
            fillFields();
            this.BindingContext = new
            {
                rss = (new NewsFeed()).rss
            };
		}

        private async void fillFields()
        {
            Clinician currentClinician = ClinicianController.Instance.LoggedInClinician;

            // Database Pane
            UserAPI userAPI = new UserAPI();
            Tuple<HttpStatusCode, int> userCountResult = await userAPI.GetUserCount();
            if (userCountResult.Item1 == HttpStatusCode.OK)
            {
                // Successfully fetched usercount -> apply label
                UserCountLabel.Text = String.Format("There are {0} users currently in the database", userCountResult.Item2);
            } else
            {
                UserCountLabel.Text = String.Format("Failed to get result from database ({0})", userCountResult.Item1);
            }
        }

        public ICommand ClickFeedItem => new Command<string>((url) =>
        {
            Console.WriteLine("Opening url");
            Device.OpenUri(new System.Uri(url));
        });
    }
}