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

        public void addSlideUpSheet(CustomPin pin) {
            //Get the current UI Window
            var window = UIApplication.SharedApplication.KeyWindow;
            var bottomSheetVC = new BottomSheetViewController(pin);

            var height = window.Frame.Height;
            var width = window.Frame.Width;
            bottomSheetVC.View.Frame = new CGRect(0, window.Frame.GetMaxY(), width, height);

            bottomSheetVC.ModalPresentationStyle = UIModalPresentationStyle.OverCurrentContext;

            window.RootViewController.PresentViewController(bottomSheetVC, true, null);

        }



    }
}
