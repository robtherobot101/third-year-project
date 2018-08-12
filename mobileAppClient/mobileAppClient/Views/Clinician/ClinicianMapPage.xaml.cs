using System;
using System.Collections.Generic;

using Xamarin.Forms;
using Xamarin.Forms.Maps;
using Plugin.Geolocator;
using System.Threading.Tasks;

namespace mobileAppClient
{
    public partial class ClinicianMapPage : ContentPage
    {
        public ClinicianMapPage()
        {
            //getCurrentLocation();
            var map = new Map(
            MapSpan.FromCenterAndRadius(
                    new Position(37, -122), Distance.FromMiles(1)))
            {
                IsShowingUser = true,
                HeightRequest = 100,
                WidthRequest = 960,
                VerticalOptions = LayoutOptions.FillAndExpand
            };

            var pin = new Pin()
            {
                Position = new Position(37, -122),
                Label = "Some Pin!"
            };
            map.Pins.Add(pin);

            var pin1 = new Pin()
            {
                Position = new Position(38, -122),
                Label = "Some Pin!"
            };
            pin1.Address = "44 Creyke Road";
            pin1.Type = PinType.SearchResult;
            map.Pins.Add(pin1);


            map.MapType = MapType.Street;
            var stack = new StackLayout { Spacing = 0 };
            stack.Children.Add(map);
            Content = stack;
        }

        public async void getCurrentLocation() {
            var locator = CrossGeolocator.Current;
            locator.DesiredAccuracy = 50;
            var position = await locator.GetPositionAsync();
        }
    }
}
