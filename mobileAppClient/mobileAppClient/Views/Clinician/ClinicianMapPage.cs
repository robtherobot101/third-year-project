using System;
using System.Collections.Generic;
using System.Linq;
using SlideOverKit;
using Xamarin.Forms;
using Xamarin.Forms.Maps;
using System.Threading.Tasks;
using mobileAppClient.odmsAPI;
using System.Net;
using System.IO;
using System.Threading;
using mobileAppClient.Maps;
using mobileAppClient.Models;
using Xamarin.Essentials;

namespace mobileAppClient.Views.Clinician
{
    public class ClinicianMapPage : ContentPage
	{

        List<CustomMapObject> users;
        List<Hospital> hospitals;

	    public int heliCount = 0;
        Timer helicopterRefreshTimer;

        public CustomMap customMap;

        public ClinicianMapPage()
        {

        }



        public async Task displayUserDialog(string organString, string id)
        {
            //if Android, use the SlideOverKit stuff
            if (Device.RuntimePlatform == Device.Android)
            {

                //ShowMenu();

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

                            DependencyService.Get<BottomSheetMapInterface>().removeBottomSheetWhenViewingAUser();

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

            customMap = new CustomMap
            {
                MapType = MapType.Street,
                WidthRequest = 100,
                HeightRequest = 100,
            };


            customMap.CustomPins = new Dictionary<Position, CustomPin> { };
            customMap.HelicopterPins = new Dictionary<string, CustomPin> { };

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

            await InitialiseHospitals();

            await StartTransfers();

            //AddTestHelicopter();
            //AddTest2Helicopter();

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
                    Console.WriteLine("Reached the end of OnAppearing");
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

            await Task.Delay(3000);
        }

        protected override void OnDisappearing()
        {
            helicopterRefreshTimer?.Dispose();
        }

        public async Task InitialiseHospitals()
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
                        Console.WriteLine("Tracking positions");
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

        /// <summary>
        /// Adds a helicopter to the map!
        /// </summary>
        /// <param name="start"></param>
        /// <param name="end"></param>
        /// <param name="organToTransferType"></param>
	    private void AddHelicopter(Position start, Position end, Organ organToTransferType, int seconds,  long receiverId, int waitingListItemId, int organId)
	    {
            // Iterate the unique helicopter identifier (is used as a dict key in the map renderer)
	        string heliID = (++heliCount).ToString();

            Helicopter heli = new Helicopter()
            {
                startPosition = start,
                destinationPosition = end,
                isLanding = false,
                duration = seconds,
                organId = organId,
                receiverId = receiverId,
                waitingListItemId = waitingListItemId

	        };

            // Address is used by helicopters to hold their unique ID
            CustomPin heliPin = new CustomPin
	        {
	            CustomType = ODMSPinType.HELICOPTER,
                OrganToTransport = organToTransferType,
	            Label = "Heli",
	            HelicopterDetails = heli,
	            Position = heli.startPosition,
	            Address = heliID,
                Url = "Heli" + "," + heliID
            };
            customMap.HelicopterPins.Add(heliPin.Address, heliPin);

            // Add the main helichopper pin to our list of custom heli pins we can track (heli pin contains the transported organ custom pin)

            Dictionary<string, CustomPin> testDictionary = new Dictionary<string, CustomPin>(customMap.HelicopterPins);
            testDictionary[heliID] = heliPin;

            customMap.HelicopterPins = testDictionary;

            customMap.Pins.Add(heliPin);



            // Add the pin we want visible on the map (but cant track these)
        }

        /// <summary>
        /// Starts the helicopter refresh timer
        /// </summary>
        /// <param name="interval"> Time between refreshes in milliseconds </param>
        public void StartTimer(int interval)
        {
            // TODO change '5000' to '0' when transferring is correctly implemented (is delay between timer started + timer actually starting to call tick method)
            helicopterRefreshTimer = new Timer(RefreshHelipcopterPositions, null, 0, interval);
        }

        /// <summary>
        /// Processes each live helicopter and moves it one step, disposing of the helicopter in case of reaching the destination
        /// </summary>
        /// <param name="o"></param>
        private async void RefreshHelipcopterPositions(object o)
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
                Device.BeginInvokeOnMainThread(() =>
                {
                    intermediateHeliPins[singleHelicopterPin.Address].Position = newHeliPosition;
                });

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

                        await HelicopterFinished(singleHelicopterPin.HelicopterDetails.waitingListItemId, singleHelicopterPin.HelicopterDetails.organId);

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

        public async Task StartTransfers()
        {
            TransplantListAPI transplantListAPI = new TransplantListAPI();
            List<OrganTransfer> transfers = await transplantListAPI.GetAllTransfers();

            foreach (OrganTransfer transfer in transfers)
            {
                int waitingListId = await transplantListAPI.GetWaitingListId((int)transfer.receiverId, transfer.organType);

                if (transfer.arrivalTime.ToDateTimeWithSeconds() < DateTime.Now)
                {
                    await HelicopterFinished(waitingListId, transfer.id);
                }
                else
                {

                    AddHelicopter(
                        GetCurrentPoint(transfer),
                        new Position(transfer.endLat, transfer.endLon),
                        transfer.organType,
                        (int)transfer.arrivalTime.ToDateTimeWithSeconds().Subtract(DateTime.Now).TotalSeconds,
                        transfer.receiverId,
                        waitingListId,
                        transfer.id);
                }
            }
        }

        private Position GetCurrentPoint(OrganTransfer transfer)
        {
            double degToRad = Math.PI / 180;

            double timeToDest = transfer.arrivalTime.ToDateTimeWithSeconds().Subtract(DateTime.Now).TotalSeconds;

            double distToDest = timeToDest * 70 / 1852;

            double distRads = (Math.PI / (180 * 60)) * distToDest;

            double deltaLongitude = transfer.startLon - transfer.endLon;



            double x = Math.Cos(transfer.endLat * degToRad) * Math.Sin(deltaLongitude * degToRad);
            double y = Math.Cos(transfer.startLat * degToRad) * Math.Sin(transfer.endLat * degToRad) - Math.Sin(transfer.startLat * degToRad) * Math.Cos(transfer.endLat * degToRad) * Math.Cos(deltaLongitude * degToRad);

            double bearing = Math.Atan2(x, y) / degToRad - 180;

            double currentLat = Math.Asin(Math.Sin(transfer.endLat * degToRad) * Math.Cos(distRads) + Math.Cos(transfer.endLat * degToRad) * Math.Sin(distRads) * Math.Cos(bearing * degToRad));

            double test = currentLat / degToRad;

            double distanceLon = Math.Atan2(Math.Sin(bearing * degToRad) * Math.Sin(distRads) * Math.Cos(transfer.endLat * degToRad), Math.Cos(distRads) - Math.Sin(transfer.endLat * degToRad) * Math.Sin(currentLat));
            double currentLon = ((transfer.endLon * degToRad - distanceLon + Math.PI) % (2 * Math.PI)) - Math.PI;

            return new Position(currentLat / degToRad, currentLon / degToRad);
        }

        public async Task NewTransfer(DonatableOrgan currentOrgan, User selectedRecipient, Position donorPosition) {
            OrganTransfer newOrganTransfer = new OrganTransfer();
            newOrganTransfer.id = currentOrgan.id;
            newOrganTransfer.receiverId = selectedRecipient.id;

            //Find the position of the donor
            newOrganTransfer.startLat = donorPosition.Latitude;
            newOrganTransfer.startLon = donorPosition.Longitude;

            Hospital receiverHospital = null;
            //await InitialiseHospitalsWithoutAddingToMap();
            foreach (Hospital hospital in hospitals) {
                if(hospital.region.Equals(selectedRecipient.region)) {
                    receiverHospital = hospital;
                }
            }

            //Find the nearest hospital
            newOrganTransfer.endLat = receiverHospital.latitude;
            newOrganTransfer.endLon = receiverHospital.longitude;

            newOrganTransfer.organType = OrganExtensions.ToOrgan(currentOrgan.organType);

            Position HospitalPosition = new Position(receiverHospital.latitude, receiverHospital.longitude);

            newOrganTransfer.arrivalTime = new CustomDateTime(DateTime.Now.AddSeconds(distance(donorPosition.Latitude, HospitalPosition.Latitude,
                                                                                               donorPosition.Longitude, HospitalPosition.Longitude, 0, 0) / 70));

            TransplantListAPI transplantListAPI = new TransplantListAPI();
            if (await transplantListAPI.InsertTransfer(newOrganTransfer) != HttpStatusCode.OK) {
                await DisplayAlert("", "Failed to start transfer (failed to insert transfer)", "OK");
                return;
            }

            if (await transplantListAPI.SetInTransfer(currentOrgan.id, 1) != true) {
                await DisplayAlert("", "Failed to start transfer (failed to set in transfer)", "OK");
                return;
            }

            int TTA = (int)newOrganTransfer.arrivalTime.ToDateTimeWithSeconds().Subtract(DateTime.Now).TotalSeconds;

            //int waitingListId = await transplantListAPI.GetWaitingListId((int)newOrganTransfer.receiverId, newOrganTransfer.organType);
            //if (waitingListId == 0) {
            //    await DisplayAlert("", "Failed to start transfer (failed to get waiting list id)", "OK");
            //    return;
            //}

            int waitingListId = 0;
            foreach(WaitingListItem item in selectedRecipient.waitingListItems) {
                if(item.organType == OrganExtensions.ToOrgan(currentOrgan.organType)) {
                    waitingListId = item.id;
                }
            }

            AddHelicopter(donorPosition,
                          HospitalPosition,
                          newOrganTransfer.organType,
                          TTA,
                          newOrganTransfer.receiverId,
                          waitingListId,
                          newOrganTransfer.id
                          );
        }

        public async Task NewTransferWithoutAddingHelicpoter(DonatableOrgan currentOrgan, User selectedRecipient, Position donorPosition)
        {
            await InitialiseHospitalsWithoutAddingToMap();

            OrganTransfer newOrganTransfer = new OrganTransfer();
            newOrganTransfer.id = currentOrgan.id;
            newOrganTransfer.receiverId = selectedRecipient.id;

            //Find the position of the donor
            newOrganTransfer.startLat = donorPosition.Latitude;
            newOrganTransfer.startLon = donorPosition.Longitude;

            Hospital receiverHospital = null;
            //await InitialiseHospitalsWithoutAddingToMap();
            foreach (Hospital hospital in hospitals)
            {
                if (hospital.region.Equals(selectedRecipient.region))
                {
                    receiverHospital = hospital;
                }
            }

            //Find the nearest hospital
            newOrganTransfer.endLat = receiverHospital.latitude;
            newOrganTransfer.endLon = receiverHospital.longitude;

            newOrganTransfer.organType = OrganExtensions.ToOrgan(currentOrgan.organType);

            Position HospitalPosition = new Position(receiverHospital.latitude, receiverHospital.longitude);

            newOrganTransfer.arrivalTime = new CustomDateTime(DateTime.Now.AddSeconds(distance(donorPosition.Latitude, HospitalPosition.Latitude,
                                                                                               donorPosition.Longitude, HospitalPosition.Longitude, 0, 0) / 70));

            TransplantListAPI transplantListAPI = new TransplantListAPI();
            await transplantListAPI.InsertTransfer(newOrganTransfer);
            await transplantListAPI.SetInTransfer(currentOrgan.id, 1);
        }

        public async Task InitialiseHospitalsWithoutAddingToMap()
        {

            ClinicianAPI clinicianApi = new ClinicianAPI();
            Tuple<HttpStatusCode, List<Hospital>> tuple = await clinicianApi.GetHospitals();
            switch (tuple.Item1)
            {
                case HttpStatusCode.OK:
                    Console.WriteLine("Organ map hospitals retrieved successfully");
                    hospitals = tuple.Item2;

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

        public double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2)
        {

            int R = 6371; // Radius of the earth

            double latDistance = (Math.PI/180)*(lat2 - lat1);
            double lonDistance = (Math.PI / 180) * (lon2 - lon1);
            double a = Math.Sin(latDistance / 2) * Math.Sin(latDistance / 2)
                           + Math.Cos((Math.PI / 180) * (lat1)) * Math.Cos((Math.PI / 180) * (lat2))
                    * Math.Sin(lonDistance / 2) * Math.Sin(lonDistance / 2);
            double c = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));
            double interDistance = R * c * 1000; // convert to meters

            double height = el1 - el2;

            interDistance = Math.Pow(interDistance, 2) + Math.Pow(height, 2);

            return Math.Sqrt(interDistance);
        }

        private async Task HelicopterFinished(int waitingListItemId, int organId)
        {
            TransplantListAPI transplantListAPI = new TransplantListAPI();
            await transplantListAPI.DeleteTransfer(organId);
            await transplantListAPI.DeleteWaitingListItem(waitingListItemId);
            await transplantListAPI.SetInTransfer(organId, 2);

        }

        public async Task<bool> CheckGetToReceiverInTime(DonatableOrgan organ, User receiver) {

            UserAPI userAPI = new UserAPI();

            User donor = await userAPI.getUser(organ.donorId, ClinicianController.Instance.AuthToken);
            double startLat = 0;
            double startLon = 0;
            try
            {
                var address = donor.cityOfDeath + ", " + donor.regionOfDeath + ", " + donor.countryOfDeath;
                var locations = await Geocoding.GetLocationsAsync(address);

                var location = locations?.FirstOrDefault();
                if (location != null)
                {
                    startLat = location.Latitude;
                    startLon = location.Longitude;
                    Console.WriteLine($"Latitude: {location.Latitude}, Longitude: {location.Longitude}, Altitude: {location.Altitude}");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.StackTrace);
            }
            Hospital receiverHospital = null;

            await InitialiseHospitalsWithoutAddingToMap();

            foreach (Hospital hospital in hospitals) {
                if (hospital.region == receiver.region){
                    receiverHospital = hospital;
                }
            }
            double dist = distance(
                    startLat,
                    receiverHospital.latitude,
                    startLon,
                    receiverHospital.longitude, 0, 0);

            DateTime arrivalTime = DateTime.Now.AddSeconds((long)(dist / 69.444444));

            return arrivalTime < (DateTime.Now.AddSeconds(organ.getTimeRemaining().Item2));
        }

    }
}
