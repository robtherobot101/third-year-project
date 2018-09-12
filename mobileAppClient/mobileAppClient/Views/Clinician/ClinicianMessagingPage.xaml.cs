using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class ClinicianMessagingPage : ContentPage
    {
        public ClinicianMessagingPage()
        {
            InitializeComponent();
            DependencyService.Get<CustomMessagingInterface>().CreateMessagingPage();
        }
    }
}
