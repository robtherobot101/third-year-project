using System;
using System.Collections.Generic;
using Xamarin.Forms.Maps;

namespace mobileAppClient
{
    public class CustomMap : Map
    {
        public Dictionary<Position, CustomPin> CustomPins { get; set; }
        public CustomCircle Circle { get; set; }
        public Dictionary<String, CustomPin> HelicopterPins { get; set; }
    }
}
