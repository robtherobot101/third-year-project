using System;
using System.Collections.Generic;

using Xamarin.Forms;
using Xamarin.Forms.Maps;
using System.Threading.Tasks;
using mobileAppClient.odmsAPI;
using System.Net;
using System.IO;
using System.Linq;

namespace mobileAppClient
{
    public partial class ClinicianMapPage : ContentPage
    {
        List<CustomMapObject> users;
        CustomMap customMap;

        public ClinicianMapPage()
        {


        }

        public async void displayUserDialog(string organString, string id) {
            string[] organList = organString.Split(',');
            id = organList[organList.Length - 1];
            organList = organList.Take(organList.Length - 1).ToArray();
            List<string> finalList = new List<string>();
            string final;
            foreach(string item in organList) {
                final = item.Replace("_icon.png", "");
                finalList.Add("- " + final + "\n");
            }
            string listString = String.Join("", finalList);
            
            var answer = await DisplayAlert("Display User?", "This user is currently donating: \n" + listString + "Would you like to view their profile?", "Yes", "No");
            if(answer == true) {
                UserAPI userAPI = new UserAPI();
                Tuple<HttpStatusCode, User> userTuple = await userAPI.GetSingleUser(id);
                switch (userTuple.Item1)
                {
                    case HttpStatusCode.OK:
                        UserController.Instance.LoggedInUser = userTuple.Item2;

                        MainPage mainPage = new MainPage(true);
                        mainPage.Title = String.Format("User Viewer: {0}", userTuple.Item2.FullName);

                        await Navigation.PushAsync(mainPage);
                        break;
                    case HttpStatusCode.ServiceUnavailable:
                        await DisplayAlert("",
                        "Server unavailable, check connection",
                        "OK");
                        break;
                    case HttpStatusCode.Unauthorized:
                        await DisplayAlert("",
                        "Unauthorised to get profile",
                        "OK");
                        break;
                    case HttpStatusCode.InternalServerError:
                        await DisplayAlert("",
                        "Server error, please try again (500)",
                        "OK");
                        break;
                }
            }
        }

        /// <summary>
        /// Activated whenever focus is on this page
        /// </summary>
        protected override async void OnAppearing()
        {

            customMap = new CustomMap
            {
                MapType = MapType.Street,
                WidthRequest = 100,
                HeightRequest = 100
            };


            customMap.CustomPins = new List<CustomPin> { };


            customMap.MoveToRegion(MapSpan.FromCenterAndRadius(
                new Position(-41.626217, 172.361873), Distance.FromMiles(350.0)));


            var stack = new StackLayout { Spacing = 0 };
            stack.Children.Add(customMap);
            Content = stack;
            Title = "Available Donor Map";

            //Get all the organs from the database
            //Create pins for every organ
            UserAPI userAPI = new UserAPI();
            Tuple<HttpStatusCode, List<CustomMapObject>> tuple = await userAPI.GetOrgansForMap();
            switch (tuple.Item1)
            {
                case HttpStatusCode.OK:
                    Console.WriteLine("Organ Map Objects Successfully received");
                    users = tuple.Item2;

                    Geocoder geocoder = new Geocoder();

                    foreach (CustomMapObject user in users)
                    {
                        if(user.organs.Count == 0) {
                            continue;
                        }

                        
                        IEnumerable<Position> position = await geocoder.GetPositionsForAddressAsync(user.cityOfDeath + ", " + user.regionOfDeath + ", " + user.countryOfDeath);
                        Position organPosition = new Position();
                        using (var sequenceEnum = position.GetEnumerator())
                        {
                            while (sequenceEnum.MoveNext())
                            {
                                organPosition = sequenceEnum.Current;
                            }
                        }
                        Random rnd = new Random();
                        double randomNumberLongitude = rnd.NextDouble() / 50.00;
                        double randomNumberLatitude = rnd.NextDouble() / 50.00;
                        Position finalPosition = new Position(organPosition.Latitude + randomNumberLatitude, organPosition.Longitude + randomNumberLongitude);

                        List<string> organIcons = new List<string>();

                        foreach(string organ in user.organs) {

                            string imageString = "";
                            switch (organ.ToLower())
                            {
                                case ("pancreas"):
                                    imageString = "pancreas_icon.png";
                                    break;
                                case ("heart"):
                                    imageString = "heart_icon.png";
                                    break;
                                case ("liver"):
                                    imageString = "liver_icon.png";
                                    break;
                                case ("tissue"):
                                    imageString = "tissue_icon.png";
                                    break;
                                case ("bone"):
                                    imageString = "bone_icon.png";
                                    break;
                                case ("skin"):
                                    imageString = "skin_icon.png";
                                    break;
                                case ("lung"):
                                    imageString = "lungs_icon.png";
                                    break;
                                case ("cornea"):
                                    imageString = "eye_icon.png";
                                    break;
                                case ("kidney"):
                                    imageString = "kidney_icon.png";
                                    break;
                                case ("intestine"):
                                    imageString = "intestines_icon.png";
                                    break;
                                case ("ear"):
                                    imageString = "ear_icon.png";
                                    break;
                            }
                            organIcons.Add(imageString);
                        }

                        //Used so that we can get the id when we want to view the user
                        organIcons.Add(user.id.ToString());

                        //SET GENDER ICON
                        //Randomize man or woman photo
                        //If other then set to a pin
                        //If none then also set to a pin

                        string genderIcon = "";
                        switch(user.gender) {
                            case("Male"):
                                int number = rnd.Next(1, 15);
                                genderIcon = "man" + number + ".png";
                                break;
                            case("Female"):
                                number = rnd.Next(1, 12);
                                genderIcon = "woman" + number + ".png";
                                break;
                            case("Other"):
                                genderIcon = "other.png";
                                break;
                            default:
                                genderIcon = "other.png";
                                break;
                        }

                        //SET PROFILE PHOTO
                        //Get profile photo from users uploaded photo (if there is one)

                        string profilePhoto = "";
                        Tuple<HttpStatusCode, string> photoResponse = await userAPI.GetUserPhotoForMapObjects(user.id);
                        if(photoResponse.Item1 != HttpStatusCode.OK) {
                            Console.WriteLine("Failed to retrieve profile photo or no profile photo found.");
                            Byte[] bytes;
                            if(Device.RuntimePlatform == Device.Android) {
                             
                            } else {
                                bytes = File.ReadAllBytes("donationIcon.png");
                                profilePhoto = Convert.ToBase64String(bytes);
                            }

                        } else {
                            profilePhoto = photoResponse.Item2;
                        }


                        var pin = new CustomPin
                        {
                            Type = PinType.Generic,
                            Position = finalPosition,
                            Label = user.firstName + " " + user.middleName + " " + user.lastName,
                            Address = user.cityOfDeath + ", " + user.regionOfDeath + ", " + user.countryOfDeath,
                            Id = "Xamarin",
                            Url = String.Join(",", organIcons),
                            genderIcon = genderIcon,
                            userPhoto = profilePhoto                         
                        };
                        customMap.CustomPins.Add(pin);
                        customMap.Pins.Add(pin);


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
