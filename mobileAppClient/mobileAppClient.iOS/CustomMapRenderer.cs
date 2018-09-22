using System;
using System.Collections.Generic;
using CoreGraphics;
using CustomRenderer.iOS;
using Foundation;
using MapKit;
using mobileAppClient;
using UIKit;
using Xamarin.Forms;
using Xamarin.Forms.Maps;
using Xamarin.Forms.Maps.iOS;
using Xamarin.Forms.Platform.iOS;
using System.Linq;
using mobileAppClient.iOS;
using mobileAppClient.Views.Clinician;
using mobileAppClient.Models;
using ObjCRuntime;

[assembly: ExportRenderer(typeof(CustomMap), typeof(CustomMapRenderer))]
namespace CustomRenderer.iOS
{
    public class CustomMapRenderer : MapRenderer
    {
        UIView customPinView;
        Dictionary<Position, CustomPin> customPins;
        Dictionary<String, CustomPin> helicopterPins;
        CustomMap formsMap;
        CustomPin currentPin;
        public MKCircleRenderer circleRenderer;
        MKMapView nativeMap;

        protected override void OnElementChanged(ElementChangedEventArgs<View> e)
        {
            base.OnElementChanged(e);

            if (e.OldElement != null)
            {
                var nativeMap = Control as MKMapView;
                if (nativeMap != null && nativeMap.Overlays != null)
                {
                    nativeMap.RemoveOverlays(nativeMap.Overlays);
                    nativeMap.OverlayRenderer = null;
                    circleRenderer = null;
                }
                nativeMap.GetViewForAnnotation = null;
                nativeMap.CalloutAccessoryControlTapped -= OnCalloutAccessoryControlTapped;
                nativeMap.DidSelectAnnotationView -= OnDidSelectAnnotationView;
                nativeMap.DidDeselectAnnotationView -= OnDidDeselectAnnotationView;
            }

            if (e.NewElement != null)
            {
                formsMap = (CustomMap)e.NewElement;
                nativeMap = Control as MKMapView;
                nativeMap.OverlayRenderer = GetOverlayRenderer;

                customPins = formsMap.CustomPins;
                helicopterPins = formsMap.HelicopterPins;

                nativeMap.GetViewForAnnotation = GetViewForAnnotation;
                nativeMap.CalloutAccessoryControlTapped += OnCalloutAccessoryControlTapped;
                nativeMap.DidSelectAnnotationView += OnDidSelectAnnotationView;
                nativeMap.DidDeselectAnnotationView += OnDidDeselectAnnotationView;

            }
        }

        public MKOverlayRenderer GetOverlayRenderer(MKMapView mapView, IMKOverlay overlayWrapper)
        {
            if (circleRenderer == null && !Equals(overlayWrapper, null))
            {
                var overlay = Runtime.GetNSObject(overlayWrapper.Handle) as IMKOverlay;
                circleRenderer = new MKCircleRenderer(overlay as MKCircle)
                {
                    FillColor = UIColor.Black,
                    Alpha = 0.4f
                };
            }
            return circleRenderer;
        }

        protected override MKAnnotationView GetViewForAnnotation(MKMapView mapView, IMKAnnotation annotation)
        {
            MKAnnotationView annotationView = null;

            if (annotation is MKUserLocation)
                return null;

            var customPin = GetCustomPin(annotation as MKPointAnnotation);
            if (customPin == null)
            {
                return null;
            }

            
            annotationView = mapView.DequeueReusableAnnotation(customPin.Id.ToString());
            if (annotationView == null)
            {
                switch (customPin.CustomType)
                {
                    case ODMSPinType.DONOR:
                        annotationView = CreateDonorPin(annotationView, customPin, annotation);
                        break;
                    case ODMSPinType.HOSPITAL:
                        annotationView = CreateHospitalPin(annotationView, customPin, annotation);
                        break;
                    case ODMSPinType.HELICOPTER:
                        annotationView = CreateHelicopterPin(annotationView, customPin, annotation);
                        break;
                    case ODMSPinType.RECEIVER:
                        annotationView = CreateReceiverPin(annotationView, customPin, annotation);
                        break;
                }


            }
            annotationView.CanShowCallout = true;

            return annotationView;
        }

        private MKAnnotationView CreateHelicopterPin(MKAnnotationView annotationView, CustomPin customPin, IMKAnnotation annotation) {
            annotationView = new CustomMKAnnotationView(annotation, customPin.Id.ToString());
            annotationView.Image = UIImage.FromFile("helicopter_icon.png").Scale(new CGSize(45, 45));
            annotationView.CalloutOffset = new CGPoint(0, 0);
            ((CustomMKAnnotationView)annotationView).Id = customPin.Id.ToString();
            ((CustomMKAnnotationView)annotationView).Url = customPin.Url;
            return annotationView;
        }

        private MKAnnotationView CreateHospitalPin(MKAnnotationView annotationView, CustomPin customPin, IMKAnnotation annotation) {
            annotationView = new CustomMKAnnotationView(annotation, customPin.Id.ToString());
            annotationView.Image = UIImage.FromFile("hospital_icon.png").Scale(new CGSize(45, 45));
            annotationView.CalloutOffset = new CGPoint(0, 0);
            ((CustomMKAnnotationView)annotationView).Id = customPin.Id.ToString();
            ((CustomMKAnnotationView)annotationView).Url = customPin.Url;
            return annotationView;
        }

        private MKAnnotationView CreateDonorPin(MKAnnotationView annotationView, CustomPin customPin, IMKAnnotation annotation) {
            annotationView = new CustomMKAnnotationView(annotation, customPin.Id.ToString());
            annotationView.Image = UIImage.FromFile(customPin.genderIcon).Scale(new CGSize(70, 70));
            annotationView.CalloutOffset = new CGPoint(0, 0);
            //Set image to profile photo

            var imageBytes = Convert.FromBase64String(customPin.userPhoto);
            var imageData = NSData.FromArray(imageBytes);

            annotationView.LeftCalloutAccessoryView = new UIImageView(UIImage.LoadFromData(imageData).Scale(new CGSize(40, 40)));
            annotationView.RightCalloutAccessoryView = UIButton.FromType(UIButtonType.DetailDisclosure);
            ((CustomMKAnnotationView)annotationView).Id = customPin.Id.ToString();
            ((CustomMKAnnotationView)annotationView).Url = customPin.Url;
            return annotationView;
        }

        private MKAnnotationView CreateReceiverPin(MKAnnotationView annotationView, CustomPin customPin, IMKAnnotation annotation)
        {
            annotationView = new CustomMKAnnotationView(annotation, customPin.Id.ToString());
            annotationView.Image = UIImage.FromFile(customPin.genderIcon).Scale(new CGSize(70, 70));
            annotationView.CalloutOffset = new CGPoint(0, 0);
            //Set image to profile photo

            var imageBytes = Convert.FromBase64String(customPin.userPhoto);
            var imageData = NSData.FromArray(imageBytes);

            annotationView.LeftCalloutAccessoryView = new UIImageView(UIImage.LoadFromData(imageData).Scale(new CGSize(40, 40)));
            ((CustomMKAnnotationView)annotationView).Id = customPin.Id.ToString();
            ((CustomMKAnnotationView)annotationView).Url = customPin.Url;
            //annotationView.Set(true, true);
            return annotationView;
        }

        async void OnCalloutAccessoryControlTapped(object sender, MKMapViewAccessoryTappedEventArgs e)
        {

            //Display Alert 
            //Go back to Mobile App Clent and call show dialog event
            var customView = e.View as CustomMKAnnotationView;
            ClinicianMapPage parent = (ClinicianMapPage)formsMap.Parent.Parent;
            parent.displayUserDialog(customView.Url, customView.Url[customView.Url.Length - 1].ToString());


        }

        async void OnDidSelectAnnotationView(object sender, MKAnnotationViewEventArgs e)
        {

            var customView = e.View as CustomMKAnnotationView;
            customPinView = new UIView();

            if(customView.Url == null) {
                return;
            }

            string[] organs = customView.Url.Split(',');
            int userId = 0;
            if (!organs[0].Equals("ReceiverURL"))
            {

                // Set size of frame and add all photos from the custom pin image
                // Will probably have to redo how we use the url, im thinking a custom object that packs and unpacks the url string of all sorts of values we need (like a json)

                userId = Int32.Parse(organs[organs.Length - 1]);

                organs = organs.Take(organs.Length - 1).ToArray();
                int rectangleWidthInt = (organs.Length * 40) + (5 * (organs.Length + 1));

                customPinView.Frame = new CGRect(0, 0, rectangleWidthInt, 50);
                customPinView.BackgroundColor = UIColor.White;
                customPinView.Layer.CornerRadius = 5;
                customPinView.Layer.MasksToBounds = true;

                int i = 0;
                foreach (string organ in organs)
                {
                    int horizontalPosition = 5 + (45 * i);
                    var image = new UIImageView(new CGRect(horizontalPosition, 5, 40, 40));
                    image.Image = UIImage.FromFile(organ);
                    customPinView.AddSubview(image);
                    i++;
                }

                customPinView.Center = new CGPoint(0, -(e.View.Frame.Height + 20));


                e.View.AddSubview(customPinView);
            } else {
                userId = Int32.Parse(organs[1]);
            }


            // Do a search to get the current custom pin (gets the first)
            //currentPin = customPins.Values.First();
            foreach (CustomPin item in customPins.Values)
            {
                if (item.userId == userId)
                {
                    currentPin = item;
                }
            }

            //ClinicianMapPage parent = (ClinicianMapPage)formsMap.Parent.Parent;
            //parent.displayBottomSheet(currentPin, formsMap, nativeMap);

            if(currentPin.CustomType == ODMSPinType.DONOR) {
                //Remove all overlays on map
                removeOverlays();

                MapEntryClass mapEntryClass = new MapEntryClass();
                await mapEntryClass.addSlideUpSheet(currentPin, formsMap, nativeMap, this);
            } 



        }

        async void OnDidDeselectAnnotationView(object sender, MKAnnotationViewEventArgs e)
        {
            if (!e.View.Selected)
            {
            
                customPinView.RemoveFromSuperview();
                customPinView.Dispose();
                customPinView = null;

                //Handle Helicopters and Hospitals
                var customView = e.View as CustomMKAnnotationView;
                if (customView.Url == null)
                {
                    return;
                }

                removeOverlays();

                ClearAllReceivers();


                var window = UIApplication.SharedApplication.KeyWindow;
                var rootVC = window.RootViewController;
                if (rootVC is BottomSheetViewController)
                {
                    BottomSheetViewController bottomSheet = (BottomSheetViewController)rootVC;
                    await bottomSheet.closeMenu();
                    bottomSheet = null;
                    rootVC = null;
                }
                else
                {
                    PotentialMatchesBottomSheetViewController matchesSheet = (PotentialMatchesBottomSheetViewController)rootVC;
                    await matchesSheet.closeMenu();
                    matchesSheet = null;
                    rootVC = null;
                }

            } 
        }

        public void removeOverlays() {
            if(nativeMap.Overlays != null && nativeMap.Overlays.Length > 0) {
                nativeMap.Overlays[0].Dispose();
                nativeMap.RemoveOverlay(nativeMap.Overlays[0]);

            }

        }

        public void ClearAllReceivers() {
            foreach(CustomPin item in customPins.Values) {
                if(item.CustomType == ODMSPinType.RECEIVER) {
                    formsMap.Pins.Remove(item);
                }
            }
        }

        CustomPin GetCustomPin(MKPointAnnotation annotation)
        {
            Position key = new Position(annotation.Coordinate.Latitude, annotation.Coordinate.Longitude);

            //Search Donor Pins
            if (customPins.TryGetValue(key, out CustomPin foundPin))
            {
                return foundPin;
            }
            Console.WriteLine(annotation.Subtitle);
            // Search helicopter pins
            if (helicopterPins.TryGetValue(annotation.Subtitle, out foundPin))
            {
                return foundPin;
            }
            return null;
        }
    }
}