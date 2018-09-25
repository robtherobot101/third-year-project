using System;
using Android.Views.InputMethods;
using mobileAppClient.Droid;
using mobileAppClient.Views.Messaging;
using Plugin.CurrentActivity;

[assembly: Xamarin.Forms.Dependency(typeof(AndroidForceKeyboardDismissalService))]
namespace mobileAppClient.Droid
{
    public class AndroidForceKeyboardDismissalService : IForceKeyboardDismissalService
    {
        public void DismissKeyboard()
        {
            InputMethodManager imm = InputMethodManager.FromContext(CrossCurrentActivity.Current.Activity.ApplicationContext);

            imm.HideSoftInputFromWindow(
                CrossCurrentActivity.Current.Activity.Window.DecorView.WindowToken, HideSoftInputFlags.NotAlways);
        }
    }
}

