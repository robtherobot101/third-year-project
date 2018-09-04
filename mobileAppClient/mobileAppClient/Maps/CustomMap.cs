using System;
using System.Collections.Generic;
using Xamarin.Forms.Maps;

namespace mobileAppClient
{
    public class CustomMap : Map
    {
        public Dictionary<Position, CustomPin> CustomPins { get; set; }
    }
}
