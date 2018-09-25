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
using Xamarin.Forms.Maps;
using mobileAppClient.Models;
using mobileAppClient.Views.Clinician;

namespace mobileAppClient.Droid
{
    [Activity(Label = "OrganTransferActivity")]
    public class OrganTransferActivity : Activity, Android.Views.View.IOnClickListener
    {
        DonatableOrgan organ;
        private List<TableRow> allRecipientRows;
        TextView organTimerText;
        TextView transferText;
        TableLayout receiverTable;
        Timer timer;


        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(Resource.Layout.OrganTransfer);
            var organString = Intent.GetStringExtra("organ");
            organ = organString.FromJson<DonatableOrgan>();
        
            prepareList();
        }


        public async void OnClick(Android.Views.View view)
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
            ClinicianMapPage clinicianMapPage = new ClinicianMapPage();

            bool canArrive = await clinicianMapPage.CheckGetToReceiverInTime(organ, selectedReceiver);

            if (!canArrive)
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.SetTitle("Cannot Transfer");
                alert.SetMessage("Cannot transfer " + organ.organType + " to " + selectedReceiver.FullName + " as it will expire before it arrives.");
                alert.SetPositiveButton("confirm", (senderAlert, args) =>{});

                Dialog dialog = alert.Create();
                dialog.Show();
            }
            else
            {



                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.SetTitle("Confirm Transfer");
                alert.SetMessage("Are you sure you want to transfer " + organ.organType + " to " + selectedReceiver.FullName + "?");
                alert.SetPositiveButton("Yes", async (senderAlert, args) =>
                {
                    Toast.MakeText(this, "Transfer process started.", ToastLength.Short).Show();
                    Console.WriteLine("LET US BEGIN");

                    //Update bottom sheet to show In transfer - empty table and update countdown

                    organTimerText.Text = "IN TRANSIT";
                    organTimerText.SetTextColor(Android.Graphics.Color.Orange);

                    transferText.Text = "This organ is currently in transit.";
                    receiverTable.RemoveAllViews();

                    //Update map to get rid of overlays and recipients 
                    timer.Dispose();
                    timer = null;
                    organ.inTransfer = 1;

                    //Insert transfer into database and add new helicopter.

                    double donorLat = Convert.ToDouble(Intent.GetStringExtra("donorLat"));
                    double donorLong = Convert.ToDouble(Intent.GetStringExtra("donorLong"));
                    Position pos = new Position(donorLat, donorLong);
                    clinicianMapPage.NewTransferWithoutAddingHelicpoter(organ, selectedReceiver, pos);
                });

                alert.SetNegativeButton("No", (senderAlert, args) =>
                {
                    Toast.MakeText(this, "Transfer Cancelled!", ToastLength.Short).Show();
                });

                Dialog dialog = alert.Create();
                dialog.Show();

            }
        }

        private async void prepareList()
        { 
            receiverTable = FindViewById<TableLayout>(Resource.Id.ReceiverTableLayout);
            var organText = FindViewById<TextView>(Resource.Id.Organ_Name);
            organTimerText = FindViewById<TextView>(Resource.Id.Time_Left);
            var organImage = FindViewById<ImageView>(Resource.Id.Organ_Picture);
            transferText = FindViewById<TextView>(Resource.Id.transferText);

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


            //Set the user donor string

            Tuple<string, long> timeRemainingTuple = organ.getTimeRemaining();
            organTimerText.Text = timeRemainingTuple.Item1;
            //Change colour based on severity
            long timeRemaining = timeRemainingTuple.Item2;
            if (timeRemaining <= 3600)
            {
                organTimerText.SetTextColor(Android.Graphics.Color.Rgb(244, 65, 65));
            }
            else if (timeRemaining <= 10800)
            {
                organTimerText.SetTextColor(Android.Graphics.Color.Rgb(244, 130, 65));
            }
            else if (timeRemaining <= 21600)
            {
                organTimerText.SetTextColor(Android.Graphics.Color.Rgb(244, 190, 65));
            }
            else if (timeRemaining <= 43200)
            {
                organTimerText.SetTextColor(Android.Graphics.Color.Rgb(244, 241, 65));
            }
            else if (timeRemaining <= 86400)
            {
                organTimerText.SetTextColor(Android.Graphics.Color.Rgb(208, 244, 65));
            }
            else if (timeRemaining <= 172800)
            {
                organTimerText.SetTextColor(Android.Graphics.Color.Rgb(160, 244, 65));
            }
            else
            {
                organTimerText.SetTextColor(Android.Graphics.Color.Rgb(76, 244, 65));
            }



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

                    UserAPI userAPI = new UserAPI();
                    Tuple<System.Net.HttpStatusCode, String> tuple = await userAPI.GetUserPhotoForMapObjects(recipient.id);
                    if (tuple.Item1 == System.Net.HttpStatusCode.OK)
                    {
                        if (tuple.Item2.Length == 0)
                        {
                            recipientImage.SetImageResource(Resource.Drawable.donationIcon);
                        }
                        else
                        {
                            var imageBytes = Convert.FromBase64String(tuple.Item2);
                            var imageData = Android.Graphics.BitmapFactory.DecodeByteArray(imageBytes, 0, imageBytes.Length);
                            recipientImage.SetImageBitmap(imageData);
                        }
                        
                    }
                    else
                    {
                        recipientImage.SetImageResource(Resource.Drawable.donationIcon);
                    }

                    recipientName.Text = recipient.FullName;

                    recipientName.SetTextAppearance(this, Android.Resource.Style.TextAppearanceMedium);

                    recipientImage.SetAdjustViewBounds(true);
                    recipientImage.SetMaxHeight(80);
                    recipientImage.SetMaxWidth(10);
                    //receiverTable.SetColumnShrinkable(0, true);

                    recipientAddress.Text = recipient?.currentAddress + ", " + recipient?.region;

                    recipientRow.AddView(recipientImage);
                    recipientRow.AddView(recipientName);
                    recipientRow.AddView(recipientAddress);
                    recipientRow.Id = i;
                    recipientRow.SetOnClickListener(this);
                    receiverTable.AddView(recipientRow);
                    allRecipientRows.Add(recipientRow);
                    i++;


                }
            }
            StartTickingTimer(1000);

        }

        public void StartTickingTimer(int interval)
        {
            timer = new Timer(RefreshCountdownsInTableView, null, 0, interval);
        }

        public void RefreshCountdownsInTableView(object o)
        {
            Device.BeginInvokeOnMainThread(() =>
            {

                if (organTimerText.Text.Equals("EXPIRED"))
                {
                    return;
                } else if (organTimerText.Text.Equals("IN TRANSIT"))
                {
                    return;
                }
                else if (organTimerText.Text.Equals("SUCCESSFULLY TRANSFERRED"))
                {
                    return;
                }
                
                else
                {
                    string timeLeftString = organTimerText.Text.Substring(16);
                    string timeString = timeLeftString.Remove(timeLeftString.Length - 5);

                    TimeSpan timeLeft = TimeSpan.Parse(timeString);
                    if (timeLeft.Equals(new TimeSpan(0, 0, 0)))
                    {
                        organTimerText.Text = "EXPIRED";
                        organTimerText.SetTextColor(Android.Graphics.Color.Red);
                        //Update the Organ object to be expired
                    }
                    else
                    {
                        timeLeft = timeLeft.Subtract(new TimeSpan(0, 0, 1));
                        organTimerText.Text = organTimerText.Text.Substring(0, 16) + timeLeft.ToString(@"dd\:hh\:mm\:ss") + " days";

                    }
                    
                }


                

            });
        }
    }
}