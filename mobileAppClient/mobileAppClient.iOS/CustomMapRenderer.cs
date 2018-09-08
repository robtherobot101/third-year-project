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
using ObjCRuntime;
using mobileAppClient.iOS;
using mobileAppClient.Views.Clinician;

[assembly: ExportRenderer(typeof(CustomMap), typeof(CustomMapRenderer))]
namespace CustomRenderer.iOS
{
    public class CustomMapRenderer : MapRenderer
    {
        UIView customPinView;
        Dictionary<Position, CustomPin> customPins;
        CustomMap formsMap;
        CustomPin currentPin;
        MKCircleRenderer circleRenderer;
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
                //var circle = formsMap.Circle;

                nativeMap.OverlayRenderer = GetOverlayRenderer;

                //if(circle != null) {
                //    var circleOverlay = MKCircle.Circle(new CoreLocation.CLLocationCoordinate2D(circle.Position.Latitude, circle.Position.Longitude), circle.Radius);
                //    nativeMap.AddOverlay(circleOverlay);
                //}


                customPins = formsMap.CustomPins;

                nativeMap.GetViewForAnnotation = GetViewForAnnotation;
                nativeMap.CalloutAccessoryControlTapped += OnCalloutAccessoryControlTapped;
                nativeMap.DidSelectAnnotationView += OnDidSelectAnnotationView;
                nativeMap.DidDeselectAnnotationView += OnDidDeselectAnnotationView;

            }
        }

        MKOverlayRenderer GetOverlayRenderer(MKMapView mapView, IMKOverlay overlayWrapper)
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
                throw new Exception("Custom pin not found");
            }

            
            annotationView = mapView.DequeueReusableAnnotation(customPin.Id.ToString());
            if (annotationView == null)
            {

                annotationView = new CustomMKAnnotationView(annotation, customPin.Id.ToString());
                annotationView.Image = UIImage.FromFile(customPin.genderIcon).Scale(new CGSize(70,70));
                annotationView.CalloutOffset = new CGPoint(0, 0);
                //Set image to profile photo

                var imageBytes = Convert.FromBase64String(customPin.userPhoto);
                var imageData = NSData.FromArray(imageBytes);

                annotationView.LeftCalloutAccessoryView = new UIImageView(UIImage.LoadFromData(imageData).Scale(new CGSize(40, 40)));
                annotationView.RightCalloutAccessoryView = UIButton.FromType(UIButtonType.DetailDisclosure);
                ((CustomMKAnnotationView)annotationView).Id = customPin.Id.ToString();
                ((CustomMKAnnotationView)annotationView).Url = customPin.Url;
            }
            annotationView.CanShowCallout = true;

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

        void OnDidSelectAnnotationView(object sender, MKAnnotationViewEventArgs e)
        {
            var customView = e.View as CustomMKAnnotationView;
            customPinView = new UIView();

            // Set size of frame and add all photos from the custom pin image
            // Will probably have to redo how we use the url, im thinking a custom object that packs and unpacks the url string of all sorts of values we need (like a json)
            string[] organs = customView.Url.Split(',');
            int userId = Int32.Parse(organs[organs.Length - 1]);

            organs = organs.Take(organs.Length - 1).ToArray();
            int rectangleWidthInt = (organs.Length * 40) + (5 * (organs.Length + 1));

            customPinView.Frame = new CGRect(0, 0, rectangleWidthInt, 50);
            customPinView.BackgroundColor = UIColor.White;
            customPinView.Layer.CornerRadius = 5;
            customPinView.Layer.MasksToBounds = true;

            int i = 0;
            foreach(string organ in organs) {
                int horizontalPosition = 5 + (45 * i);
                var image = new UIImageView(new CGRect(horizontalPosition, 5, 40, 40));
                image.Image = UIImage.FromFile(organ);
                customPinView.AddSubview(image);
                i++;
            }

            customPinView.Center = new CGPoint(0, -(e.View.Frame.Height + 20));


            e.View.AddSubview(customPinView);


            // Do a search to get the current custom pin (gets the first)
            //currentPin = customPins.Values.First();
            foreach (CustomPin item in customPins.Values)
            {
                if (item.userId == userId)
                {
                    currentPin = item;
                }
            }

            ClinicianMapPage parent = (ClinicianMapPage)formsMap.Parent.Parent;
            parent.displayBottomSheet(currentPin, formsMap, nativeMap);

        }

        async void OnDidDeselectAnnotationView(object sender, MKAnnotationViewEventArgs e)
        {
            if (!e.View.Selected)
            {
                customPinView.RemoveFromSuperview();
                customPinView.Dispose();
                customPinView = null;
                var window = UIApplication.SharedApplication.KeyWindow;
                var rootVC = window.RootViewController;
                var number = rootVC.ChildViewControllers;
                var menuPopUp = rootVC.ChildViewControllers[1];
                if(menuPopUp is BottomSheetViewController) {
                    BottomSheetViewController bottomSheet = (BottomSheetViewController)rootVC.ChildViewControllers[1];
                    await bottomSheet.closeMenu();
                    bottomSheet.View.RemoveFromSuperview();
                    bottomSheet.View.Dispose();
                    bottomSheet.View = null;
                    bottomSheet.RemoveFromParentViewController();
                } else {
                    PotentialMatchesBottomSheetViewController matchesSheet = (PotentialMatchesBottomSheetViewController)rootVC.ChildViewControllers[1];
                    await matchesSheet.closeMenu();
                    matchesSheet.View.RemoveFromSuperview();
                    matchesSheet.View.Dispose();
                    matchesSheet.View = null;
                    matchesSheet.RemoveFromParentViewController();
                }


            }
        }

        CustomPin GetCustomPin(MKPointAnnotation annotation)
        {
            Position key = new Position(annotation.Coordinate.Latitude, annotation.Coordinate.Longitude);
            if (customPins.TryGetValue(key, out CustomPin foundPin))
            {
                return foundPin;
            }
            return null;
        }
    }
}