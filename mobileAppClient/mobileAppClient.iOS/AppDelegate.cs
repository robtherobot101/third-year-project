using System;
using System.Collections.Generic;
using System.Linq;
using SegmentedControl;
using Plugin.CrossPlatformTintedImage;
using Microsoft.AppCenter;
using Microsoft.AppCenter.Analytics;
using Microsoft.AppCenter.Crashes;

using Foundation;
using ImageCircle.Forms.Plugin.iOS;
using UIKit;
using Microsoft.AppCenter.Push;
using Plugin.Toasts;
using Xamarin.Forms;
using UserNotifications;

namespace mobileAppClient.iOS
{
    // The UIApplicationDelegate for the application. This class is responsible for launching the 
    // User Interface of the application, as well as listening (and optionally responding) to 
    // application events from iOS.
    [Register("AppDelegate")]
    public partial class AppDelegate : global::Xamarin.Forms.Platform.iOS.FormsApplicationDelegate
    {
        //
        // This method is invoked when the application has loaded and is ready to run. In this 
        // method you should instantiate the window, load the UI into it and then make the window
        // visible.
        //
        // You have 17 seconds to return from this method, or iOS will terminate your application.
        //
        public override bool FinishedLaunching(UIApplication app, NSDictionary options)
        {
            global::Xamarin.Forms.Forms.Init();
            global::SegmentedControl.FormsPlugin.iOS.SegmentedControlRenderer.Init();
            global::Plugin.CrossPlatformTintedImage.iOS.TintedImageRenderer.Init();
            global::Xamarin.FormsMaps.Init();
            global::CarouselView.FormsPlugin.iOS.CarouselViewRenderer.Init();
            global::ImageCircle.Forms.Plugin.iOS.ImageCircleRenderer.Init();
            
            LoadApplication(new App());

            // For circular images (on menu drawer)
            //ImageCircleRenderer.Init();

            return base.FinishedLaunching(app, options);
        }

        public override void RegisteredForRemoteNotifications(UIApplication application, NSData deviceToken)
        {
            Push.RegisteredForRemoteNotifications(deviceToken);
        }

        public override void FailedToRegisterForRemoteNotifications(UIApplication application, NSError error)
        {
            Push.FailedToRegisterForRemoteNotifications(error);
        }

        public override void DidReceiveRemoteNotification(UIApplication application, NSDictionary userInfo, System.Action<UIBackgroundFetchResult> completionHandler)
        {
            var result = Push.DidReceiveRemoteNotification(userInfo);
            if (result)
            {
                completionHandler?.Invoke(UIBackgroundFetchResult.NewData);
            }
            else
            {
                completionHandler?.Invoke(UIBackgroundFetchResult.NoData);
            }
        }
    }
}
