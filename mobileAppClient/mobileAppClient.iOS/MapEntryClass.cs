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


            var rootVC = window.RootViewController;

            if(rootVC != null) {
                bottomSheetVC.AddChildViewController(rootVC);
            }

            window.RootViewController = bottomSheetVC;




        }



    }
}
