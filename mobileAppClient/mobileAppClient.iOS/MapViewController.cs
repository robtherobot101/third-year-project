using System;
using CoreGraphics;
using mobileAppClient.Maps;
using UIKit;
using Xamarin.Forms;

namespace mobileAppClient.iOS
{
    public class MapViewController : UIViewController
    {
        public MapViewController()
        {
        }


         public void addBottomSheetView() {

            // 1- Init bottomSheetVC
            var bottomSheetVC = new BottomSheetViewController();

            // 2- Add bottomSheetVC as a child view 
            this.AddChildViewController(bottomSheetVC);
            this.View.AddSubview(bottomSheetVC.View);
            bottomSheetVC.DidMoveToParentViewController(this);

            // 3- Adjust bottomSheet frame and initial position.
            var height = View.Frame.Height;
            var width = View.Frame.Width;
            bottomSheetVC.View.Frame = new CGRect(0, this.View.Frame.GetMaxY(), width, height);

        }

        public override void ViewDidAppear(bool animated)
        {
            base.ViewDidAppear(animated);
            addBottomSheetView();
        }


        //        override func viewDidAppear(animated: Bool)
        //        {
        //            super.viewDidAppear(animated)
        //    addBottomSheetView()
        //}


        //        func addBottomSheetView()
        //        {
        //            // 1- Init bottomSheetVC
        //            let bottomSheetVC = BottomSheetViewController()

        //    // 2- Add bottomSheetVC as a child view 
        //            self.addChildViewController(bottomSheetVC)
        //    self.view.addSubview(bottomSheetVC.view)
        //    bottomSheetVC.didMoveToParentViewController(self)

        //    // 3- Adjust bottomSheet frame and initial position.
        //            let height = view.frame.height
        //    let width = view.frame.width
        //    bottomSheetVC.view.frame = CGRectMake(0, self.view.frame.maxY, width, height)
        //}
    }
}
