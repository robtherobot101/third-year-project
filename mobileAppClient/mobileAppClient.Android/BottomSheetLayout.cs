using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Util;
using Android.Views;
using Android.Widget;

using Cheesebaron.SlidingUpPanel;

namespace mobileAppClient.Droid
{
    class BottomSheetLayout : SlidingUpPanelLayout
    {
        public BottomSheetLayout(Context context) : base(context)
        {
        }

        public BottomSheetLayout(IntPtr javaReference, JniHandleOwnership transfer) : base(javaReference, transfer)
        {
        }

        public BottomSheetLayout(Context context, IAttributeSet attrs) : base(context, attrs)
        {
        }

        public BottomSheetLayout(Context context, IAttributeSet attrs, int defStyle) : base(context, attrs, defStyle)
        {
        }
    }
}