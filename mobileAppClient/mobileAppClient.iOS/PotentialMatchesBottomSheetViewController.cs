using System;
using System.Collections.Generic;
using System.IO;
using System.Threading.Tasks;
using CoreGraphics;
using MapKit;
using mobileAppClient.Models;
using UIKit;
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

        public PotentialMatchesBottomSheetViewController(CustomPin pin, CustomMap map, MKMapView nativeMap, string organ) : base("PotentialMatchesBottomSheetViewController", null)
        {
            this.nativeMap = nativeMap;
            this.map = map;
            this.organName = organ;
            this.customPin = pin;
            holdView = new UIView();
            fullView = 400;
            partialView = UIScreen.MainScreen.Bounds.Height - (UIApplication.SharedApplication.StatusBarFrame.Height) - 60;

        }

        public void prepareSheetDetails()
        {
            organNameLabel.Text = "Organ: " + char.ToUpper(organName[0]) + organName.Substring(1);
            //SET THE TEXT DETAIL TO BE THE COUNTDOWN
            timeRemainingLabel.Text = "INSERT COUNTDOWN HERE";
            //Change colour based on severity
            timeRemainingLabel.TextColor = UIColor.Red;
            organImageView.Image = UIImage.FromFile(organName + "_icon.png");

            //DO API CALL HERE TO RETRIEVE ALL RECIPIENTS

            List<string> tableItems = new List<string> { "Vegetables", "Fruits", "Flower Buds", "Legumes", "Bulbs", "Tubers" };
            potentialRecipientsTableView.Source = new RecipientsTableSource(tableItems, this);

        }

        void BackButton_TouchUpInside(object sender, EventArgs e)
        {
            var window = UIApplication.SharedApplication.KeyWindow;
            var bottomSheetVC = new BottomSheetViewController(customPin, map, nativeMap);

            var rootVC = window.RootViewController;

            this.View.RemoveFromSuperview();
            this.View.Dispose();
            this.View = null;
            this.RemoveFromParentViewController();

            rootVC.AddChildViewController(bottomSheetVC);
            rootVC.View.AddSubview(bottomSheetVC.View);
            bottomSheetVC.DidMoveToParentViewController(rootVC);

            var height = window.Frame.Height;
            var width = window.Frame.Width;
            bottomSheetVC.View.Frame = new CGRect(0, window.Frame.GetMaxY(), width, height);

        }

        public async Task closeMenu()
        {
            await UIView.AnimateAsync(0.5, new Action(() => {
                var frame = this.View.Frame;
                var yComponent = UIScreen.MainScreen.Bounds.Height;
                this.View.Frame = new CGRect(frame.X, yComponent, frame.Width, frame.Height);
            }));
        }

        void prepareRecipientsOnMap() {
            //TODO CENTER THE MAP ON THE CIRCLE
            map.Circle = new CustomCircle
            {
                Position = new Position(-41.626217, 172.361873),
                Radius = 10000
            };
            var circleOverlay = MKCircle.Circle(new CoreLocation.CLLocationCoordinate2D(map.Circle.Position.Latitude, map.Circle.Position.Longitude), map.Circle.Radius);
            nativeMap.AddOverlay(circleOverlay);
            //var bytes = File.ReadAllBytes("donationIcon.png");
            //var profilePhoto = Convert.ToBase64String(bytes);
            //var pin = new CustomPin
            //{
            //    CustomType = ODMSPinType.DONOR,
            //    Position = new Position(-41.626217, 172.361873),
            //    Label = "TEST",
            //    Address = "TEST",
            //    Url = "700",
            //    genderIcon = "other.png",
            //    userPhoto = profilePhoto,
            //    userId = 700
            //};
            //map.CustomPins.Add(pin.Position, pin);
            //map.Pins.Add(pin);

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
            prepareRecipientsOnMap();

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

