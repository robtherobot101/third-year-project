using System;
using System.Collections.Generic;
using System.Text;

namespace mobileAppClient
{
    /*
     * Class to handle the logged in user of the application.
     * Accessible using a singleton framework with a given instance.
     */
    sealed class UserController
    {
        public User LoggedInUser { get; set; }
        public string AuthToken { get; set; }
        public Photo photoObject { get; set; }
        public Xamarin.Forms.ImageSource ProfilePhotoSource { get; set; }

        private List<UserObserver> userObservers;

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
            this.photoObject = null;
            this.ProfilePhotoSource = null;
        }

        /*
         * Logs in a given user, updating all of its observers.
         */ 
        public void Login()
        {
            foreach (UserObserver userObserver in userObservers)
            {
                userObserver.updateUser();
            }
        }

        /*
         * Adds a given observer to the user.
         */ 
        public void addUserObserver(UserObserver newUserObserver)
        {
            userObservers.Add(newUserObserver);
        }

        private UserController()
        {
            userObservers = new List<UserObserver>();
        }
    }
}
