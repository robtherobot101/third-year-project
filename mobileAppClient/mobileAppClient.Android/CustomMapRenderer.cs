using System;
using System.Collections.Generic;
using System.Threading;
using Android.Content;
using Android.Gms.Maps;
using Android.Gms.Maps.Model;
using Android.Graphics;
using Android.Widget;
using CustomRenderer.Droid;
using mobileAppClient;
using mobileAppClient.Droid;
using mobileAppClient.Models;
using Xamarin.Forms;
using Xamarin.Forms.Maps;
using Xamarin.Forms.Maps.Android;

using mobileAppClient.Views.Clinician;
using CustomPin = mobileAppClient.CustomPin;

[assembly: ExportRenderer(typeof(CustomMap), typeof(CustomMapRenderer))]
namespace CustomRenderer.Droid
{

    public class CustomMapRenderer : MapRenderer, GoogleMap.IInfoWindowAdapter
    {
        private Dictionary<Position, CustomPin> customPins;
        private Dictionary<string, CustomPin> helicopterPins;
        private Dictionary<Organ, int> helicopterIcons;

        private String selectedHeli;
        private Tuple<CustomPin, Polyline> highlightedFlightPath;
        private Tuple<CustomPin, Circle> highlightedOrganRange;

        CustomMap formsMap;

        public CustomMapRenderer(Context context) : base(context)
        {

        }

        protected override void OnElementChanged(Xamarin.Forms.Platform.Android.ElementChangedEventArgs<Map> e)
        {
            base.OnElementChanged(e);

            if (e.OldElement != null)
            {
                NativeMap.InfoWindowClick -= OnInfoWindowClick;
            }

            if (e.NewElement != null)
            {
                formsMap = (CustomMap)e.NewElement;
                customPins = formsMap.CustomPins;
                helicopterPins = formsMap.HelicopterPins;
                intialiseHelicopterIcons();

                highlightedFlightPath = new Tuple<CustomPin, Polyline>(null, null);
                highlightedOrganRange = new Tuple<CustomPin, Circle>(null, null);

                Control.GetMapAsync(this);
            }
        }

        /// <summary>
        /// Initialises a dictionary used to lookup helicopter icons based on an organ enum
        /// </summary>
        private void intialiseHelicopterIcons()
        {
            helicopterIcons = new Dictionary<Organ, int>();
            helicopterIcons.Add(Organ.LIVER, Resource.Drawable.helicopter_liver_icon);
            helicopterIcons.Add(Organ.KIDNEY, Resource.Drawable.helicopter_kidney_icon);
            helicopterIcons.Add(Organ.PANCREAS, Resource.Drawable.helicopter_pancreas_icon);
            helicopterIcons.Add(Organ.HEART, Resource.Drawable.helicopter_heart_icon);
            helicopterIcons.Add(Organ.LUNG, Resource.Drawable.helicopter_lung_icon);
            helicopterIcons.Add(Organ.INTESTINE, Resource.Drawable.helicopter_intestine_icon);
            helicopterIcons.Add(Organ.CORNEA, Resource.Drawable.helicopter_cornea_icon);
            helicopterIcons.Add(Organ.EAR, Resource.Drawable.helicopter_ear_icon);
            helicopterIcons.Add(Organ.SKIN, Resource.Drawable.helicopter_skin_icon);
            helicopterIcons.Add(Organ.BONE, Resource.Drawable.helicopter_bone_icon);
            helicopterIcons.Add(Organ.TISSUE, Resource.Drawable.helicopter_tissue_icon);
        }

        protected override void OnMapReady(GoogleMap map)
        {
            base.OnMapReady(map);

            NativeMap.InfoWindowClick += OnInfoWindowClick;
            NativeMap.SetInfoWindowAdapter(this);
        }

        /// <summary>
        /// Generates marker options for a pin that contains information about a donor
        /// </summary>
        /// <param name="pin"></param>
        /// <returns></returns>
        private MarkerOptions CreateDonorMarker(CustomPin pin)
        {
            var marker = new MarkerOptions();
            marker.SetPosition(new LatLng(pin.Position.Latitude, pin.Position.Longitude));
            marker.SetTitle(pin.Label);
            marker.SetSnippet(pin.Address);
            Bitmap imageBitmap;

            switch (pin.genderIcon)
            {
                case "man1.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.man1);
                    break;
                case "man2.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.man2);
                    break;
                case "man3.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.man3);
                    break;
                case "man4.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.man4);
                    break;
                case "man5.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.man5);
                    break;
                case "man6.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.man6);
                    break;
                case "man7.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.man7);
                    break;
                case "man8.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.man8);
                    break;
                case "man9.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.man9);
                    break;
                case "man10.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.man10);
                    break;
                case "man11.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.man11);
                    break;
                case "man12.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.man12);
                    break;
                case "man13.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.man13);
                    break;
                case "man14.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.man14);
                    break;
                case "woman1.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.woman1);
                    break;
                case "woman2.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.woman2);
                    break;
                case "woman3.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.woman3);
                    break;
                case "woman4.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.woman4);
                    break;
                case "woman5.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.woman5);
                    break;
                case "woman6.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.woman6);
                    break;
                case "woman7.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.woman7);
                    break;
                case "woman8.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.woman8);
                    break;
                case "woman9.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.woman9);
                    break;
                case "woman10.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.woman10);
                    break;
                case "woman11.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.woman11);
                    break;
                case "other.png":
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.other);
                    break;
                default:
                    imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.other);
                    break;

            }
            Bitmap resizedBitmap;
            if (pin.genderIcon.Equals("other.png"))
            {
                resizedBitmap = Bitmap.CreateScaledBitmap(imageBitmap, 110, 110, false);
            }
            else
            {
                resizedBitmap = Bitmap.CreateScaledBitmap(imageBitmap, 120, 120, false);
            }
            marker.SetIcon(BitmapDescriptorFactory.FromBitmap(resizedBitmap));

            return marker;
        }

        /// <summary>
        /// Generates marker options for a pin that contains information about a hospital
        /// </summary>
        /// <param name="pin"></param>
        /// <returns></returns>
        private MarkerOptions CreateHospitalMarker(CustomPin pin)
        {
            // Create basic options
            var marker = new MarkerOptions();
            marker.SetPosition(new LatLng(pin.Position.Latitude, pin.Position.Longitude));
            marker.SetTitle(pin.Label);
            marker.SetSnippet(pin.Address);

            // Create the image
            Bitmap imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.hospital_icon);

            // Scale the image
            imageBitmap = Bitmap.CreateScaledBitmap(imageBitmap, 110, 110, false);

            marker.SetIcon(BitmapDescriptorFactory.FromBitmap(imageBitmap));

            return marker;
        }

        /// <summary>
        /// Generates marker options for a pin that contains information about a helicopter
        /// </summary>
        /// <param name="pin"></param>
        /// <returns></returns>
        private MarkerOptions CreateHelicopterMarker(CustomPin pin)
        {
            refreshFlightDetails(pin);
            
            // Create basic options
            var marker = new MarkerOptions();
            marker.SetPosition(new LatLng(pin.Position.Latitude, pin.Position.Longitude));
            marker.SetTitle(pin.Label);

            // Snippet is set to store the unique heli identifier (dictionary key) so it can be recalled when searching for a customPin based on Marker
            marker.SetSnippet(pin.Address);

            // Create the image
            Bitmap imageBitmap = BitmapFactory.DecodeResource(Resources, helicopterIcons[pin.OrganToTransport]);

            // Scale the image
            imageBitmap = Bitmap.CreateScaledBitmap(imageBitmap, 170, 170, false);

            marker.SetIcon(BitmapDescriptorFactory.FromBitmap(imageBitmap));

            return marker;
        }

        /// <summary>
        /// Creates a MarkerOptions for a new pin on the map
        /// </summary>
        /// <param name="pin"></param>
        /// <returns></returns>
        protected override MarkerOptions CreateMarker(Pin pin)
        {
            MarkerOptions markerToAddOptions = null;

            // Find the correlating custom pin to get our extra parameters
            CustomPin foundCustomPin = GetCustomPin(pin);

            switch (foundCustomPin.CustomType)
            {
                case ODMSPinType.DONOR:
                    markerToAddOptions = CreateDonorMarker(foundCustomPin);
                    break;
                case ODMSPinType.HOSPITAL:
                    markerToAddOptions = CreateHospitalMarker(foundCustomPin);
                    break;
                case ODMSPinType.HELICOPTER:
                    markerToAddOptions = CreateHelicopterMarker(foundCustomPin);
                    break;
            }
            return markerToAddOptions;
        }


        void OnInfoWindowClick(object sender, GoogleMap.InfoWindowClickEventArgs e)
        {
            var customPin = GetCustomPin(e.Marker);
            if (customPin == null)
            {
                throw new Exception("Custom pin not found");
            }


            //MapActivity mapActivity = new MapActivity();
            //mapActivity.InitializeMap();

            //Context ac = Context.ApplicationContext;
            //@ Andy peek here. The way the Bsheet works is that it creates a fragment. 
            // It then calls the show() method, which requires some form of fragmentmanager, which you get from a context/activity. There is an activity file which I was meddling with in here, but didn't reallly work
            // I used the github link in discord as reference. 
            // One of the biggest hurdles is that fragments are now deprecated and so need the Android.Support.V4.xxx to work with this library.
            // If you're confused would recommend loading up his sample project and deploying it.

            //BottomSheetFragment fragment = BottomSheetFragment.NewInstance(0, "test");

            //fragment.Show(ac., "dialog");
            ClinicianMapPage parent = (ClinicianMapPage)formsMap.Parent.Parent;
            parent.displayUserDialog(customPin.Url, customPin.Url.Substring(customPin.Url.Length - 1));
        }

        /// <summary>
        /// Toggles more details regarding a helicopter (organs + flight path)
        /// </summary>
        /// <param name="customPin"></param>
        private void processHelicopterTapped(CustomPin customPin)
        {
            clearFlightPath();
            clearOrganRange();

            // Heli is already highlighted, deselect
            if (customPin.Address.Equals(selectedHeli))
            {
                // Reset selected heli
                selectedHeli = null;
            }
            else
            {
                selectedHeli = customPin.Address;
                addFlightPath(customPin);
                addOrganRange(customPin);
            }
        }

        /// <summary>
        /// Refreshes the flight path if the helicopter is currently selected
        /// </summary>
        /// <param name="heliPin"></param>
        private void refreshFlightDetails(CustomPin heliPin)
        {

            if (heliPin.Address.Equals(selectedHeli))
            {
                clearFlightPath();
                clearOrganRange();

                if (!heliPin.HelicopterDetails.hasArrived(heliPin.Position))
                {
                    addFlightPath(heliPin);
                    addOrganRange(heliPin);
                }
            }
        }

        /// <summary>
        /// Adds a polyline path based on helicopter destination and current position
        /// </summary>
        /// <param name="heliPin"></param>
        private void addFlightPath(CustomPin heliPin)
        {
            var currentFlightPathOptions = new PolylineOptions();

            // Other colour constants can be found at https://developer.android.com/reference/android/graphics/Color#constants_1
            int BLUE = -16776961;
            currentFlightPathOptions.InvokeColor(BLUE);
            
            // Add current position (start of path)
            currentFlightPathOptions.Add(new LatLng(heliPin.Position.Latitude, heliPin.Position.Longitude));

            // Add destination position (end of path)
            currentFlightPathOptions.Add(new LatLng(heliPin.HelicopterDetails.destinationPosition.Latitude,
                heliPin.HelicopterDetails.destinationPosition.Longitude));

            highlightedFlightPath = new Tuple<CustomPin, Polyline>(heliPin, NativeMap.AddPolyline(currentFlightPathOptions));
        }

        /// <summary>
        /// Adds a bubble that reflects the expiry range of a helicopter carrying organ
        /// </summary>
        /// <param name="heliPin"></param>
        private void addOrganRange(CustomPin heliPin)
        {
            var circleOptions = new CircleOptions();
            circleOptions.InvokeCenter(new LatLng(heliPin.Position.Latitude, heliPin.Position.Longitude));

            // TODO adjust to organ countdown
            circleOptions.InvokeRadius(40000);

            circleOptions.InvokeFillColor(0X660000FF);
            circleOptions.InvokeStrokeColor(0X660000FF);
            circleOptions.InvokeStrokeWidth(0);

            highlightedOrganRange = new Tuple<CustomPin, Circle>(heliPin, NativeMap.AddCircle(circleOptions));
        }
    
        /// <summary>
        /// Clears the highlighted flight path
        /// </summary>
        private void clearFlightPath()
        {
            highlightedFlightPath.Item2?.Remove();
            highlightedFlightPath = new Tuple<CustomPin, Polyline>(null, null);
        }

        /// <summary>
        /// Clears the highlighted organ expiry range
        /// </summary>
        private void clearOrganRange()
        {
            highlightedOrganRange.Item2?.Remove();
            highlightedOrganRange = new Tuple<CustomPin, Circle>(null, null);
        }

        public Android.Views.View GetInfoContents(Marker marker)
        {
            if (Android.App.Application.Context.GetSystemService(Context.LayoutInflaterService) is Android.Views.LayoutInflater inflater)
            {
                Android.Views.View view;
                var customPin = GetCustomPin(marker);
                if (customPin == null)
                {
                    return null;
                }

                if (customPin.CustomType == ODMSPinType.HOSPITAL)
                {
                    // Hospital pop-up dialog not yet implemented
                    return null;
                }

                if (customPin.CustomType == ODMSPinType.HELICOPTER)
                {
                    marker.Title = null;
                    processHelicopterTapped(customPin);
                    return null;
                }

                view = inflater.Inflate(Resource.Layout.XamarinMapInfoWindow, null);

                var infoTitle = view.FindViewById<TextView>(Resource.Id.InfoWindowTitle);
                var infoAddress = view.FindViewById<TextView>(Resource.Id.InfoWindowAddress);
                var imageFrame = view.FindViewById<ImageView>(Resource.Id.ImageFrame);
                var organFrame = view.FindViewById<LinearLayout>(Resource.Id.OrganFrame);

                if (infoTitle != null)
                {
                    infoTitle.Text = customPin.Label;
                }
                if (infoAddress != null)
                {
                    infoAddress.Text = customPin.Address;
                }
                if (imageFrame != null)
                {
                    if (customPin.userPhoto.Length == 0)
                    {
                        imageFrame.SetImageResource(Resource.Drawable.donationIcon);
                    }
                    else
                    {
                        var imageBytes = Convert.FromBase64String(customPin.userPhoto);
                        var imageData = BitmapFactory.DecodeByteArray(imageBytes, 0, imageBytes.Length);
                        imageFrame.SetImageBitmap(imageData);
                    }
                }
                if (organFrame != null)
                {
                    var organs = customPin.Url.Split(',');
                    foreach (var organ in organs)
                    {
                        var organImage = new ImageView(Context.ApplicationContext);
                        switch (organ)
                        {
                            case "bone_icon.png":
                                organImage.SetImageResource(Resource.Drawable.bone_icon);
                                break;
                            case "ear_icon.png":
                                organImage.SetImageResource(Resource.Drawable.ear_icon);
                                break;
                            case "cornea_icon.png":
                                organImage.SetImageResource(Resource.Drawable.eye_icon);
                                break;
                            case "heart_icon.png":
                                organImage.SetImageResource(Resource.Drawable.heart_icon);
                                break;
                            case "intestine_icon.png":
                                organImage.SetImageResource(Resource.Drawable.intestines_icon);
                                break;
                            case "kidney_icon.png":
                                organImage.SetImageResource(Resource.Drawable.kidney_icon);
                                break;
                            case "liver_icon.png":
                                organImage.SetImageResource(Resource.Drawable.liver_icon);
                                break;
                            case "lung_icon.png":
                                organImage.SetImageResource(Resource.Drawable.lungs_icon);
                                break;
                            case "pancreas_icon.png":
                                organImage.SetImageResource(Resource.Drawable.pancreas_icon);
                                break;
                            case "skin_icon.png":
                                organImage.SetImageResource(Resource.Drawable.skin_icon);
                                break;
                            case "tissue_icon.png":
                                organImage.SetImageResource(Resource.Drawable.tissue_icon);
                                break;
                        }
                        organImage.SetAdjustViewBounds(true);
                        organImage.SetMaxHeight(80);
                        organImage.SetMaxWidth(80);
                        organImage.SetPadding(5, 0, 5, 0);
                        organFrame.AddView(organImage);
                    }
                }


                return view;
            }
            return null;
        }

        public Android.Views.View GetInfoWindow(Marker marker)
        {
            return null;
        }

        /// <summary>
        /// Gets custom pin based on a marker
        /// </summary>
        /// <param name="annotation"></param>
        /// <returns></returns>
        CustomPin GetCustomPin(Marker annotation)
        {
            Position key = new Position(annotation.Position.Latitude, annotation.Position.Longitude);
            // Search custom pins
            if (customPins.TryGetValue(key, out CustomPin foundPin))
            {
                return foundPin;
            }

            // Search helicopter pins
            if (helicopterPins.TryGetValue(annotation.Snippet, out foundPin))
            {
                return foundPin;
            }

            return null;
        }

        /// <summary>
        /// Gets custom pin based on pin
        /// </summary>
        /// <param name="pin"></param>
        /// <returns></returns>
        CustomPin GetCustomPin(Pin pin)
        {
            // Search custom pins
            if (customPins.TryGetValue(pin.Position, out CustomPin foundPin))
            {
                return foundPin;
            }

            // Search helicopter pins
            if (helicopterPins.TryGetValue(pin.Address, out foundPin))
            {
                return foundPin;
            }
            return null;
        }
    }
}