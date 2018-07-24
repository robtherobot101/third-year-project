using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using mobileAppClient.odmsAPI;
using System.Net;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the organs page for 
     * showing all of a users donated organs. 
     */ 
    public partial class OrgansPage : ContentPage 
    {
        /*
         * Constructor which sets all of the organ cells to be on or off
         * depending on which organs a user has donated.
         */ 
        public OrgansPage()
        {
            InitializeComponent();
            foreach(string item in UserController.Instance.LoggedInUser.organs) {
                Console.WriteLine(item);
                switch(item) {
                    case "LIVER":
                        LiverCell.On = true;
                        break;
                    case "KIDNEY":
                        KidneyCell.On = true;
                        break;
                    case "PANCREAS":
                        PancreasCell.On = true;
                        break;
                    case "HEART": 
                        HeartCell.On = true;
                        break;
                    case "LUNG":
                        LungCell.On = true;
                        break;
                    case "INTESTINE":
                        IntestineCell.On = true;
                        break;
                    case "CORNEA":
                        CorneaCell.On = true;
                        break;
                    case "EAR":
                        MiddleEarCell.On = true;
                        break;
                    case "SKIN":
                        SkinCell.On = true;
                        break;
                    case "BONE":
                        BoneMarrowCell.On = true;
                        break;
                    case "TISSUE":
                        ConnectiveTissueCell.On = true;
                        break;
                        
                }
            }

        }

        /*
         * Handles when a user presses the donate all button
         * to set all organs to be donated.
         */ 
        void Handle_DonateAllPressed(object sender, System.EventArgs e)
        {
            LiverCell.On = true;
            KidneyCell.On = true;
            PancreasCell.On = true;
            HeartCell.On = true;
            LungCell.On = true;
            IntestineCell.On = true;
            CorneaCell.On = true;
            MiddleEarCell.On = true;
            SkinCell.On = true;
            BoneMarrowCell.On = true;
            ConnectiveTissueCell.On = true;
            
        }

        /*
         * Handles when a user presses the save button, sending an 
         * API call to update a user's donated organs.
         */ 
        async void Handle_SavePressed(object sender, System.EventArgs e)
        {
            UserController.Instance.LoggedInUser.organs.Clear();
            if(LiverCell.On) {
                UserController.Instance.LoggedInUser.organs.Add("LIVER");
            }
            if (KidneyCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add("KIDNEY");
            }
            if (PancreasCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add("PANCREAS");
            }
            if (HeartCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add("HEART");
            }
            if (LungCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add("LUNG");
            }
            if (IntestineCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add("INTESTINE");
            }
            if (CorneaCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add("CORNEA");
            }
            if (MiddleEarCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add("EAR");
            }
            if (SkinCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add("SKIN");
            }
            if (BoneMarrowCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add("BONE");
            }
            if (ConnectiveTissueCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add("TISSUE");
            }
            foreach(string item in UserController.Instance.LoggedInUser.organs) {
                Console.WriteLine(item);
            }

            UserAPI userAPI = new UserAPI();
            HttpStatusCode userUpdated = await userAPI.UpdateUser();

            switch(userUpdated)
            {
                case HttpStatusCode.Created:
                    await DisplayAlert("",
                    "User Organs Successfully updated",
                    "OK");
                    break;
                case HttpStatusCode.BadRequest:
                    await DisplayAlert("",
                    "User Organs Update Failed (400)",
                    "OK");
                    break;
                case HttpStatusCode.ServiceUnavailable:
                    await DisplayAlert("",
                    "Server unavailable, check connection",
                    "OK");
                    break;
                case HttpStatusCode.InternalServerError:
                    await DisplayAlert("",
                    "Server error, please try again",
                    "OK");
                    break;

            }
        }
    }
}
