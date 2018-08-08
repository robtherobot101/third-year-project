using CarouselView.FormsPlugin.Abstractions;
using Microsoft.Toolkit.Parsers.Rss;
using mobileAppClient.Models;
using mobileAppClient.odmsAPI;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Net;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace mobileAppClient.Views
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class ClinicianOverviewPage : ContentPage
	{
        public CarouselViewControl carousel;
        public ObservableCollection<RssSchema> rss { get; set; }
        public int rssPosition = 0;

		public ClinicianOverviewPage ()
		{
			InitializeComponent();
            fillFields();
            rss = new ObservableCollection<RssSchema>();
            fillFeed();
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

        /**
         * A temporary class to define an RSS item
         */ 
        public class RssItem
        {
            public string Title { get; set; }
            public string Content { get; set; }
        }

        /**
         * Fill the image carousel with images and captions
         */
        private async void fillFeed()
        {
            var rssString = await ServerConfig.Instance.client.GetStringAsync("http://qwantz.com/rssfeed.php");
            var rssParser = new RssParser();

            foreach (var element in rss)
            {
                Console.WriteLine($"Title: {element.Title}");
                Console.WriteLine($"Summary: {element.Summary}");
                rss.Add(element);
            }
        }
	}
}