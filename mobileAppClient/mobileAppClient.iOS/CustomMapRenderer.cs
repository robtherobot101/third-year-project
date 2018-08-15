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


[assembly: ExportRenderer(typeof(CustomMap), typeof(CustomMapRenderer))]
namespace CustomRenderer.iOS
{
    public class CustomMapRenderer : MapRenderer
    {
        UIView customPinView;
        List<CustomPin> customPins;
        CustomMap formsMap;

        protected override void OnElementChanged(ElementChangedEventArgs<View> e)
        {
            base.OnElementChanged(e);

            if (e.OldElement != null)
            {
                var nativeMap = Control as MKMapView;
                nativeMap.GetViewForAnnotation = null;
                nativeMap.CalloutAccessoryControlTapped -= OnCalloutAccessoryControlTapped;
                nativeMap.DidSelectAnnotationView -= OnDidSelectAnnotationView;
                nativeMap.DidDeselectAnnotationView -= OnDidDeselectAnnotationView;
            }

            if (e.NewElement != null)
            {
                formsMap = (CustomMap)e.NewElement;
                var nativeMap = Control as MKMapView;
                customPins = formsMap.CustomPins;

                nativeMap.GetViewForAnnotation = GetViewForAnnotation;
                nativeMap.CalloutAccessoryControlTapped += OnCalloutAccessoryControlTapped;
                nativeMap.DidSelectAnnotationView += OnDidSelectAnnotationView;
                nativeMap.DidDeselectAnnotationView += OnDidDeselectAnnotationView;
            }
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
            parent.displayUserDialog(customView.Url, customView.Url.Substring(customView.Url.Length - 1));


        }

        void OnDidSelectAnnotationView(object sender, MKAnnotationViewEventArgs e)
        {
            var customView = e.View as CustomMKAnnotationView;
            customPinView = new UIView();

            //Set size of frame and add all photos from the custom pin image
            string[] organs = customView.Url.Split(',');
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
            
        }

        void OnDidDeselectAnnotationView(object sender, MKAnnotationViewEventArgs e)
        {
            if (!e.View.Selected)
            {
                customPinView.RemoveFromSuperview();
                customPinView.Dispose();
                customPinView = null;
            }
        }

        CustomPin GetCustomPin(MKPointAnnotation annotation)
        {
            var position = new Position(annotation.Coordinate.Latitude, annotation.Coordinate.Longitude);
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