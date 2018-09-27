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
        async void Handle_SavePressed(object sender, EventArgs e)
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

        /*
         * Retrieves all organs for the user
         */
        private async Task<List<DonatableOrgan>> GetOrgans()
        {
            TransplantListAPI transplantListAPI = new TransplantListAPI();
            List<DonatableOrgan> donatableOrgans = await transplantListAPI.GetSingleUsersDonatableOrgans(UserController.Instance.LoggedInUser.id);
            return donatableOrgans;
        }

        /*
         * When the page appears, the state of the controls are reset
         */
        protected override async void OnAppearing()
        { 
            List<DonatableOrgan> donatableOrgans = await GetOrgans();
            foreach (Organ item in UserController.Instance.LoggedInUser.organs)
            {
                Console.WriteLine(item);
                switch (item)
                {
                    case Organ.LIVER:
                        LiverCell.On = true;
                        foreach (DonatableOrgan organ in donatableOrgans)
                        {
                            if (OrganExtensions.ToOrgan(organ.organType) == item && (organ.inTransfer != 0 || organ.expired))
                            {
                                LiverCell.IsEnabled = false;
                            }
                        }
                        break;
                    case Organ.KIDNEY:
                        KidneyCell.On = true;
                        foreach (DonatableOrgan organ in donatableOrgans)
                        {
                            if (OrganExtensions.ToOrgan(organ.organType) == item && (organ.inTransfer != 0 || organ.expired))
                            {
                                KidneyCell.IsEnabled = false;
                            }
                        }
                        break;
                    case Organ.PANCREAS:
                        PancreasCell.On = true;
                        foreach (DonatableOrgan organ in donatableOrgans)
                        {
                            if (OrganExtensions.ToOrgan(organ.organType) == item && (organ.inTransfer != 0 || organ.expired))
                            {
                                PancreasCell.IsEnabled = false;
                            }
                        }
                        break;
                    case Organ.HEART:
                        HeartCell.On = true;
                        foreach (DonatableOrgan organ in donatableOrgans)
                        {
                            if (OrganExtensions.ToOrgan(organ.organType) == item && (organ.inTransfer != 0 || organ.expired))
                            {
                                HeartCell.IsEnabled = false;
                            }
                        }
                        break;
                    case Organ.LUNG:
                        LungCell.On = true;
                        foreach (DonatableOrgan organ in donatableOrgans)
                        {
                            if (OrganExtensions.ToOrgan(organ.organType) == item && (organ.inTransfer != 0 || organ.expired))
                            {
                                LungCell.IsEnabled = false;
                            }
                        }
                        break;
                    case Organ.INTESTINE:
                        IntestineCell.On = true;
                        foreach (DonatableOrgan organ in donatableOrgans)
                        {
                            if (OrganExtensions.ToOrgan(organ.organType) == item && (organ.inTransfer != 0 || organ.expired))
                            {
                                IntestineCell.IsEnabled = false;
                            }
                        }
                        break;
                    case Organ.CORNEA:
                        CorneaCell.On = true;
                        foreach (DonatableOrgan organ in donatableOrgans)
                        {
                            if (OrganExtensions.ToOrgan(organ.organType) == item && (organ.inTransfer != 0 || organ.expired))
                            {
                                CorneaCell.IsEnabled = false;
                            }
                        }
                        break;
                    case Organ.EAR:
                        MiddleEarCell.On = true;
                        foreach (DonatableOrgan organ in donatableOrgans)
                        {
                            if (OrganExtensions.ToOrgan(organ.organType) == item && (organ.inTransfer != 0 || organ.expired))
                            {
                                MiddleEarCell.IsEnabled = false;
                            }
                        }
                        break;
                    case Organ.SKIN:
                        SkinCell.On = true;
                        foreach (DonatableOrgan organ in donatableOrgans)
                        {
                            if (OrganExtensions.ToOrgan(organ.organType) == item && (organ.inTransfer != 0 || organ.expired))
                            {
                                SkinCell.IsEnabled = false;
                            }
                        }
                        break;
                    case Organ.BONE:
                        BoneMarrowCell.On = true;
                        foreach (DonatableOrgan organ in donatableOrgans)
                        {
                            if (OrganExtensions.ToOrgan(organ.organType) == item && (organ.inTransfer != 0 || organ.expired))
                            {
                                BoneMarrowCell.IsEnabled = false;
                            }
                        }
                        break;
                    case Organ.TISSUE:
                        ConnectiveTissueCell.On = true;
                        foreach (DonatableOrgan organ in donatableOrgans)
                        {
                            if (OrganExtensions.ToOrgan(organ.organType) == item && (organ.inTransfer != 0 || organ.expired))
                            {
                                ConnectiveTissueCell.IsEnabled = false;
                            }
                        }
                        break;
                }
            }
        }
    }
}
