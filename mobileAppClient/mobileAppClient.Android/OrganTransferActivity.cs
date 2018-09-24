using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using mobileAppClient.odmsAPI;
using ServiceStack;
using Xamarin.Forms;
namespace mobileAppClient.Droid
{
    [Activity(Label = "OrganTransferActivity")]
    public class OrganTransferActivity : Activity
    {
        DonatableOrgan organ;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(Resource.Layout.OrganTransfer);
            var organString = Intent.GetStringExtra("organ");
            organ = organString.FromJson<DonatableOrgan>();
            prepareList();
        }

        private async void prepareList()
        { 
            var receiverTable = FindViewById<TableLayout>(Resource.Id.ReceiverTableLayout);
            var organText = FindViewById<TextView>(Resource.Id.Organ_Name);
            var organTimerText = FindViewById<TextView>(Resource.Id.Time_Left);
            var organDonor = FindViewById<TextView>(Resource.Id.UserName);
            var organImage = FindViewById<ImageView>(Resource.Id.Organ_Picture);

            switch (organ.organType.ToLower())
            {
                case "bone":
                    organImage.SetImageResource(Resource.Drawable.bone_icon);
                    organText.Text = "Bone Marrow";
                    break;
                case "ear":
                    organImage.SetImageResource(Resource.Drawable.ear_icon);
                    organText.Text = "Middle Ear";
                    break;
                case "cornea":
                    organImage.SetImageResource(Resource.Drawable.cornea_icon);
                    organText.Text = "Cornea";
                    break;
                case "heart":
                    organImage.SetImageResource(Resource.Drawable.heart_icon);
                    organText.Text = "Heart";
                    break;
                case "intestine":
                    organImage.SetImageResource(Resource.Drawable.intestine_icon);
                    organText.Text = "Intestines";
                    break;
                case "kidney":
                    organImage.SetImageResource(Resource.Drawable.kidney_icon);
                    organText.Text = "Kidney";
                    break;
                case "liver":
                    organImage.SetImageResource(Resource.Drawable.liver_icon);
                    organText.Text = "Liver";
                    break;
                case "lung":
                    organImage.SetImageResource(Resource.Drawable.lung_icon);
                    organText.Text = "Lungs";
                    break;
                case "pancreas":
                    organImage.SetImageResource(Resource.Drawable.pancreas_icon);
                    organText.Text = "Pancreas";
                    break;
                case "skin":
                    organImage.SetImageResource(Resource.Drawable.skin_icon);
                    organText.Text = "Skin";
                    break;
                case "tissue":
                    organImage.SetImageResource(Resource.Drawable.tissue_icon);
                    organText.Text = "Connective Tissue";
                    break;
            }
            UserAPI userApi = new UserAPI();
            User donor = await userApi.getUser(organ.donorId, ClinicianController.Instance.AuthToken);
            organDonor.Text = donor.FullName;

            //StartTickingTimer(1000);
        }

        //public void StartTickingTimer(int interval)
        //{
        //    Timer timer = new Timer(RefreshCountdownsInTableView, null, 0, interval);
        //}

        //public void RefreshCountdownsInTableView(object o)
        //{
        //    Device.BeginInvokeOnMainThread(() =>
        //    {
        //        TableSource tableSource = (TableSource)OrgansTableView.Source;
        //        foreach (UITableViewCell cell in tableSource.Cells.Values)
        //        {
        //            string detailString = organTimer.Text;
        //            if (detailString.Equals("EXPIRED"))
        //            {
        //                continue;
        //            }
        //            else
        //            {
        //                string timeLeftString = detailString.Substring(16);
        //                string timeString = timeLeftString.Remove(timeLeftString.Length - 5);

        //                TimeSpan timeLeft = TimeSpan.Parse(timeString);
        //                if (timeLeft.Equals(new TimeSpan(0, 0, 0)))
        //                {
        //                    detailString = "EXPIRED";
        //                    organTimer.SetTextColor(Android.Graphics.Color.Red);
        //                    //Update the Organ object to be expired
        //                }
        //                else
        //                {
        //                    timeLeft = timeLeft.Subtract(new TimeSpan(0, 0, 1));
        //                    detailString = detailString.Substring(0, 16) + timeLeft.ToString(@"dd\:hh\:mm\:ss") + " days";

        //                }
        //                organTimer.Text = detailString;
        //            }


        //        }
        //    });
        //}
    }
}