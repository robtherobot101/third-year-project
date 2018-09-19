using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;

using Xamarin.Forms;
using mobileAppClient.Maps;
using Plugin.CurrentActivity;

[assembly: Dependency(typeof(mobileAppClient.Droid.MapEntryClass))]
namespace mobileAppClient.Droid
{
    class MapEntryClass : BottomSheetMapInterface
    {
        public MapEntryClass()
        {
        }

        public void addSlideUpSheet(CustomPin pin, CustomMap map)
        {
            Android.App.Activity mainActivity = CrossCurrentActivity.Current.Activity;

            Android.Views.View view = mainActivity.CurrentFocus;
            Android.Views.View root = view.RootView;
            Android.Views.View altRoot = mainActivity.Window.DecorView.FindViewById(Android.Resource.Id.Content);
            Android.Widget.RelativeLayout parent = view.Parent as Android.Widget.RelativeLayout;
            parent.RemoveView(view);
            Console.WriteLine("Retrieved current focus...");
            //LinearLayout BottomSheetPage = new LinearLayout(mainActivity);
            Console.WriteLine("Created a new layout...");
            if (Android.App.Application.Context.GetSystemService(Context.LayoutInflaterService) is Android.Views.LayoutInflater inflater)
            {
                Android.Views.View bottomSheetView = inflater.Inflate(Resource.Layout.BottomSheet, null);
                Android.Views.View test = inflater.Inflate(Resource.Layout.XamarinMapInfoWindow, null);
                Console.WriteLine("Created a bsheet...");
                Cheesebaron.SlidingUpPanel.SlidingUpPanelLayout main = bottomSheetView as Cheesebaron.SlidingUpPanel.SlidingUpPanelLayout;
                Android.Widget.RelativeLayout bottom = main.FindViewById<Android.Widget.RelativeLayout>(Resource.Id.mainWindow);
                bottom.AddView(view);
                mainActivity.SetContentView(main);

                
                //parent.AddView(bottomSheetView);
                //inflate to bsheet 

                //add the two views 

                //BottomSheetPage.AddView(view.RootView);
                //Console.WriteLine("Added the currentfocus...");
                //BottomSheetPage.AddView(bottomSheetView);
                //Console.WriteLine("Added the bsheet...");
                //// $$$$$$ PROFIT $$$$$$$
                //mainActivity.SetContentView(BottomSheetPage);
                //Console.WriteLine("Set content view!!!!");


            }
        }
    }
}
