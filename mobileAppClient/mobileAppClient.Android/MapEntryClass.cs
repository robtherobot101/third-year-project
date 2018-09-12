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
            Console.WriteLine("Set main activity...");
            

            Android.Views.View view = mainActivity.CurrentFocus;
            LinearLayout parent = view.Parent as LinearLayout;
            
            Console.WriteLine("Retrieved current focus...");
            LinearLayout BottomSheetPage = new LinearLayout(mainActivity);
            Console.WriteLine("Created a new layout...");
            if (Android.App.Application.Context.GetSystemService(Context.LayoutInflaterService) is Android.Views.LayoutInflater inflater)
            {
                Android.Views.View bottomSheetView = inflater.Inflate(Resource.Layout.BottomSheet, null);
                Console.WriteLine("Created a bsheet...");
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
