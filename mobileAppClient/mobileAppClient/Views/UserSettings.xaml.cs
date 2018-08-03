using System;
using System.Collections.Generic;

using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class UserSettings : ContentPage
    {
        public UserSettings()
        {
            InitializeComponent();
        }

        void Handle_ChangeProfilePhotoPressed(object sender, System.EventArgs e)
        {
            Console.WriteLine("Gday");
        }
    }
}
