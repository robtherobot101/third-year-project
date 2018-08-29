using System;
using CoreGraphics;
using UIKit;

namespace mobileAppClient.iOS
{
    public class BottomSheetViewController : UIViewController
    {
        public BottomSheetViewController()
        {
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
                var yComponent = UIScreen.MainScreen.Bounds.Height - 200;
                this.View.Frame = new CGRect(0, yComponent, frame.Width, frame.Height);
            }));
          
        }

        public override void ViewDidLoad()
        {
            base.ViewDidLoad();

            var gesture = new UIPanGestureRecognizer(panGesture);
            View.AddGestureRecognizer(gesture);

            //        let gesture = UIPanGestureRecognizer.init(target: self, action: #selector(BottomSheetViewController.panGesture))
            //view.addGestureRecognizer(gesture)
        }

        public void panGesture(UIPanGestureRecognizer recognizer) {
            var translation = recognizer.TranslationInView(this.View);
            var y = this.View.Frame.GetMinY();
            this.View.Frame = new CGRect(0, y + translation.Y, View.Frame.Width, View.Frame.Height);
            recognizer.SetTranslation(CGPoint.Empty, this.View);
        }

        //        func panGesture(recognizer: UIPanGestureRecognizer)
        //        {
        //            let translation = recognizer.translationInView(self.view)
        //    let y = self.view.frame.minY
        //    self.view.frame = CGRectMake(0, y + translation.y, view.frame.width, view.frame.height)
        //     recognizer.setTranslation(CGPointZero, inView: self.view)
        //}

        //        override func viewDidAppear(animated: Bool)
        //        {
        //            super.viewDidAppear(animated)

        //    UIView.animateWithDuration(0.3) { [weak self] in
        //        let frame = self?.view.frame
        //        let yComponent = UIScreen.mainScreen().bounds.height - 200
        //        self?.view.frame = CGRectMake(0, yComponent, frame!.width, frame!.height)
        //    }
        //}

        //        func prepareBackgroundView()
        //        {
        //            let blurEffect = UIBlurEffect.init(style: .Dark)
        //    let visualEffect = UIVisualEffectView.init(effect: blurEffect)
        //    let bluredView = UIVisualEffectView.init(effect: blurEffect)
        //    bluredView.contentView.addSubview(visualEffect)

        //    visualEffect.frame = UIScreen.mainScreen().bounds
        //    bluredView.frame = UIScreen.mainScreen().bounds

        //    view.insertSubview(bluredView, atIndex: 0)
        //}
    }
}
