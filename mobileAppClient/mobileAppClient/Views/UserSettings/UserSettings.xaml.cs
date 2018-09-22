using System;
using System.IO;
using System.Net;
using mobileAppClient.odmsAPI;
using mobileAppClient.Views.UserSettings;
using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class UserSettings : ContentPage
    {
        
        public UserSettings()
        {
            InitializeComponent();

        }

        async void Handle_PhotoSettingsTapped(object sender, System.EventArgs e)
        {
            await Navigation.PushAsync(new PhotoSettingsPage());
        }

        async void Handle_FacebookSettingsTapped(object sender, System.EventArgs e)
        {
            //Do a thing
        }


        async void Handle_GoogleSettingsTapped(object sender, System.EventArgs e)
        {
            //Do a thing
        }

        async void Handle_AccountSettingsTapped(object sender, System.EventArgs e)
        {
            await Navigation.PushAsync(new AccountSettingsPage());
        }
    }
}
