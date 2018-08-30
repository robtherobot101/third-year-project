using System;
using CoreGraphics;
using Foundation;
using UIKit;

namespace mobileAppClient.iOS
{
    public partial class BottomSheetViewController : UIViewController
    {

        public nfloat fullView;
        public nfloat partialView;
        public CustomPin pin;
        

        public BottomSheetViewController(CustomPin pin) : base("BottomSheetViewController", null)
        {
            this.pin = pin;
            holdView = new UIView();
            fullView = 300;
            partialView = UIScreen.MainScreen.Bounds.Height - (UIApplication.SharedApplication.StatusBarFrame.Height) - 60;
        }

        public void prepareSheetDetails() {

            NameLabel.Text = pin.Label;
            AddressLabel.Text = pin.Address;
            var imageBytes = Convert.FromBase64String(pin.userPhoto);
            var imageData = NSData.FromArray(imageBytes);
            ProfilePhotoImageView.Image = UIImage.LoadFromData(imageData);

            //string[] tableItems = new string[] { "Vegetables", "Fruits", "Flower Buds", "Legumes", "Bulbs", "Tubers" };
            OrgansTableView.Source = new TableSource(pin, this);


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
            UIView.Animate(0.3, new Action(() => {
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


        public void rightButton() {
            Console.WriteLine("ooooo click me!");
        }

        public void close() {
            UIView.Animate(0.3, new Action(() => {
                var frame = this.View.Frame;
                this.View.Frame = new CGRect(0, this.partialView, frame.Width, frame.Height);
            }));
        }

        public override void ViewDidLoad()
        {
            base.ViewDidLoad();

            var gesture = new UIPanGestureRecognizer(panGesture);
            View.AddGestureRecognizer(gesture);

            roundViews();

            prepareSheetDetails();

            //OrgansTableView.Delegate = this;

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

