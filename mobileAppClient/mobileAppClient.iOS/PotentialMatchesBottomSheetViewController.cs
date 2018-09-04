using System;
using CoreGraphics;
using UIKit;

namespace mobileAppClient.iOS
{
    public partial class PotentialMatchesBottomSheetViewController : UIViewController
    {
        public nfloat fullView;
        public nfloat partialView;
        public CustomPin customPin;

        public PotentialMatchesBottomSheetViewController(CustomPin pin) : base("PotentialMatchesBottomSheetViewController", null)
        {
            this.customPin = pin;
            holdView = new UIView();
            fullView = 400;
            partialView = UIScreen.MainScreen.Bounds.Height - (UIApplication.SharedApplication.StatusBarFrame.Height) - 60;

        }

        public void prepareSheetDetails()
        {

            //NameLabel.Text = pin.Label;
            //AddressLabel.Text = pin.Address;
            //var imageBytes = Convert.FromBase64String(pin.userPhoto);
            //var imageData = NSData.FromArray(imageBytes);
            //ProfilePhotoImageView.Image = UIImage.LoadFromData(imageData);

            ////string[] tableItems = new string[] { "Vegetables", "Fruits", "Flower Buds", "Legumes", "Bulbs", "Tubers" };
            //OrgansTableView.Source = new TableSource(pin, this);

        }

        void BackButton_TouchUpInside(object sender, EventArgs e)
        {
            var window = UIApplication.SharedApplication.KeyWindow;
            var bottomSheetVC = new BottomSheetViewController(customPin);

            var rootVC = window.RootViewController;

            this.View.RemoveFromSuperview();

            rootVC.AddChildViewController(bottomSheetVC);
            rootVC.View.AddSubview(bottomSheetVC.View);
            bottomSheetVC.DidMoveToParentViewController(rootVC);

            var height = window.Frame.Height;
            var width = window.Frame.Width;
            bottomSheetVC.View.Frame = new CGRect(0, window.Frame.GetMaxY(), width, height);

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

