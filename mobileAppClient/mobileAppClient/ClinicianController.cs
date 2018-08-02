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
    sealed class ClinicianController
    {
        public Clinician LoggedInClinician { get; set; }
        public string AuthToken { get; set; }

        private static readonly Lazy<ClinicianController> lazy =
        new Lazy<ClinicianController>(() => new ClinicianController());

        public static ClinicianController Instance { get { return lazy.Value; } }

        /*
         * Logs out a given user, setting the logged in user to null.
         */
        public void Logout()
        {
            this.LoggedInClinician = null;
            this.AuthToken = null;
        }

        private ClinicianController()
        {
   
        }
    }
}
