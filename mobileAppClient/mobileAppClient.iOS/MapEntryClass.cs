using System;
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
            var bottomSheet = new MapViewController();
            window.RootViewController = bottomSheet;
            window.MakeKeyAndVisible();

        }



    }
}
