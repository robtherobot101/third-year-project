using System;
using System.Threading;
using System.Threading.Tasks;
using CoreGraphics;
using CustomRenderer.iOS;
using Foundation;
using iAd;
using MapKit;
using ObjCRuntime;
using UIKit;
using Xamarin.Forms;

namespace mobileAppClient.iOS
{
    public partial class BottomSheetViewController : UIViewController
    {

        public nfloat fullView;
        public nfloat partialView;
        public CustomPin pin;
        public CustomMap map;
        public MKMapView nativeMap;
        public CustomMapRenderer customMapRenderer;
        public Timer OrganTimeTickingTimer;

        public BottomSheetViewController(CustomPin pin, CustomMap map, MKMapView nativeMap, CustomMapRenderer customMapRenderer) : base("BottomSheetViewController", null)
        {
            this.nativeMap = nativeMap;
            this.map = map;
            this.pin = pin;
            holdView = new UIView();
            fullView = 300;
            partialView = UIScreen.MainScreen.Bounds.Height - (UIApplication.SharedApplication.StatusBarFrame.Height) - 80;
            this.customMapRenderer = customMapRenderer;
        }

        public void prepareSheetDetails() {

            NameLabel.Text = pin.Label;
            AddressLabel.Text = pin.Address;
            var imageBytes = Convert.FromBase64String(pin.userPhoto);
            var imageData = NSData.FromArray(imageBytes);
            ProfilePhotoImageView.Image = UIImage.LoadFromData(imageData);

            OrgansTableView.Source = new TableSource(pin, map, nativeMap, this, customMapRenderer);
            OrgansTableView.ScrollEnabled = true;
        }

        public void StartTickingTimer(int interval) {
            OrganTimeTickingTimer = new Timer(RefreshCountdownsInTableView, null, 0, interval); 
        }

        public void RefreshCountdownsInTableView(object o) {
            Device.BeginInvokeOnMainThread(() =>
            {
                TableSource tableSource = (TableSource)OrgansTableView.Source;
                foreach (UITableViewCell cell in tableSource.Cells.Values)
                {
                    string detailString = cell.DetailTextLabel.Text;
                    if(detailString.Equals("EXPIRED")) {
                        continue;
                    } else if(detailString.Equals("IN TRANSIT")) {
                        continue;
                    } else if(detailString.Equals("SUCCESSFULLY TRANSFERRED")) {
                        continue;
                    } else {
                        string timeLeftString = detailString.Substring(16);
                        string timeString = timeLeftString.Remove(timeLeftString.Length - 5);

                        TimeSpan timeLeft = TimeSpan.Parse(timeString);
                        if (timeLeft.Equals(new TimeSpan(0, 0, 0)))
                        {
                            detailString = "EXPIRED";
                            cell.DetailTextLabel.TextColor = UIColor.Red;
                            //Update the Organ object to be expired
                        }
                        else
                        {
                            timeLeft = timeLeft.Subtract(new TimeSpan(0, 0, 1));
                            detailString = detailString.Substring(0, 16) + timeLeft.ToString(@"dd\:hh\:mm\:ss") + " days";

                        }
                        cell.DetailTextLabel.Text = detailString;
                    }


                }
            });


        }

        public async Task closeMenu() {
            await UIView.AnimateAsync(0.5, new Action(() => {
                var frame = this.View.Frame;
                var yComponent = UIScreen.MainScreen.Bounds.Height;
                this.View.Frame = new CGRect(frame.X, yComponent, frame.Width, frame.Height);
            }));
        }

        public void StopTimers()
        {
            OrganTimeTickingTimer.Dispose();
            OrganTimeTickingTimer = null;
        }


        public void prepareBackgroundView() {
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
            OpenMenu();

          
        }

        public async Task OpenMenu() {
            await UIView.AnimateAsync(0.3, new Action(() => {
                var frame = this.View.Frame;
                //var yComponent = UIScreen.MainScreen.Bounds.Height - 200;
                var yComponent = this.partialView;
                this.View.Frame = new CGRect(0, yComponent, frame.Width, frame.Height);
            }));
        }

        public override void DidReceiveMemoryWarning()
        {
            base.DidReceiveMemoryWarning();
            // Dispose of any resources that can be recreated.
        }

        public override void ViewDidLoad()
        {
            base.ViewDidLoad();

            var gesture = new UIPanGestureRecognizer(panGesture);
            View.AddGestureRecognizer(gesture);

            roundViews();

            prepareSheetDetails();

            StartTickingTimer(1000);

        }

        public void panGesture(UIPanGestureRecognizer recognizer) {
            var translation = recognizer.TranslationInView(this.View);
            var velocity = recognizer.VelocityInView(this.View);
            var y = this.View.Frame.GetMinY();

            if( (y + translation.Y >= fullView) && (y + translation.Y <= partialView)) {
                this.View.Frame = new CGRect(0, y + translation.Y, View.Frame.Width, View.Frame.Height);
                recognizer.SetTranslation(CGPoint.Empty, this.View);
            }

            if(recognizer.State == UIGestureRecognizerState.Ended) {
                var duration = velocity.Y < 0 ? (double)((y - fullView) / -velocity.Y) : (double)((partialView - y) / velocity.Y);

                duration = duration > 1.3 ? 1 : duration;

                UIView.Animate(duration, 0.0, UIViewAnimationOptions.AllowUserInteraction, () =>
                {
                    if(velocity.Y >= 0) {
                        this.View.Frame = new CGRect(0, this.partialView, this.View.Frame.Width, this.View.Frame.Height);
                    } else {
                        this.View.Frame = new CGRect(0, this.fullView, this.View.Frame.Width, this.View.Frame.Height);
                    }
                }, null);
            } 

        }

        public void roundViews() {
            View.Layer.CornerRadius = 5;
            holdView.Layer.CornerRadius = 3;
            View.ClipsToBounds = true;
        }

    }



}

