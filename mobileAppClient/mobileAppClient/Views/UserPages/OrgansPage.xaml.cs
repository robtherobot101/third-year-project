using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using mobileAppClient.odmsAPI;
using System.Net;
using mobileAppClient.Models;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the organs page for 
     * showing all of a users donated organs. 
     */ 
    public partial class OrgansPage : ContentPage 
    {
        private bool isClinicianEditing;
        /*
         * Constructor which sets all of the organ cells to be on or off
         * depending on which organs a user has donated.
         */ 
        public OrgansPage()
        {
            InitializeComponent();
            if (ClinicianController.Instance.isLoggedIn())
            {
                isClinicianEditing = true;
            }
            else
            {
                isClinicianEditing = false;
            }
            foreach (Organ item in UserController.Instance.LoggedInUser.organs) {
                Console.WriteLine(item);
                switch(item) {
                    case Organ.LIVER:
                        LiverCell.On = true;
                        break;
                    case Organ.KIDNEY:
                        KidneyCell.On = true;
                        break;
                    case Organ.PANCREAS:
                        PancreasCell.On = true;
                        break;
                    case Organ.HEART: 
                        HeartCell.On = true;
                        break;
                    case Organ.LUNG:
                        LungCell.On = true;
                        break;
                    case Organ.INTESTINE:
                        IntestineCell.On = true;
                        break;
                    case Organ.CORNEA:
                        CorneaCell.On = true;
                        break;
                    case Organ.EAR:
                        MiddleEarCell.On = true;
                        break;
                    case Organ.SKIN:
                        SkinCell.On = true;
                        break;
                    case Organ.BONE:
                        BoneMarrowCell.On = true;
                        break;
                    case Organ.TISSUE:
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
                UserController.Instance.LoggedInUser.organs.Add(Organ.LIVER);
            }
            if (KidneyCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add(Organ.KIDNEY);
            }
            if (PancreasCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add(Organ.PANCREAS);
            }
            if (HeartCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add(Organ.HEART);
            }
            if (LungCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add(Organ.LUNG);
            }
            if (IntestineCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add(Organ.INTESTINE);
            }
            if (CorneaCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add(Organ.CORNEA);
            }
            if (MiddleEarCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add(Organ.EAR);
            }
            if (SkinCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add(Organ.SKIN);
            }
            if (BoneMarrowCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add(Organ.BONE);
            }
            if (ConnectiveTissueCell.On)
            {
                UserController.Instance.LoggedInUser.organs.Add(Organ.TISSUE);
            }

            UserAPI userAPI = new UserAPI();
            HttpStatusCode userUpdated = await userAPI.UpdateUser(isClinicianEditing);

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
