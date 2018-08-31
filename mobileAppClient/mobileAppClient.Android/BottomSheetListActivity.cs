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
using Android.Support.V7.App;
using Cocosw.BottomSheetActions;
using Android.Graphics.Drawables;
using Android.Graphics;
using Android.Support.V4.Graphics.Drawable;

namespace mobileAppClient.Droid
{
    [Activity]
    public class BottomSheetListActivity : AppCompatActivity
    {
        private int action;
        private ArrayAdapter<String> adapter;
        private int selectedPosition;

        protected override void OnCreate(Bundle bundle)
        {
            
        }
    }
}
