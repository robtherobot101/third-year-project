using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SlideOverKit;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace mobileAppClient.Views.Clinician
{
    public partial class SlideUpMenuView : SlideMenuView
    {
        public SlideUpMenuView ()
        {
            InitializeComponent ();
            // You must set HeightRequest in this case
            this.HeightRequest = 250;
            // You must set IsFullScreen in this case, 
            // otherwise you need to set WidthRequest, 
            // just like the QuickInnerMenu sample
            this.IsFullScreen = true;
            this.MenuOrientations = MenuOrientation.BottomToTop;

            // You must set BackgroundColor, 
            // and you cannot put another layout with background color cover the whole View
            // otherwise, it cannot be dragged on Android
            this.BackgroundColor = Color.Black;
            this.BackgroundViewColor = Color.Transparent;

            // In some small screen size devices, the menu cannot be full size layout.
            // In this case we need to set different size for Android.
            if (Device.RuntimePlatform == Device.Android)
                this.HeightRequest += 50;
        }
    }
}

//    public partial class SlideUpMenuView : SlideMenuView
//    {
//        public SlideUpMenuView()

//        {
//            // You must set HeightRequest in this case
//            this.HeightRequest = 250;
//            // You must set IsFullScreen in this case, 
//            // otherwise you need to set WidthRequest, 
//            // just like the QuickInnerMenu sample
//            this.IsFullScreen = true;
//            this.MenuOrientations = MenuOrientation.BottomToTop;

//            // You must set BackgroundColor, 
//            // and you cannot put another layout with background color cover the whole View
//            // otherwise, it cannot be dragged on Android
//            this.BackgroundColor = Color.Black;
//            this.BackgroundViewColor = Color.Transparent;

//            // In some small screen size devices, the menu cannot be full size layout.
//            // In this case we need to set different size for Android.
//            if (Device.OS == TargetPlatform.Android)
//                this.HeightRequest += 50;

//            System.Diagnostics.Debug.WriteLine("Entered Class...");
//            try
//            {
//                System.Diagnostics.Debug.WriteLine("Attempting InitComp....");
//                InitializeComponent();
//                System.Diagnostics.Debug.WriteLine("InitComp Succeeded...");
//            } catch (System.Exception)
//            {
//                System.Diagnostics.Debug.WriteLine(e);
//            }
           
//        }

//        private void InitializeComponent()
//        {
//            System.Diagnostics.Debug.WriteLine("Attempting Inner IntComp...");
//            global::Xamarin.Forms.Xaml.Extensions.LoadFromXaml(this, typeof(SlideUpMenuView));
//            System.Diagnostics.Debug.WriteLine("Inner IntComp Succeeded...");
//        }
//    }
//}