using System;
using System.Collections.Generic;
using Android.Content;
using Android.Gms.Maps;
using Android.Gms.Maps.Model;
using Android.Graphics;
using Android.Widget;
using CustomRenderer.Droid;
using mobileAppClient;
using mobileAppClient.Droid;
using Xamarin.Forms;
using Xamarin.Forms.Maps;
using Xamarin.Forms.Maps.Android;

[assembly: ExportRenderer(typeof(CustomMap), typeof(CustomMapRenderer))]
namespace CustomRenderer.Droid
{
    public class CustomMapRenderer : MapRenderer, GoogleMap.IInfoWindowAdapter
    {
        List<CustomPin> customPins;
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
                Control.GetMapAsync(this);
            }
        }

        protected override void OnMapReady(GoogleMap map)
        {
            base.OnMapReady(map);

            NativeMap.InfoWindowClick += OnInfoWindowClick;
            NativeMap.SetInfoWindowAdapter(this);
        }

        protected override MarkerOptions CreateMarker(Pin pin)
        {
            var marker = new MarkerOptions();
            marker.SetPosition(new LatLng(pin.Position.Latitude, pin.Position.Longitude));
            marker.SetTitle(pin.Label);
            marker.SetSnippet(pin.Address);
            Bitmap imageBitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.other);
            Bitmap resizedBitmap = Bitmap.CreateScaledBitmap(imageBitmap, 70, 70, false);
            marker.SetIcon(BitmapDescriptorFactory.FromBitmap(resizedBitmap));
            return marker;
        }

        void OnInfoWindowClick(object sender, GoogleMap.InfoWindowClickEventArgs e)
        {
            var customPin = GetCustomPin(e.Marker);
            if (customPin == null)
            {
                throw new Exception("Custom pin not found");
            }
            ClinicianMapPage parent = (ClinicianMapPage)formsMap.Parent.Parent;
            parent.displayUserDialog(customPin.Url, customPin.Url.Substring(customPin.Url.Length - 1));
        }

        public Android.Views.View GetInfoContents(Marker marker)
        {
            var inflater = Android.App.Application.Context.GetSystemService(Context.LayoutInflaterService) as Android.Views.LayoutInflater;
            if (inflater != null)
            {
                Android.Views.View view;

                var customPin = GetCustomPin(marker);
                if (customPin == null)
                {
                    throw new Exception("Custom pin not found");
                }


                view = inflater.Inflate(Resource.Layout.XamarinMapInfoWindow, null);


                var infoTitle = view.FindViewById<TextView>(Resource.Id.InfoWindowTitle);
                var infoSubtitle = view.FindViewById<TextView>(Resource.Id.InfoWindowSubtitle);
                var infoAddress = view.FindViewById<TextView>(Resource.Id.InfoWindowAddress);

                if (infoTitle != null)
                {
                    infoTitle.Text = customPin.Label;
                }
                if (infoSubtitle != null)
                {
                    infoSubtitle.Text = "Donating " + (customPin.Url.Split(',').Length - 1) + ((customPin.Url.Split(',').Length > 2 || customPin.Url.Split(',').Length == 0) ? " organs" : " organ");
                }
                if (infoAddress != null)
                {
                    infoAddress.Text = customPin.Address;
                }

                return view;
            }
            return null;
        }

        public Android.Views.View GetInfoWindow(Marker marker)
        {
            return null;
        }

        CustomPin GetCustomPin(Marker annotation)
        {
            var position = new Position(annotation.Position.Latitude, annotation.Position.Longitude);
            foreach (var pin in customPins)
            {
                if (pin.Position == position)
                {
                    return pin;
                }
            }
            return null;
        }
    }
}