using mobileAppClient.odmsAPI;
using System;
using System.Net;
using System.Windows.Input;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace mobileAppClient.Views
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class ClinicianOverviewPage : ContentPage
	{
        public ICommand OpenCommand { get; private set; }
        public ClinicianOverviewPage()
        {
            InitializeComponent();
            OpenCommand = new Command<string>(OpenItem);
            fillFields();

            this.BindingContext = new
            {
                rss = new NewsFeed().rss
            };
        }

        private async void fillFields()
        {
            Models.Clinician currentClinician = ClinicianController.Instance.LoggedInClinician;

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

        void OpenItem(string url)
        {
            Console.WriteLine("Opening url");
            Device.OpenUri(new System.Uri(url));
        }
    }
}