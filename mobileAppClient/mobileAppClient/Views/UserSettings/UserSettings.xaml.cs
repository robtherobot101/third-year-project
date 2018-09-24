using System;
using System.IO;
using System.Net;
using mobileAppClient.odmsAPI;
using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class UserSettings : ContentPage
    {
        
        public UserSettings()
        {
            InitializeComponent();

        }

        async void Handle_Tapped(object sender, EventArgs e)
        {
            await Navigation.PushAsync(new PhotoSettingsPage());
        }


    }
}
