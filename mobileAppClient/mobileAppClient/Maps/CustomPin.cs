using System;
using System.Collections.Generic;
using mobileAppClient.Models;
using Xamarin.Forms.Maps;

namespace mobileAppClient
{
    public class CustomPin : Pin
    {
        public string Url { get; set; }
        public string genderIcon { get; set; }
        public string userPhoto { get; set; }
        public ODMSPinType CustomType { get; set; }
    }
}
