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
using Android.Support.V7.App;
using Cocosw.BottomSheetActions;
using Android.Support.V4.Graphics.Drawable;
using Android.Support.V4.App;
using Android.App;
using SlideOverKit;
using FragmentManager = Android.Support.V4.App.FragmentManager;
using mobileAppClient.Views.Clinician;
using Cheesebaron.SlidingUpPanel;
using Resource = mobileAppClient.Droid.Resource;
using Plugin.CurrentActivity;

[assembly: ExportRenderer(typeof(CustomMap), typeof(CustomMapRenderer))]
namespace CustomRenderer.Droid
{

    public class CustomMapRenderer : MapRenderer, GoogleMap.IInfoWindowAdapter
    {
        private Dictionary<Position, CustomPin> customPins;
        private Dictionary<String, CustomPin> helicopterPins;
        CustomMap formsMap;
        private CustomMapRenderer customMapRenderer;

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

                Control.GetMapAsync(this);
            }
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
        /// Generates marker options for a pin that contains information about a hospital
        /// </summary>
        /// <param name="pin"></param>
        /// <returns></returns>
        private MarkerOptions CreateHelicopterMarker(CustomPin pin)
        {
            // Create basic options
            var marker = new MarkerOptions();
            marker.SetPosition(new LatLng(pin.Position.Latitude, pin.Position.Longitude));
            marker.SetTitle(pin.Label);

            // Create the image
            Bitmap imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.helicopter_icon);

            // Scale the image
            imageBitmap = Bitmap.CreateScaledBitmap(imageBitmap, 110, 110, false);

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

            Android.App.Activity mainActivity = CrossCurrentActivity.Current.Activity;
            var view = mainActivity.CurrentFocus;
            Android.Views.View root = view.RootView;
            Android.Views.View altRoot = mainActivity.Window.DecorView.FindViewById(Android.Resource.Id.Content);
            Android.Widget.RelativeLayout parent = view.Parent as Android.Widget.RelativeLayout;

            Intent intent = new Intent(mainActivity.BaseContext, typeof(BottomSheetListActivity));
            intent.PutExtra("name", customPin.Label);
            intent.PutExtra("address", customPin.Address);
            intent.PutExtra("profilePicture", customPin.userPhoto);
            intent.PutExtra("organs", customPin.Url);
            mainActivity.StartActivity(intent);


            // ClinicianMapPage parent = (ClinicianMapPage)formsMap.Parent.Parent;

            // parent.displayBottomSheet(customPin, formsMap); 

            // parent.displayUserDialog(customPin.Url, customPin.Url.Substring(customPin.Url.Length - 1));

            if (Android.App.Application.Context.GetSystemService(Context.LayoutInflaterService) is Android.Views.LayoutInflater inflater)
            {
                Android.Views.View detailsView;
                // detailsView.FindViewById<TextView>(Resource.Id.)

                //detailsView = inflater.Inflate(Resource.Layout.OrganTransfer, null);

                //var name = detailsView.FindViewById<TextView>(Resource.Id.User_Name);
                //var address = detailsView.FindViewById<TextView>(Resource.Id.Address);
                //var profilePicture = detailsView.FindViewById<ImageView>(Resource.Id.ProfilePictureFrame);

                //Android.App.Activity mainActivity = CrossCurrentActivity.Current.Activity;

                //var view = mainActivity.CurrentFocus;
                //Android.Views.View root = view.RootView;
                //Android.Views.View altRoot = mainActivity.Window.DecorView.FindViewById(Android.Resource.Id.Content);
                //Android.Widget.RelativeLayout parent = view.Parent as Android.Widget.RelativeLayout;



                //if (name != null)
                //{
                //    name.Text = customPin.Label;
                //}
                //if (address != null)
                //{
                //    address.Text = customPin.Address;
                //}
                //if (profilePicture != null)
                //{
                //    if (customPin.userPhoto.Length == 0)
                //    {
                //        profilePicture.SetImageResource(Resource.Drawable.donationIcon);
                //    }
                //    else
                //    {
                //        var imageBytes = Convert.FromBase64String(customPin.userPhoto);
                //        var imageData = BitmapFactory.DecodeByteArray(imageBytes, 0, imageBytes.Length);
                //        profilePicture.SetImageBitmap(imageData);
                //    }
                //}

                //parent.AddView(detailsView);
            }
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

                if (customPin.CustomType == ODMSPinType.HOSPITAL || customPin.CustomType == ODMSPinType.HELICOPTER)
                {
                    // Hospital pop-up dialog not yet implemented
                    return null;
                }

               // ClinicianMapPage parent = (ClinicianMapPage)formsMap.Parent.Parent;
                //parent.displayBottomSheet(customPin, formsMap);


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
                            case "eye_icon.png":
                                organImage.SetImageResource(Resource.Drawable.eye_icon);
                                break;
                            case "heart_icon.png":
                                organImage.SetImageResource(Resource.Drawable.heart_icon);
                                break;
                            case "intestines_icon.png":
                                organImage.SetImageResource(Resource.Drawable.intestines_icon);
                                break;
                            case "kidney_icon.png":
                                organImage.SetImageResource(Resource.Drawable.kidney_icon);
                                break;
                            case "liver_icon.png":
                                organImage.SetImageResource(Resource.Drawable.liver_icon);
                                break;
                            case "lungs_icon.png":
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
        /// NOT FOR HELICOPTERS!!!
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