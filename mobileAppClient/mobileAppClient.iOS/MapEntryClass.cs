using System;
using CoreGraphics;
using mobileAppClient.Maps;
using UIKit;
using Xamarin.Forms;

[assembly: Dependency(typeof(mobileAppClient.iOS.MapEntryClass))]
namespace mobileAppClient.iOS
{
    public class MapEntryClass : BottomSheetMapInterface
    {
        public MapEntryClass()
        {
        }

        public void addSlideUpSheet() {
            //Get the current UI Window
            var window = UIApplication.SharedApplication.KeyWindow;
            //var bottomSheet = new MapViewController();
            var bottomSheetVC = new BottomSheetViewController();

            var height = window.Frame.Height;
            var width = window.Frame.Width;
            bottomSheetVC.View.Frame = new CGRect(0, window.Frame.GetMaxY(), width, height);

            //bottomSheet.View.BackgroundColor = UIColor.White;
            bottomSheetVC.ModalPresentationStyle = UIModalPresentationStyle.OverCurrentContext;

            window.RootViewController.PresentViewController(bottomSheetVC, true, null);
            window.MakeKeyAndVisible();

        }



    }
}
