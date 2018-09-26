using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Foundation;
using mobileAppClient.iOS.Notifications;
using mobileAppClient.Notifications;
using UIKit;

[assembly: Xamarin.Forms.Dependency(typeof(iOSToast))]
namespace mobileAppClient.iOS.Notifications
{
    public class iOSToast : IToast
    {
        const double LONG_DELAY = 3.5;
        const double SHORT_DELAY = 2.0;

        NSTimer alertDelay;
        public UIAlertController alert;

        public void LongAlert(string message)
        {
            ShowAlert(message, LONG_DELAY);
        }
        public void ShortAlert(string message)
        {
            ShowAlert(message, SHORT_DELAY);
        }

        void ShowAlert(string message, double seconds)
        {
            alertDelay = NSTimer.CreateScheduledTimer(seconds, (obj) =>
            {
                dismissMessage();
            });
            alert = UIAlertController.Create(null, message, UIAlertControllerStyle.Alert);
            //alert.AddAction(UIAlertAction.Create("Confirm", UIAlertActionStyle.Cancel, null));
            UIViewController uIViewController = GetVisibleViewController();
            uIViewController.PresentViewController(alert, true, null);
            //UIApplication.SharedApplication.KeyWindow.RootViewController.PresentViewController(alert, true, null);

        }

        void dismissMessage()
        {
            if (alert != null)
            {
                UIViewController uIViewController = GetVisibleViewController();
                uIViewController.DismissViewController(true, null);
                //alert.DismissViewController(true, null);
            }
            if (alertDelay != null)
            {
                alertDelay.Dispose();
            }
        }

        UIViewController GetVisibleViewController()
        {
            var rootController = UIApplication.SharedApplication.KeyWindow.RootViewController;

            if (rootController.PresentedViewController == null)
                return rootController;

            if (rootController.PresentedViewController is UINavigationController)
            {
                return ((UINavigationController)rootController.PresentedViewController).VisibleViewController;
            }

            if (rootController.PresentedViewController is UITabBarController)
            {
                return ((UITabBarController)rootController.PresentedViewController).SelectedViewController;
            }

            return rootController.PresentedViewController;
        }
    }
}