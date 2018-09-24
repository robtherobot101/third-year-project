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
    public class OrganTransferActivity : Activity, Android.Views.View.IOnClickListener
    {
        DonatableOrgan organ;
        private List<TableRow> allRecipientRows;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(Resource.Layout.OrganTransfer);
            var organString = Intent.GetStringExtra("organ");
            organ = organString.FromJson<DonatableOrgan>();
            prepareList();
        }

        public void OnClick(Android.Views.View view)
        {
            int rowIndex = view.Id;
            String recipientName = null;
            Android.Views.View nextChild = ((ViewGroup)view).GetChildAt(1);

            recipientName = ((TextView)nextChild).Text;

            User selectedReceiver = null;

            foreach (User receiver in organ.topReceivers)
            {
                if (receiver.FullName.ToLower().Equals(recipientName.ToLower()))
                {
                    selectedReceiver = receiver;
                }
            }

            //Begin trasnfer process
        }

        private async void prepareList()
        { 
            var receiverTable = FindViewById<TableLayout>(Resource.Id.ReceiverTableLayout);
            var organText = FindViewById<TextView>(Resource.Id.Organ_Name);
            var organTimerText = FindViewById<TextView>(Resource.Id.Time_Left);
            var organDonor = FindViewById<TextView>(Resource.Id.UserName);
            var organImage = FindViewById<ImageView>(Resource.Id.Organ_Picture);
            var transferText = FindViewById<TextView>(Resource.Id.transferText);

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

            //Set the time left to be thetext of the previous page

            //Set the user donor string

            //-----------------------------------------------------------------------------------------------
            //Bottom Card
            //------------------------------------------------------------------------------------

            //This is used onClick as an index to insert the receiver table.
            int i = 1;
            allRecipientRows = new List<TableRow>();
            if(organ.expired)
            {
                organTimerText.Text = "EXPIRED";
                transferText.Text = "This organ has expired.";
            } else if(organ.inTransfer == 1)
            {
                organTimerText.Text = "IN TRANSIT";
                transferText.Text = "This organ is in transit.";
            }
            else if (organ.inTransfer == 2)
            {
                organTimerText.Text = "SUCCESSFULLY TRANSFERRED";
                transferText.Text = "This has been successfully transferred.";
            }

            else if(organ.topReceivers.IsEmpty())
            {
                transferText.Text = "No valid receivers found.";
               
            } else
            {
                foreach (User recipient in organ.topReceivers)
                {
                    TableRow recipientRow = new TableRow(this);
                    TextView recipientName = new TextView(this);
                    TextView recipientAddress = new TextView(this);
                    ImageView recipientImage = new ImageView(this);

                    //TODO Get the users profile picture and set the imageView

                    recipientImage.SetImageResource(Resource.Drawable.donationIcon);

                    recipientName.Text = recipient.FullName;

                    recipientName.SetTextAppearance(this, Android.Resource.Style.TextAppearanceMedium);

                    recipientImage.SetAdjustViewBounds(true);
                    recipientImage.SetMaxHeight(80);
                    recipientImage.SetMaxWidth(80);
                    recipientImage.SetPadding(5, 0, 20, 0);

                    recipientAddress.Text = recipient.currentAddress + ", " + recipient.region;

                    recipientRow.AddView(recipientImage);
                    recipientRow.AddView(recipientName);
                    recipientRow.AddView(recipientAddress);
                    recipientRow.Id = i;
                    recipientRow.SetOnClickListener(this);
                    receiverTable.AddView(recipientRow);
                    allRecipientRows.Add(recipientRow);
                    i++;


                }
                //StartTickingTimer(1000);
            }



        }

        public void StartTickingTimer(int interval)
        {
            Timer timer = new Timer(RefreshCountdownsInTableView, null, 0, interval);
        }

        public void RefreshCountdownsInTableView(object o)
        {
            Device.BeginInvokeOnMainThread(() =>
            {
                foreach (TableRow row in allRecipientRows)
                {
                    string detailString = ((TextView)(row.GetChildAt(2))).Text;
                    if (detailString.Equals("EXPIRED"))
                    {
                        continue;
                    }
                    else
                    {
                        string timeLeftString = detailString.Substring(16);
                        string timeString = timeLeftString.Remove(timeLeftString.Length - 5);

                        TimeSpan timeLeft = TimeSpan.Parse(timeString);
                        if (timeLeft.Equals(new TimeSpan(0, 0, 0)))
                        {
                            detailString = "EXPIRED";
                            ((TextView)(row.GetChildAt(2))).SetTextColor(Android.Graphics.Color.Red);
                            //Update the Organ object to be expired
                        }
                        else
                        {
                            timeLeft = timeLeft.Subtract(new TimeSpan(0, 0, 1));
                            detailString = detailString.Substring(0, 16) + timeLeft.ToString(@"dd\:hh\:mm\:ss") + " days";

                        }
                        ((TextView)(row.GetChildAt(2))).Text = detailString;
                    }


                }

            });
        }
    }
}