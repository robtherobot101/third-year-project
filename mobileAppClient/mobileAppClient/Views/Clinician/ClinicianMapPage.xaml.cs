using System;
using System.Collections.Generic;

using Xamarin.Forms;
using Xamarin.Forms.Maps;
using System.Threading.Tasks;
using mobileAppClient.odmsAPI;
using System.Net;

namespace mobileAppClient
{
    public partial class ClinicianMapPage : ContentPage
    {
        List<CustomMapObject> organs;
        CustomMap customMap;

        public ClinicianMapPage()
        {

            customMap = new CustomMap
            {
                MapType = MapType.Street,
                WidthRequest = 100,
                HeightRequest = 100
            };


            customMap.CustomPins = new List<CustomPin> { };


            customMap.MoveToRegion(MapSpan.FromCenterAndRadius(
              new Position(37.79752, -122.40183), Distance.FromMiles(2000.0)));
            


            var stack = new StackLayout { Spacing = 0 };
            stack.Children.Add(customMap);
            Content = stack;
        }

        /// <summary>
        /// Activated whenever focus is on this page
        /// </summary>
        protected override async void OnAppearing()
        {
            //Get all the organs from the database
            //Create pins for every organ
            UserAPI userAPI = new UserAPI();
            Tuple<HttpStatusCode, List<CustomMapObject>> tuple = await userAPI.GetOrgansForMap();
            switch (tuple.Item1)
            {
                case HttpStatusCode.OK:
                    Console.WriteLine("Organ Map Objects Successfully received");
                    organs = tuple.Item2;

                    Geocoder geocoder = new Geocoder();
                    foreach (CustomMapObject item in organs)
                    {
                        if (item.regionOfDeath != null)
                        {

                            IEnumerable<Position> position = await geocoder.GetPositionsForAddressAsync(item.regionOfDeath);
                            Position organPosition = new Position();
                            using (var sequenceEnum = position.GetEnumerator())
                            {
                                while (sequenceEnum.MoveNext())
                                {
                                    organPosition = sequenceEnum.Current;
                                }
                            }
                            string imageString = "";
                            switch(item.name) {
                                case ("pancreas"):
                                    imageString = "pancreas_icon.png";
                                    break;
                                case ("heart"):
                                    imageString = "heart_icon.png";
                                    break;
                                case ("liver"):
                                    imageString = "liver_icon.png";
                                    break;
                                case ("connective-tissue"):
                                    imageString = "tissue_icon.png";
                                    break;
                                case ("bone-marrow"):
                                    imageString = "bone_icon.png";
                                    break;
                                case ("skin"):
                                    imageString = "skin_icon.png";
                                    break;
                                case ("lung"):
                                    imageString = "lung_icon.png";
                                    break;
                                case ("cornea"):
                                    imageString = "eye_icon.png";
                                    break;
                                case ("kidney"):
                                    imageString = "kidney_icon.png";
                                    break;
                                case ("intestine"):
                                    imageString = "intestine_icon.png";
                                    break;
                            }

                            var pin = new CustomPin
                            {
                                Type = PinType.Place,
                                Position = organPosition,
                                Label = item.name[0].ToString().ToUpper() + item.name.Substring(1),
                                Address = item.regionOfDeath,
                                Id = "Xamarin",
                                Url = "http://xamarin.com/about/",
                                image = imageString
                            };
                            customMap.Pins.Add(pin);
                            customMap.CustomPins.Add(pin);


                        }

                    }

                    break;
                case HttpStatusCode.ServiceUnavailable:
                    await DisplayAlert("",
                    "Server unavailable, check connection",
                    "OK");
                    break;
                case HttpStatusCode.InternalServerError:
                    await DisplayAlert("",
                    "Server error retrieving organs, please try again (500)",
                    "OK");
                    break;
            }


        }


    }
}
