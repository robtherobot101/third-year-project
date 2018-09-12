using System;
using CoreGraphics;
using MapKit;
using mobileAppClient.Maps;
using UIKit;
using Xamarin.Forms;

//[assembly: Dependency(typeof(mobileAppClient.iOS.MapEntryClass))]
namespace mobileAppClient.iOS
{
    public class MapEntryClass 
    {
        public MapEntryClass()
        {
        }

        public void addSlideUpSheet(CustomPin pin, CustomMap map, MKMapView nativeMap) {
            //Get the current UI Window
            var window = UIApplication.SharedApplication.KeyWindow;
            var bottomSheetVC = new BottomSheetViewController(pin, map, nativeMap);

            var rootVC = window.RootViewController;

            if(rootVC.View.Subviews.Length > 1) {
                UIView subView = rootVC.View.Subviews[1];
                subView.RemoveFromSuperview();
            }

            rootVC.AddChildViewController(bottomSheetVC);
            rootVC.View.AddSubview(bottomSheetVC.View);
            bottomSheetVC.DidMoveToParentViewController(rootVC);

            var height = window.Frame.Height;
            var width = window.Frame.Width;
            bottomSheetVC.View.Frame = new CGRect(0, window.Frame.GetMaxY(), width, height);



        }



    }
}
