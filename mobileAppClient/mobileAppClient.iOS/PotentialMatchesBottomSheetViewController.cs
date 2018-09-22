using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using CoreGraphics;
using CustomRenderer.iOS;
using Foundation;
using MapKit;
using mobileAppClient.Models;
using mobileAppClient.odmsAPI;
using UIKit;
using Xamarin.Forms;
using Xamarin.Forms.Maps;

namespace mobileAppClient.iOS
{
    public partial class PotentialMatchesBottomSheetViewController : UIViewController
    {
        public nfloat fullView;
        public nfloat partialView;
        public CustomPin customPin;
        public CustomMap map;
        public string organName;
        public MKMapView nativeMap;
        public UITableViewCell currentOrganCell;
        public double organTimeLeft;
        public CustomMapRenderer customMapRenderer;
        public DonatableOrgan currentOrgan;

        public PotentialMatchesBottomSheetViewController(CustomPin pin, CustomMap map, MKMapView nativeMap, 
                                                         string organ, UITableViewCell currentOrganCell, CustomMapRenderer customMapRenderer) : base("PotentialMatchesBottomSheetViewController", null)
        {
            this.nativeMap = nativeMap;
            this.map = map;
            this.organName = organ;
            this.customPin = pin;
            holdView = new UIView();
            fullView = 360;
            partialView = UIScreen.MainScreen.Bounds.Height - (UIApplication.SharedApplication.StatusBarFrame.Height) - 60;
            this.currentOrganCell = currentOrganCell;
            this.customMapRenderer = customMapRenderer;
            foreach (DonatableOrgan donatableOrgan in customPin.donatableOrgans)
            {
                if (donatableOrgan.organType.ToLower().Equals(organName.ToLower()))
                {
                    currentOrgan = donatableOrgan;
                }
            }

        }

        public void prepareSheetDetails()
        {
            organNameLabel.Text = "Organ: " + char.ToUpper(organName[0]) + organName.Substring(1);
            //SET THE TEXT DETAIL TO BE THE COUNTDOWN
            timeRemainingLabel.Text = currentOrganCell.DetailTextLabel.Text;
            //Change colour based on severity
            timeRemainingLabel.TextColor = currentOrganCell.DetailTextLabel.TextColor;
            organImageView.Image = UIImage.FromFile(organName + "_icon.png");
            if (currentOrganCell.DetailTextLabel.Text.Equals("EXPIRED"))
            {
                potentialRecipientsLabel.Text = "This organ has expired.";
                potentialRecipientsTableView.Hidden = true;
            }
            else if (currentOrgan.topReceivers.Count == 0) {
                potentialRecipientsLabel.Text = "No matched recipients found.";
                potentialRecipientsTableView.Hidden = true;
       
            } else {
                potentialRecipientsTableView.Hidden = false;
                potentialRecipientsLabel.Text = "Potential Recipients:";
                potentialRecipientsTableView.Source = new RecipientsTableSource(this, currentOrgan, map, customPin);
                potentialRecipientsTableView.ScrollEnabled = true;
            }


        }


        public void StartOrganTimeTickingTimer(int interval)
        {
            Timer timer = new Timer(RefreshCountdownsInTableView, null, 0, interval);
        }

        public void StartOrganCircleRadiusCountdown(int interval)
        {
            Timer timer2 = new Timer(RefreshOrganCircleOnMap, null, 0, interval);
        }

        public void RefreshCountdownsInTableView(object o)
        {
            Device.BeginInvokeOnMainThread(() =>
            {

                string detailString = timeRemainingLabel.Text;
                if (detailString.Equals("EXPIRED"))
                {
                    return;
                }
                else
                {
                    string timeLeftString = detailString.Substring(16);
                    string timeString = timeLeftString.Remove(timeLeftString.Length - 5);

                    TimeSpan timeLeft = TimeSpan.Parse(timeString);
                    if (timeLeft.Equals(new TimeSpan(0, 0, 0)))
                    {
                        detailString = "EXPIRED";
                        timeRemainingLabel.TextColor = UIColor.Red;
                        currentOrgan.expired = true;
                        potentialRecipientsLabel.Text = "This organ has expired.";
                        potentialRecipientsTableView.Hidden = true;
                        return;
                    }
                    else
                    {
                        timeLeft = timeLeft.Subtract(new TimeSpan(0, 0, 1));
                        detailString = detailString.Substring(0, 16) + timeLeft.ToString(@"dd\:hh\:mm\:ss") + " days";

                    }
                    timeRemainingLabel.Text = detailString;
                }

            });


        }

        void BackButton_TouchUpInside(object sender, EventArgs e)
        {
            var window = UIApplication.SharedApplication.KeyWindow;
            var bottomSheetVC = new BottomSheetViewController(customPin, map, nativeMap, customMapRenderer);

            window.RootViewController = bottomSheetVC;


        }

        public async Task closeMenu()
        {
            await UIView.AnimateAsync(0.5, new Action(() => {
                var frame = this.View.Frame;
                var yComponent = UIScreen.MainScreen.Bounds.Height;
                this.View.Frame = new CGRect(frame.X, yComponent, frame.Width, frame.Height);
            }));
        }

        public void RefreshOrganCircleOnMap(object o) {

            Device.BeginInvokeOnMainThread(() =>
            {
                organTimeLeft--;
                Console.WriteLine(organTimeLeft);

                if(organTimeLeft <= 0) {
                    return;
                } else
                {
                    double mapRadius;
                    double distanceTime = organTimeLeft * 70;

                    if (distanceTime > 500000)
                    {
                        mapRadius = 500000;
                    } else {
                        mapRadius = distanceTime;
                    }
                    Position currentPosition = customPin.Position;

                    map.Circle = new CustomCircle
                    {
                        Position = currentPosition,
                        Radius = mapRadius
                    };
                    var circleOverlay = MKCircle.Circle(new CoreLocation.CLLocationCoordinate2D(map.Circle.Position.Latitude, map.Circle.Position.Longitude), map.Circle.Radius);
                    if(nativeMap.Overlays == null || nativeMap.Overlays.Length == 0) {
                        return;
                    } else {
                        nativeMap.RemoveOverlay(nativeMap.Overlays[0]);

                        customMapRenderer.circleRenderer = null;
                        nativeMap.OverlayRenderer = customMapRenderer.GetOverlayRenderer;

                        nativeMap.AddOverlay(circleOverlay);
                    }

                }


            });
        }

        async void prepareRecipientsOnMap(Position position) {
            string detailString = timeRemainingLabel.Text;
            if (detailString.Equals("EXPIRED"))
            {
                return;
            }
            else
            {
                string timeLeftString = detailString.Substring(16);
                string timeString = timeLeftString.Remove(timeLeftString.Length - 5);
                TimeSpan timeLeft = TimeSpan.Parse(timeString);
                organTimeLeft = timeLeft.TotalSeconds;

                double distanceTime = organTimeLeft * 70;

                double mapRadius;
                if (organTimeLeft > 500000)
                {
                    mapRadius = 500000;
                }
                else 
                {
                    mapRadius = distanceTime;
                }

                map.Circle = new CustomCircle
                {
                    Position = position,
                    Radius = mapRadius
                };
                var circleOverlay = MKCircle.Circle(new CoreLocation.CLLocationCoordinate2D(map.Circle.Position.Latitude, map.Circle.Position.Longitude), map.Circle.Radius);
                nativeMap.AddOverlay(circleOverlay);
                Position mapCenter = new Position(position.Latitude - 1, position.Longitude);
                map.MoveToRegion(MapSpan.FromCenterAndRadius(
                    mapCenter, Distance.FromMiles(100.0)));

                //Add all other user objects around the user
                Random rnd = new Random();
                foreach (User item in currentOrgan.topReceivers) {

                    //SET GENDER ICON
                    //Randomize man or woman photo
                    //If other then set to a pin
                    //If none then also set to a pin

                    string genderIcon = "";
                    switch (item.gender)
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

                    //Find the position of the Pin
                    Geocoder geocoder = new Geocoder();
                    IEnumerable<Position> usersPosition = await geocoder.GetPositionsForAddressAsync(item?.currentAddress + ", " + item?.region + ", " + item?.country);
                    Position receiverPosition = new Position();
                    using (var sequenceEnum = usersPosition.GetEnumerator())
                    {
                        while (sequenceEnum.MoveNext())
                        {
                            receiverPosition = sequenceEnum.Current;
                        }
                    }

                    //double randomNumberLongitude = rnd.NextDouble() / 50.00;
                    //double randomNumberLatitude = rnd.NextDouble() / 50.00;
                    //Position finalPosition = new Position(organPosition.Latitude + randomNumberLatitude, organPosition.Longitude + randomNumberLongitude);

                    Position finalPosition = new Position(receiverPosition.Latitude, receiverPosition.Longitude);


                    //SET PROFILE PHOTO
                    //Get profile photo from users uploaded photo (if there is one)

                    UserAPI userAPI = new UserAPI();

                    string profilePhoto = "";
                    Tuple<HttpStatusCode, string> photoResponse = await userAPI.GetUserPhotoForMapObjects(item.id);
                    if (photoResponse.Item1 != HttpStatusCode.OK)
                    {
                        Console.WriteLine("Failed to retrieve profile photo or no profile photo found.");
                        Byte[] bytes = File.ReadAllBytes("donationIcon.png");
                        profilePhoto = Convert.ToBase64String(bytes);

                    }
                    else
                    {
                        profilePhoto = photoResponse.Item2;
                    }


                    var pin = new CustomPin
                    {
                        CustomType = ODMSPinType.RECEIVER,
                        Position = finalPosition,
                        Label = item.FullName,
                        Address = item?.currentAddress + ", " + item?.region,
                        Url = "ReceiverURL," + item.id.ToString(),
                        genderIcon = genderIcon,
                        userPhoto = profilePhoto,
                        userId = item.id
                    };
                    map.CustomPins.Add(pin.Position, pin);
                    map.Pins.Add(pin);
                }

            }

        }



        public void prepareBackgroundView()
        {
            var blurEffect = UIBlurEffect.FromStyle(UIBlurEffectStyle.Dark);
            var visualEffect = new UIVisualEffectView(blurEffect);
            var bluredView = new UIVisualEffectView(blurEffect);
            bluredView.ContentView.AddSubview(visualEffect);

            visualEffect.Frame = UIScreen.MainScreen.Bounds;
            bluredView.Frame = UIScreen.MainScreen.Bounds;

            View.InsertSubview(bluredView, 0);
        }

        public override void ViewWillAppear(bool animated)
        {
            base.ViewWillAppear(animated);
            prepareBackgroundView();
        }

        public override void ViewDidAppear(bool animated)
        {
            base.ViewDidAppear(animated);
            UIView.Animate(0.3, new Action(() => {
                var frame = this.View.Frame;
                this.View.Frame = new CGRect(0, 400, frame.Width, frame.Height);
            }));

        }

        public override void ViewDidLoad()
        {
            base.ViewDidLoad();
            // Perform any additional setup after loading the view, typically from a nib.

            var gesture = new UIPanGestureRecognizer(panGesture);
            View.AddGestureRecognizer(gesture);

            roundViews();

            prepareSheetDetails();
            backButton.TouchUpInside += BackButton_TouchUpInside;
            prepareRecipientsOnMap(customPin.Position);

            StartOrganTimeTickingTimer(1000);

            StartOrganCircleRadiusCountdown(2000);

        }

        public override void DidReceiveMemoryWarning()
        {
            base.DidReceiveMemoryWarning();
            // Release any cached data, images, etc that aren't in use.
        }

        public void panGesture(UIPanGestureRecognizer recognizer)
        {
            var translation = recognizer.TranslationInView(this.View);
            var velocity = recognizer.VelocityInView(this.View);
            var y = this.View.Frame.GetMinY();

            if ((y + translation.Y >= fullView) && (y + translation.Y <= partialView))
            {
                this.View.Frame = new CGRect(0, y + translation.Y, View.Frame.Width, View.Frame.Height);
                recognizer.SetTranslation(CGPoint.Empty, this.View);
            }

            if (recognizer.State == UIGestureRecognizerState.Ended)
            {
                var duration = velocity.Y < 0 ? (double)((y - fullView) / -velocity.Y) : (double)((partialView - y) / velocity.Y);

                duration = duration > 1.3 ? 1 : duration;

                UIView.Animate(duration, 0.0, UIViewAnimationOptions.AllowUserInteraction, () =>
                {
                    if (velocity.Y >= 0)
                    {
                        this.View.Frame = new CGRect(0, this.partialView, this.View.Frame.Width, this.View.Frame.Height);
                    }
                    else
                    {
                        this.View.Frame = new CGRect(0, this.fullView, this.View.Frame.Width, this.View.Frame.Height);
                    }
                }, null);
            }

        }

        public void roundViews()
        {
            View.Layer.CornerRadius = 5;
            holdView.Layer.CornerRadius = 3;
            View.ClipsToBounds = true;
        }
    }
}

