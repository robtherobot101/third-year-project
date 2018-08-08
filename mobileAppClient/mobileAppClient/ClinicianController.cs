using mobileAppClient.Models;
using System;
using System.Collections.Generic;
using System.Text;

namespace mobileAppClient
{
    /*
     * Class to handle the logged in clinician of the application.
     * Accessible using a singleton framework with a given instance.
     */
    public sealed class ClinicianController
    {
        public Clinician LoggedInClinician { get; set; }
        public string AuthToken { get; set; }
        public MainPage mainPageController { get; set; } 
        public bool isTestMode { get; set; }

        private static readonly Lazy<ClinicianController> lazy =
        new Lazy<ClinicianController>(() => new ClinicianController());

        public static ClinicianController Instance { get { return lazy.Value; } }

        /*
         * Logs out the logged in clinician, setting the logged in user to null.
         */
        public void Logout()
        {
            this.LoggedInClinician = null;
            this.AuthToken = null;
        }

        public bool isLoggedIn()
        {
            if (this.LoggedInClinician != null)
            {
                return true;
            } else
            {
                return false;
            }
        }

        /*
         * Logs in a given user, updating all of its observers.
         */
        public void Login(Clinician loggedInClinician, string authToken)
        {
            this.LoggedInClinician = loggedInClinician;
            this.AuthToken = authToken;
            if (!isTestMode)
            {
                this.mainPageController.clinicianLoggedIn();
            }
        }

        private ClinicianController()
        {
            isTestMode = false;
        }
    }
}
