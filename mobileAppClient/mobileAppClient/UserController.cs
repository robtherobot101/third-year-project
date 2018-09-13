using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Essentials;
using Xamarin.Forms;

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
        public Photo photoObject { get; set; }
        public ImageSource ProfilePhotoSource { get; set; }
        public string FacebookEmail { get; set; }
        public string FacebookDateOfBirth { get; set; }
        public MainPage mainPageController { get; set; }
        public LoginPage loginPageController { get; set; }

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

            SecureStorage.RemoveAll();
        }

        /*
         * Logs in a given user, updating the main view to be for a user
         */ 
        public void Login(User loggedInUser, string authToken)
        {
            this.LoggedInUser = loggedInUser;
            this.AuthToken = authToken;

            if (Instance.loginPageController.rememberLogin)
            {
                SecureStorage.SetAsync("usernameEmail", loggedInUser.email);
                SecureStorage.SetAsync("password", loggedInUser.password);
            }           
        }

        /// <summary>
        /// Used to return control to the Login page after a Google login
        /// </summary>
        /// <param name="code"></param>
        /// <returns></returns>
        public async Task PassControlToLoginPage(string code)
        {
            await loginPageController.Handle_RedirectUriCaught(code);
        }

        private UserController()
        {
        }
    }
}
