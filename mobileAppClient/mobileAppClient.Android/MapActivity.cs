using System;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Support.V7.App;
using Xamarin.Forms;

namespace mobileAppClient.Droid
{

    [Activity(MainLauncher = true)]
    public class MapActivity : AppCompatActivity
    {
        Context context = Android.App.Application.Context;
        Android.Views.View view;

        public MapActivity()
        {
        }

        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);
            //SetContentView();
        }

        public void InitializeMap() {
            //Intent intent = new Intent(this, typeof(BottomSheetListActivity));
            //intent.PutExtra("action", 3);
            //intent.PutExtra("title", "Andy");
            //intent.PutExtra("style", (e.Position == 5));
            //StartActivity(intent);
        }
    }
}
