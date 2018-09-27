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
    /*
     * Renderer for the custom map
     */ 
    public class CustomMapRenderer : MapRenderer
    {
        UIView customPinView;
        Dictionary<Position, CustomPin> customPins;
        Dictionary<string, CustomPin> helicopterPins;
        CustomMap formsMap;
        CustomPin currentPin;
        public MKCircleRenderer circleRenderer;
        public MKPolylineRenderer polylineRenderer;
        MKMapView nativeMap;

        private String selectedHeli;
        private Tuple<CustomPin, MKPolyline> highlightedFlightPath;
        private Tuple<CustomPin, MKCircle> highlightedOrganRange;

        private Dictionary<Organ, string> helicopterIcons;


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
                    polylineRenderer = null;
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

                intialiseHelicopterIcons();

                highlightedFlightPath = new Tuple<CustomPin, MKPolyline>(null, null);
                highlightedOrganRange = new Tuple<CustomPin, MKCircle>(null, null);

                nativeMap.GetViewForAnnotation = GetViewForAnnotation;
                nativeMap.CalloutAccessoryControlTapped += OnCalloutAccessoryControlTapped;
                nativeMap.DidSelectAnnotationView += OnDidSelectAnnotationView;
                nativeMap.DidDeselectAnnotationView += OnDidDeselectAnnotationView;


            }


        }

        /*
         * Gets the new overlay renderer for the lines and circles
         */ 
        public MKOverlayRenderer GetOverlayRenderer(MKMapView mapView, IMKOverlay overlayWrapper)
        {

            var overlay = Runtime.GetNSObject(overlayWrapper.Handle) as IMKOverlay;
            if(overlay.GetType() == typeof(MKPolyline)) {
                if (polylineRenderer == null && !Equals(overlayWrapper, null))
                {

                    polylineRenderer = new MKPolylineRenderer(overlay as MKPolyline)
                    {
                        StrokeColor = UIColor.Blue,
                        LineWidth = (System.nfloat)2.0,
                        Alpha = 0.4f,

                    };
                }

                return polylineRenderer;
            } else {
                if (circleRenderer == null && !Equals(overlayWrapper, null))
                {
                    circleRenderer = new MKCircleRenderer(overlay as MKCircle)
                    {
                        FillColor = UIColor.Black,
                        Alpha = 0.4f,
                    };
                }
                return circleRenderer;
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
                Console.WriteLine("Pin added incorrectly! KNOWN BUG");
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

        /// <summary>
        /// Initialises a dictionary used to lookup helicopter icons based on an organ enum
        /// </summary>
        public void intialiseHelicopterIcons()
        {
            helicopterIcons = new Dictionary<Organ, string>();
            helicopterIcons.Add(Organ.LIVER, "helicopter_liver_icon.png");
            helicopterIcons.Add(Organ.KIDNEY, "helicopter_kidney_icon.png");
            helicopterIcons.Add(Organ.PANCREAS, "helicopter_pancreas_icon.png");
            helicopterIcons.Add(Organ.HEART, "helicopter_heart_icon.png");
            helicopterIcons.Add(Organ.LUNG, "helicopter_lung_icon.png");
            helicopterIcons.Add(Organ.INTESTINE, "helicopter_intestine_icon.png");
            helicopterIcons.Add(Organ.CORNEA, "helicopter_cornea_icon.png");
            helicopterIcons.Add(Organ.EAR, "helicopter_ear_icon.png");
            helicopterIcons.Add(Organ.SKIN, "helicopter_skin_icon.png");
            helicopterIcons.Add(Organ.BONE, "helicopter_bone_icon.png");
            helicopterIcons.Add(Organ.TISSUE, "helicopter_tissue_icon.png");
        }

        /*
         * Creates a new helicopter pin
         */ 
        private MKAnnotationView CreateHelicopterPin(MKAnnotationView annotationView, CustomPin customPin, IMKAnnotation annotation) {

            refreshFlightDetails(customPin);

            annotationView = new CustomMKAnnotationView(annotation, customPin.Id.ToString());
            annotationView.Image = UIImage.FromFile(helicopterIcons[customPin.OrganToTransport]).Scale(new CGSize(45, 45));
            annotationView.CalloutOffset = new CGPoint(0, 0);
            ((CustomMKAnnotationView)annotationView).Id = customPin.Id.ToString();
            ((CustomMKAnnotationView)annotationView).Url = customPin.Url;
            return annotationView;
        }

        /*
         * Creates a new Hospital pin
         */
        private MKAnnotationView CreateHospitalPin(MKAnnotationView annotationView, CustomPin customPin, IMKAnnotation annotation) {
            annotationView = new CustomMKAnnotationView(annotation, customPin.Id.ToString());
            annotationView.Image = UIImage.FromFile("hospital_icon.png").Scale(new CGSize(45, 45));
            annotationView.CalloutOffset = new CGPoint(0, 0);
            ((CustomMKAnnotationView)annotationView).Id = customPin.Id.ToString();
            ((CustomMKAnnotationView)annotationView).Url = customPin.Url;
            return annotationView;
        }

        /*
         * Creates a donor pin
         */ 
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

        /*
         * Creates a receiver pin
         */ 
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

        /*
         * Event when the callout is called
         */ 
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

            //Helicopter or Hospital
            if(customView.Url == null) {
                return;
            }

            string[] customViewUrl = customView.Url.Split(',');
            int userId = 0;
            if(customViewUrl[0].Equals("Heli")) {
                CustomPin helicopterPin = helicopterPins[customViewUrl[1]];
                processHelicopterTapped(helicopterPin);
                return;

            }
            else if (!customViewUrl[0].Equals("ReceiverURL"))
            {

                // Set size of frame and add all photos from the custom pin image
                // Will probably have to redo how we use the url, im thinking a custom object that packs and unpacks the url string of all sorts of values we need (like a json)

                userId = Int32.Parse(customViewUrl[customViewUrl.Length - 1]);

                customViewUrl = customViewUrl.Take(customViewUrl.Length - 1).ToArray();
                int rectangleWidthInt = (customViewUrl.Length * 40) + (5 * (customViewUrl.Length + 1));

                customPinView.Frame = new CGRect(0, 0, rectangleWidthInt, 50);
                customPinView.BackgroundColor = UIColor.White;
                customPinView.Layer.CornerRadius = 5;
                customPinView.Layer.MasksToBounds = true;

                int i = 0;
                foreach (string organ in customViewUrl)
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
                userId = Int32.Parse(customViewUrl[1]);
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

                //Handle Helicopters and Hospitals
                var customView = e.View as CustomMKAnnotationView;
                if (customView.Url == null)
                {
                    return;
                }

                string[] customViewUrl = customView.Url.Split(',');
                if (customViewUrl[0].Equals("Heli"))
                {
                    return;
                }

                customPinView.RemoveFromSuperview();
                customPinView.Dispose();
                customPinView = null;

                removeOverlays();
                ClearAllReceivers();


                var window = UIApplication.SharedApplication.KeyWindow;
                var rootVC = window.RootViewController;
                if (rootVC is BottomSheetViewController)
                {
                    BottomSheetViewController bottomSheet = (BottomSheetViewController)rootVC;
                    await bottomSheet.closeMenu();
                    bottomSheet.StopTimers();
                    bottomSheet.Dispose();
                    bottomSheet = null;
                    window.RootViewController = rootVC.ChildViewControllers[0];

                }
                else
                {
                    PotentialMatchesBottomSheetViewController matchesSheet = (PotentialMatchesBottomSheetViewController)rootVC;
                    await matchesSheet.closeMenu();
                    matchesSheet.StopTimers();
                    matchesSheet.Dispose();
                    matchesSheet = null;
                    window.RootViewController = rootVC.ChildViewControllers[0];
                }

            }
        }

        public void removeOverlays() {
            if(nativeMap.Overlays != null && nativeMap.Overlays.Length > 0) {
                nativeMap.RemoveOverlay(nativeMap.Overlays[0]);
                this.circleRenderer = null;
                nativeMap.OverlayRenderer = this.GetOverlayRenderer;

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
            //Console.WriteLine(annotation.Subtitle);
            // Search helicopter pins
            helicopterPins = formsMap.HelicopterPins;
            if (helicopterPins.TryGetValue(annotation.Subtitle, out foundPin))
            {
                return foundPin;
            }
            return null;
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

            CoreLocation.CLLocationCoordinate2D[] cLLocationCoordinate2Ds = new CoreLocation.CLLocationCoordinate2D[2];
            cLLocationCoordinate2Ds[0] = new CoreLocation.CLLocationCoordinate2D(heliPin.Position.Latitude, heliPin.Position.Longitude);
            cLLocationCoordinate2Ds[1] = new CoreLocation.CLLocationCoordinate2D(heliPin.HelicopterDetails.destinationPosition.Latitude,
                                                                                 heliPin.HelicopterDetails.destinationPosition.Longitude);

            var currentFlightPathOptions = MKPolyline.FromCoordinates(cLLocationCoordinate2Ds);

            nativeMap.AddOverlay(currentFlightPathOptions);

            highlightedFlightPath = new Tuple<CustomPin, MKPolyline>(heliPin, currentFlightPathOptions);
        }

        /// <summary>
        /// Adds a bubble that reflects the expiry range of a helicopter carrying organ
        /// </summary>
        /// <param name="heliPin"></param>
        private void addOrganRange(CustomPin heliPin)
        {
            //TODO Add proper radius
            var circleOptions = MKCircle.Circle(
                new CoreLocation.CLLocationCoordinate2D(heliPin.Position.Latitude, heliPin.Position.Longitude), 40000);

            nativeMap.AddOverlay(circleOptions);

            highlightedOrganRange = new Tuple<CustomPin, MKCircle>(heliPin, circleOptions);
        }

        /// <summary>
        /// Clears the highlighted flight path
        /// </summary>
        private void clearFlightPath()
        {
            if(!(highlightedFlightPath.Item2 == null)) {
                nativeMap.RemoveOverlay(highlightedFlightPath.Item2);
                this.polylineRenderer = null;
                nativeMap.OverlayRenderer = this.GetOverlayRenderer;
            }
            highlightedFlightPath = new Tuple<CustomPin, MKPolyline>(null, null);

        }

        /// <summary>
        /// Clears the highlighted organ expiry range
        /// </summary>
        private void clearOrganRange()
        {
            if (!(highlightedOrganRange.Item2 == null))
            {
                nativeMap.RemoveOverlay(highlightedOrganRange.Item2);
                this.circleRenderer = null;
                nativeMap.OverlayRenderer = this.GetOverlayRenderer;
            }

            highlightedOrganRange = new Tuple<CustomPin, MKCircle>(null, null);
        }
    }
}