using System;
using System.Collections.Generic;
using Xamarin.Forms.Maps;

namespace mobileAppClient
{
    public class CustomMap : Map
    {
        public bool isBeingModified { get; set; }
        public Dictionary<Position, CustomPin> CustomPins { get; set; }
        public Dictionary<String, CustomPin> HelicopterPins { get; set; }
    }
}
