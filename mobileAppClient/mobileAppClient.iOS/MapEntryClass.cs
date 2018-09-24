using System;
using System.Threading.Tasks;
using CoreGraphics;
using CustomRenderer.iOS;
using MapKit;
using mobileAppClient.Maps;
using UIKit;
using Xamarin.Forms;

namespace mobileAppClient.iOS
{
    public class MapEntryClass
    {
        public MapEntryClass()
        {
        }

        public async Task addSlideUpSheet(CustomPin pin, CustomMap map, MKMapView nativeMap, CustomMapRenderer customMapRenderer)
        {
            //Get the current UI Window
            var window = UIApplication.SharedApplication.KeyWindow;
            BottomSheetViewController bottomSheetVC = new BottomSheetViewController(pin, map, nativeMap, customMapRenderer);
            //await bottomSheetVC.OpenMenu();

            //var rootVC = window.RootViewController;

            //var yComponent = UIScreen.MainScreen.Bounds.Height - (UIApplication.SharedApplication.StatusBarFrame.Height) - 70;

            //bottomSheetVC.View.Frame = new CGRect(0, yComponent, rootVC.View.Frame.Width, rootVC.View.Frame.Height - 500);
            //bottomSheetVC.View.LayoutIfNeeded();

            //UIView.Transition(window, 0.3, UIViewAnimationOptions.CurveEaseIn, () => window.RootViewController = bottomSheetVC, null);
            //UIView.Transition()

            var rootVC = window.RootViewController;

            if(rootVC != null) {
                bottomSheetVC.AddChildViewController(rootVC);
            }

            window.RootViewController = bottomSheetVC;



            //rootVC = window.RootViewController;

            //var children = rootVC.ChildViewControllers;

            //var rootVC = window.RootViewController;

            //rootVC.View.AddSubview(bottomSheetVC.View);
            //rootVC.View.BringSubviewToFront(bottomSheetVC.View);
            ////bottomSheetVC.DidMoveToParentViewController(rootVC);



            //if(rootVC.View.Subviews.Length > 1) {
            //    UIView subView = rootVC.View.Subviews[1];
            //    subView.RemoveFromSuperview();
            //}

            //UIApplication.SharedApplication.Delegate.GetWindow().RootViewController = bottomSheetVC;

            //bottomSheetVC.ModalPresentationStyle = UIModalPresentationStyle.OverCurrentContext;
            //rootVC.AddChildViewController(bottomSheetVC);
            //UIApplication.SharedApplication.Delegate.GetWindow().RootViewController = bottomSheetVC;
            //rootVC.PresentModalViewController(bottomSheetVC, true);

        }



    }
}
