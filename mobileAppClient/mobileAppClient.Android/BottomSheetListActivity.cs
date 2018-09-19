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
       // private int action;
       // private ArrayAdapter<String> adapter;
      //  private int selectedPosition;

        protected override void OnCreate(Bundle bundle)
        {
            //if (Intent.GetBooleanExtra("style", false))
            //{
            //    this.SetTheme(Resource.Style.StyleTheme);
            //}

            base.OnCreate(bundle);
            SetContentView(Resource.Layout.OrganTransfer);

           // SupportActionBar.SetDisplayHomeAsUpEnabled(true);
          //  SupportActionBar.SetDisplayShowHomeEnabled(true);

            //this.Title = Intent.GetStringExtra("title");
           // this.action = Intent.GetIntExtra("action", 0);

            //String[] items = new String[] { "Miguel de Icaza", "Nat Friedman", "James Montemagno", "Joseph Hill", "Stephanie Schatz\n" };

            //this.adapter = new ArrayAdapter<String>(this, Android.Resource.Layout.SimpleListItem1, Android.Resource.Id.Text1, items);
            //var listView = FindViewById<ListView>(Resource.Id.listView);
            //listView.Adapter = this.adapter;
            //listView.OnItemClickListener = this;
            PrepareSheet();

        }

        //public override bool OnOptionsItemSelected(IMenuItem item)
        //{
        //    if (item.ItemId == Android.Resource.Id.Home)
        //        this.Finish();

        //    return base.OnOptionsItemSelected(item);
        //}

        //public void OnItemClick(AdapterView parent, View view, int position, long id)
        //{
        //    this.selectedPosition = position;
        //    this.ShowBottomSheet(this.selectedPosition);
        //}

        public void PrepareSheet()
        {
            var name = FindViewById<TextView>(Resource.Id.User_Name);
            var address = FindViewById<TextView>(Resource.Id.Address);
            var profilePicture = FindViewById<ImageView>(Resource.Id.ProfilePictureFrame);
            var organTable = FindViewById<TableLayout>(Resource.Id.organTableLayout);

            if (name != null)
            {
                name.Text = Intent.GetStringExtra("name");
            }
            if (address != null)
            {
                address.Text = Intent.GetStringExtra("address");
            }
            if (profilePicture != null)
            {
                var pictureString = Intent.GetStringExtra("profilePicture");
                if (pictureString.Length == 0)
                {
                    profilePicture.SetImageResource(Resource.Drawable.donationIcon);
                }
                else
                {
                    var imageBytes = Convert.FromBase64String(pictureString);
                    var imageData = BitmapFactory.DecodeByteArray(imageBytes, 0, imageBytes.Length);
                    profilePicture.SetImageBitmap(imageData);
                }
            }

            var organString = Intent.GetStringExtra("organs");

            var organs = organString.Split(',');
            foreach (var organ in organs)
            {
                TableRow organRow = new TableRow(this);
                TextView organText = new TextView(this);
                ImageView organImage = new ImageView(this);
                switch (organ)
                {
                    case "bone_icon.png":
                        organImage.SetImageResource(Resource.Drawable.bone_icon);
                        organText.Text = "Bone Marrow";
                        break;
                    case "ear_icon.png":
                        organImage.SetImageResource(Resource.Drawable.ear_icon);
                        organText.Text = "Middle Ear";
                        break;
                    case "eye_icon.png":
                        organImage.SetImageResource(Resource.Drawable.eye_icon);
                        organText.Text = "Cornea";
                        break;
                    case "heart_icon.png":
                        organImage.SetImageResource(Resource.Drawable.heart_icon);
                        organText.Text = "Heart";
                        break;
                    case "intestines_icon.png":
                        organImage.SetImageResource(Resource.Drawable.intestines_icon);
                        organText.Text = "Intestines";
                        break;
                    case "kidney_icon.png":
                        organImage.SetImageResource(Resource.Drawable.kidney_icon);
                        organText.Text = "Kidney";
                        break;
                    case "liver_icon.png":
                        organImage.SetImageResource(Resource.Drawable.liver_icon);
                        organText.Text = "Liver";
                        break;
                    case "lungs_icon.png":
                        organImage.SetImageResource(Resource.Drawable.lungs_icon);
                        organText.Text = "Lungs";
                        break;
                    case "pancreas_icon.png":
                        organImage.SetImageResource(Resource.Drawable.pancreas_icon);
                        organText.Text = "Pancreas";
                        break;
                    case "skin_icon.png":
                        organImage.SetImageResource(Resource.Drawable.skin_icon);
                        organText.Text = "Skin";
                        break;
                    case "tissue_icon.png":
                        organImage.SetImageResource(Resource.Drawable.tissue_icon);
                        organText.Text = "Connective Tissue";
                        break;
                }
                organRow.AddView(organImage);
                organRow.AddView(organText);
                organTable.AddView(organRow);

            }
               

        }

        //public void OnClick(IDialogInterface dialog, int which)
        //{
        //    //string name = this.adapter.GetItem(this.selectedPosition);
        //    //switch (which)
        //    //{
        //    //    case Resource.Id.share:
        //    //        Toast.MakeText(this, "Share to " + name, ToastLength.Short).Show();
        //    //        break;
        //    //    case Resource.Id.upload:
        //    //        Toast.MakeText(this, "Upload for " + name, ToastLength.Short).Show();
        //    //        break;
        //    //    case Resource.Id.call:
        //    //        Toast.MakeText(this, "Call to " + name, ToastLength.Short).Show();
        //    //        break;
        //    //    case Resource.Id.help:
        //    //        Toast.MakeText(this, "Help me!", ToastLength.Short).Show();
        //    //        break;
        //    //}
        //}
    }
}
