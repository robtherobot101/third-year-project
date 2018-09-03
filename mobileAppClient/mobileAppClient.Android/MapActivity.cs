using System;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Support.V7.App;
using Cocosw.BottomSheetActions;
using Xamarin.Forms;

namespace mobileAppClient.Droid
{

    [Activity(MainLauncher = true)]
    public class MapActivity : AppCompatActivity
    {
        Context context = Android.App.Application.Context;

        public MapActivity()
        {
        }

        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);
            SetContentView(Resource.Layout.BottomSheet);
        }

        public void InitializeMap() {
            Intent intent = new Intent(context, typeof(BottomSheetListActivity));
            intent.PutExtra("action", 3);
            intent.PutExtra("title", "Andy");
            //intent.PutExtra("style", (e.Position == 5));
            StartActivity(intent);
        }
    }
}
