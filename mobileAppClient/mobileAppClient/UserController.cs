using System;
using System.Collections.Generic;
using System.Text;

namespace mobileAppClient
{
    /*
     * Class to handle the logged in user of the application.
     * Accessible using a singleton framework with a given instance.
     */ 
    public sealed class UserController
    {
        public User LoggedInUser { get; set; }
        public string AuthToken { get; set; }
        public MainPage mainPageController { get; set; }
        public bool isTestMode { get; set; }

        private static readonly Lazy<UserController> lazy =
        new Lazy<UserController>(() => new UserController());

        public static UserController Instance { get { return lazy.Value; } }

        /*
         * Logs out a given user, setting the logged in user to null.
         */ 
        public void Logout()
        {
            this.LoggedInUser = null;
            this.AuthToken = null;
        }

        /*
         * Logs in a given user, updating the main view to be for a user
         */ 
        public void Login(User loggedInUser, string authToken)
        {
            this.LoggedInUser = loggedInUser;
            this.AuthToken = authToken;
            if (!isTestMode)
            {
                this.mainPageController.userLoggedIn();
            }
        }

        private UserController()
        {
            isTestMode = false;
        }
    }
}
