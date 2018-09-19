using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SlideOverKit;
using Xamarin.Forms;
using Xamarin.Forms.Maps;
using System.Threading.Tasks;
using mobileAppClient.odmsAPI;
using System.Net;
using System.IO;
using System.Linq;
using System.Threading;
using mobileAppClient.Maps;
using mobileAppClient.Models;

namespace mobileAppClient.Views.Clinician
{
	public class ClinicianMapPage : MenuContainerPage
	{

        List<CustomMapObject> users;
        List<Hospital> hospitals;

	    private int heliCount = 0;

        CustomMap customMap;

        public ClinicianMapPage()
        {
            this.SlideMenu = new SlideUpMenuView();

        }

        public async void displayBottomSheet(CustomPin pin, CustomMap map) {

            DependencyService.Get<BottomSheetMapInterface>().addSlideUpSheet(pin, map);
        }

        public async void displayUserDialog(string organString, string id)
        {
            //if Android, use the SlideOverKit stuff
            if (Device.RuntimePlatform == Device.Android)
            {
                ShowMenu();
            }
            //otherwise iPhone or something
            else
            {
                string[] organList = organString.Split(',');
                id = organList[organList.Length - 1];
                organList = organList.Take(organList.Length - 1).ToArray();
                List<string> finalList = new List<string>();
                string final;
                foreach (string item in organList)
                {
                    final = item.Replace("_icon.png", "");
                    finalList.Add("- " + final + "\n");
                }
                string listString = String.Join("", finalList);

                var answer = await DisplayAlert("Display User?", "This user is currently donating: \n" + listString + "Would you like to view their profile?", "Yes", "No");
                if (answer == true)
                {
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
        }

        ///// <summary>
        ///// Activated whenever focus is on this page
        ///// </summary>
        protected override async void OnAppearing()
        {
            this.SlideMenu = new SlideUpMenuView();

            customMap = new CustomMap
            {
                MapType = MapType.Street,
                WidthRequest = 100,
                HeightRequest = 100,
            };


            customMap.CustomPins = new Dictionary<Position, CustomPin> { };
            customMap.HelicopterPins = new Dictionary<String, CustomPin> { };

            //Center on New Zealand

            var centerPosition = new Position(-41.626217, 172.361873);

            customMap.MoveToRegion(MapSpan.FromCenterAndRadius(
                centerPosition, Distance.FromMiles(500.0)));

          

            var stack = new StackLayout { Spacing = 0 };
            stack.Children.Add(customMap);
            Content = stack;
            Title = "Available Donor Map";

            //Get all the organs from the database
            //Create pins for every organ
            UserAPI userAPI = new UserAPI();
            Tuple<HttpStatusCode, List<CustomMapObject>> tuple = await userAPI.GetOrgansForMap();

            //await InitialiseHospitals();

            AddTestHelicopter();
            AddTest2Helicopter();

            switch (tuple.Item1)
            {
                case HttpStatusCode.OK:
                    Console.WriteLine("Organ Map Objects Successfully received");
                    users = tuple.Item2;

                    Geocoder geocoder = new Geocoder();

                    foreach (CustomMapObject user in users)
                    {
                        if (user.organs.Count == 0)
                        {
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

                        foreach (DonatableOrgan organ in user.organs)
                        {

                            string imageString = "";
                            switch (organ.organType.ToLower())
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
                                case ("connective-tissue"):
                                    imageString = "connective-tissue_icon.png";
                                    break;
                                case ("bone-marrow"):
                                    imageString = "bone-marrow_icon.png";
                                    break;
                                case ("skin"):
                                    imageString = "skin_icon.png";
                                    break;
                                case ("lung"):
                                    imageString = "lung_icon.png";
                                    break;
                                case ("cornea"):
                                    imageString = "cornea_icon.png";
                                    break;
                                case ("kidney"):
                                    imageString = "kidney_icon.png";
                                    break;
                                case ("intestine"):
                                    imageString = "intestine_icon.png";
                                    break;
                                case ("middle-ear"):
                                    imageString = "middle-ear_icon.png";
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
                        switch (user.gender)
                        {
                            case ("Male"):
                                int number = rnd.Next(1, 15);
                                genderIcon = "man" + number + ".png";
                                break;
                            case ("Female"):
                                number = rnd.Next(1, 12);
                                genderIcon = "woman" + number + ".png";
                                break;
                            case ("Other"):
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
                        if (photoResponse.Item1 != HttpStatusCode.OK)
                        {
                            Console.WriteLine("Failed to retrieve profile photo or no profile photo found.");
                            Byte[] bytes;
                            if (Device.RuntimePlatform == Device.Android)
                            {

                            }
                            else
                            {
                                bytes = File.ReadAllBytes("donationIcon.png");
                                profilePhoto = Convert.ToBase64String(bytes);
                            }

                        }
                        else
                        {
                            profilePhoto = photoResponse.Item2;
                        }


                        var pin = new CustomPin
                        {
                            CustomType = ODMSPinType.DONOR,
                            Position = finalPosition,
                            Label = user.firstName + " " + user.middleName + " " + user.lastName,
                            Address = user.cityOfDeath + ", " + user.regionOfDeath + ", " + user.countryOfDeath,
                            Url = String.Join(",", organIcons),
                            genderIcon = genderIcon,
                            userPhoto = profilePhoto,
                            userId = user.id,
                            donatableOrgans = user.organs
                        };
                        customMap.CustomPins.Add(pin.Position, pin);

                        lock (customMap.Pins)
                        {
                            customMap.Pins.Add(pin);
                        }
                        
                    }

                    StartTimer(200);
                    break;

                case HttpStatusCode.ServiceUnavailable:
                    await DisplayAlert("",
                    "Server unavailable, check connection",
                    "OK");
                    StartTimer(200);
                    break;
                case HttpStatusCode.InternalServerError:
                    await DisplayAlert("",
                    "Server error retrieving organs, please try again (500)",
                    "OK");
                    StartTimer(200);
                    break;
            }
        }

        private async Task InitialiseHospitals()
        {

            ClinicianAPI clinicianApi = new ClinicianAPI();
            Tuple<HttpStatusCode, List<Hospital>> tuple = await clinicianApi.GetHospitals();
            switch (tuple.Item1)
            {
                case HttpStatusCode.OK:
                    Console.WriteLine("Organ map hospitals retrieved successfully");
                    hospitals = tuple.Item2;

                    foreach (Hospital currentHospital in hospitals)
                    {
                        Position finalPosition = new Position(currentHospital.latitude, currentHospital.longitude);

                        var pin = new CustomPin
                        {
                            CustomType = ODMSPinType.HOSPITAL,
                            Position = finalPosition,
                            Label = currentHospital.name,
                            Address = currentHospital.address,
                        };

                        // We add to this list to track our pins with additional information (like hospital or donor)
                        customMap.CustomPins.Add(pin.Position, pin);

                        lock (customMap.Pins)
                        {
                            customMap.Pins.Add(pin);
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
                    "Server error retrieving hospitals, please try again (500)",
                    "OK");
                    break;
            }
        }

        private void AddTestHelicopter()
        {
            Position start = new Position(-37.9061137, 176.2050742);
            Position end = new Position(-36.8613687, 174.7676895);
            AddHelicopter(start, end, Organ.LIVER);
        }

	    private void AddTest2Helicopter()
	    {
	        Position start = new Position(-36.8613687, 174.7676895);
            Position end = new Position(-37.9061137, 176.2050742);
            AddHelicopter(start, end, Organ.LUNG);
        }

        /// <summary>
        /// Adds a helicopter to the map!
        /// </summary>
        /// <param name="start"></param>
        /// <param name="end"></param>
        /// <param name="organToTransferType"></param>
	    private void AddHelicopter(Position start, Position end, Organ organToTransferType)
	    {
            // Iterate the unique helicopter identifier (is used as a dict key in the map renderer)
	        string heliID = (++heliCount).ToString();

	        Helicopter heli = new Helicopter()
	        {
	            startPosition = start,
	            destinationPosition = end,
                isLanding = false
	        };

            // Address is used by helicopters to hold their unique ID
            CustomPin heliPin = new CustomPin
	        {
	            CustomType = ODMSPinType.HELICOPTER,
                OrganToTransport = organToTransferType,
	            Label = "Heli",
	            HelicopterDetails = heli,
	            Position = heli.startPosition,
	            Address = heliID
            };

            // Add the main helichopper pin to our list of custom heli pins we can track (heli pin contains the transported organ custom pin)
	        customMap.HelicopterPins.Add(heliPin.Address, heliPin);

            // Add the pin we want visible on the map (but cant track these)
	        customMap.Pins.Add(heliPin);
        }

        /// <summary>
        /// Starts the helicopter refresh timer
        /// </summary>
        /// <param name="interval"> Time between refreshes in milliseconds </param>
        public void StartTimer(int interval)
        {
            // TODO change '5000' to '0' when transferring is correctly implemented (is delay between timer started + timer actually starting to call tick method)
            Timer t = new Timer(RefreshHelipcopterPositions, null, 5000, interval);
        }

        /// <summary>
        /// Processes each live helicopter and moves it one step, disposing of the helicopter in case of reaching the destination
        /// </summary>
        /// <param name="o"></param>
        private void RefreshHelipcopterPositions(object o)
        {
            Dictionary<String, CustomPin> intermediateHeliPins = new Dictionary<String, CustomPin>();

            // Copy the live map helicopters into an intermediary array that we can edit and calc new positions
            foreach (var singleHelicopterPin in customMap.HelicopterPins.Values)
            {
                Position currentPosition = singleHelicopterPin.Position;
                Position newHeliPosition;

                // Calc new position
                newHeliPosition = singleHelicopterPin.HelicopterDetails.getNewPosition(currentPosition);

                // Add to the intermediary dictionary, and modify to include the new position
                intermediateHeliPins.Add(singleHelicopterPin.Address, singleHelicopterPin);
                intermediateHeliPins[singleHelicopterPin.Address].Position = newHeliPosition;
            }

            // Copy intermediary dictionary into the Maps custom dictionary of helis
            customMap.HelicopterPins = new Dictionary<String, CustomPin>(intermediateHeliPins);

            
            foreach (var singleHelicopterPin in intermediateHeliPins.Values)
            {
                // Clear the pin first
                Device.BeginInvokeOnMainThread(() =>
                {
                    customMap.Pins.Remove(singleHelicopterPin);
                });

                // Check if the helicopter has reached its destination
                if ((singleHelicopterPin.HelicopterDetails.hasArrived(singleHelicopterPin.Position)))
                {
                    // If it has, raise the isLanding flag and still add the pin to map
                    // (allows the map renderer to remove the organ radius + flight path if selected)
                    if (!singleHelicopterPin.HelicopterDetails.isLanding)
                    {
                        // Start landing procedure
                        customMap.HelicopterPins[singleHelicopterPin.Address].HelicopterDetails.isLanding = true;
                    }
                    else
                    {
                        // When the pin (in the last refresh loop had its isLanding flag raised) is refreshed again,
                        // remove the pin as it will have been tidied up on the map renderer side
                        customMap.HelicopterPins.Remove(singleHelicopterPin.Address);
                        return;
                    }
                }

                // Add the pin finally on the map in its refreshed locations
                Device.BeginInvokeOnMainThread(() =>
                {
                    customMap.Pins.Add(singleHelicopterPin);
                });
                
            }
        }
    }
}
