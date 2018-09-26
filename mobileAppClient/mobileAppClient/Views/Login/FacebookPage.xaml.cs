using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Http;
using System.Threading;
using System.Threading.Tasks;
using mobileAppClient.Models;
using mobileAppClient.odmsAPI;
using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class FacebookPage : ContentPage
    {
        private LoginPage parentLoginPage;
        private FacebookServices facebookServices;
        private int userId;
        private User accountBeingChanged;
        public FacebookPage(LoginPage loginPage)
        {
            InitializeComponent();
            this.parentLoginPage = loginPage;
            facebookServices = new FacebookServices();

            var webView = new WebView
            {
                Source = facebookServices.apiRequest,
                HeightRequest = 1
            };

            webView.Navigated += WebViewOnNavigated;
            Content = webView;
        }

        public FacebookPage(int userId)
        {
            InitializeComponent();
            facebookServices = new FacebookServices();
            this.userId = userId;
            var webView = new WebView
            {
                Source = facebookServices.apiRequest,
                HeightRequest = 1
            };

            webView.Navigated += ChangeLoginMethodOnNavigate;
            Content = webView;
        }

        private async void ChangeLoginMethodOnNavigate(object sender, WebNavigatedEventArgs e)
        {
            //Console.WriteLine("User ID is: " + userId);
            var accessToken = facebookServices.ExtractAccessTokenFromUrl(e.Url);
            if (accessToken != "")
            {
                FacebookProfile facebookProfile = await facebookServices.GetFacebookProfileAsync(accessToken);

                HttpStatusCode facebookRegisterStatus = await new LoginAPI().FacebookRegisterUser(userId, facebookProfile.Id);

                switch (facebookRegisterStatus)
                {
                    case HttpStatusCode.Created:
                        await DisplayAlert("",
                        "Login method changed to Facebook",
                        "OK");
                        break;
                    case HttpStatusCode.BadRequest:
                        await DisplayAlert("",
                        "Failed to change login method (400)",
                        "OK");
                        break;
                    case HttpStatusCode.ServiceUnavailable:
                        await DisplayAlert("",
                        "Server unavailable, check connection",
                        "OK");
                        break;
                    case HttpStatusCode.Unauthorized:
                        await DisplayAlert("",
                        "Unauthorised to modify profile",
                        "OK");
                        break;
                    case HttpStatusCode.InternalServerError:
                        await DisplayAlert("",
                        "Server error, please try again (500)",
                        "OK");
                        break;
                }
            }
            await Navigation.PopModalAsync();
        }

        async void Handle_CancelClicked(object sender, System.EventArgs e)
        {
            await Navigation.PopModalAsync();
        }

        private async void WebViewOnNavigated(object sender, WebNavigatedEventArgs e)
        {
            var accessToken = facebookServices.ExtractAccessTokenFromUrl(e.Url);

            if (accessToken != "")
            {
                FacebookProfile facebookProfile = await facebookServices.GetFacebookProfileAsync(accessToken);
                string password = "password";

                UserAPI userAPI = new UserAPI();
                LoginAPI loginAPI = new LoginAPI();

                //Do a check to see if user is already in the database - if they are then skip the register and go to login if not just login
                Tuple<HttpStatusCode, bool> isUniqueEmailResult = await userAPI.isUniqueApiId(facebookProfile.Id);
                if (isUniqueEmailResult.Item1 != HttpStatusCode.OK)
                {
                    Console.WriteLine("Failed to connect to server for checking of unique email");
                }

                //This account already exists
                if (isUniqueEmailResult.Item2 == false)
                {
                    HttpStatusCode statusCode = await loginAPI.LoginUser(facebookProfile.Id);
                    switch (statusCode)
                    {
                        case HttpStatusCode.OK:
                            // Pop away login screen on successful login
                            HttpStatusCode httpStatusCode = await userAPI.GetUserPhoto();
                            UserController.Instance.mainPageController.updateMenuPhoto();
                            await Navigation.PopModalAsync();
                            await parentLoginPage.OpenMainPageFromSignUp();
                            break;
                        case HttpStatusCode.Unauthorized:
                            await DisplayAlert(
                                "Failed to Login",
                                "Incorrect username/password",
                                "OK");
                            break;
                        case HttpStatusCode.ServiceUnavailable:
                            await DisplayAlert(
                                "Failed to Login",
                                "Server unavailable, check connection",
                                "OK");
                            break;
                        case HttpStatusCode.InternalServerError:
                            await DisplayAlert(
                                "Failed to Login",
                                "Server error",
                                "OK");
                            break;
                    }
                }
                else //Create new account
                {

                    var waitHandle = new EventWaitHandle(false, EventResetMode.AutoReset);
                    var modalPage = new NavigationPage(new IncompleteFacebookDetailsPage(facebookProfile));
                    modalPage.Disappearing += (sender2, e2) =>
                    {
                        waitHandle.Set();
                    };


                    await Navigation.PushModalAsync(modalPage);
                    await Task.Run(() => waitHandle.WaitOne());
                    facebookProfile.Email = UserController.Instance.FacebookEmail;
                    facebookProfile.Birthday = UserController.Instance.FacebookDateOfBirth;
                    facebookProfile.NHI = UserController.Instance.NHI;

                    User inputUser = new User
                    {
                        name = new List<string> {facebookProfile.FirstName, "", facebookProfile.LastName},
                        preferredName = new List<string> {facebookProfile.FirstName, "", facebookProfile.LastName},
                        email = facebookProfile.Email,
                        username = facebookProfile.Email,
                        password = password,
                        dateOfBirth = new CustomDate(DateTime.Parse(facebookProfile.Birthday)),
                        creationTime = new CustomDateTime(DateTime.Now),
                        gender = facebookProfile.Gender.ToUpper(),
                        region = facebookProfile.Location.Name,
                        organs = new List<Organ>(),
                        userHistory = new List<HistoryItem>(),
                        nhi = facebookProfile.NHI
                    };

                    //Server requires to initialise the organs and user history items on creation

                    HttpStatusCode registerUserResult = await loginAPI.RegisterUser(inputUser);
                    
                    switch (registerUserResult)
                    {
                        case HttpStatusCode.Created:
                            HttpStatusCode registerFacebookUserResult =
                                await loginAPI.FacebookRegisterUser(inputUser.id, facebookProfile.Id);

                            //Set the local profile picture to the picture object

                            HttpClient client = ServerConfig.Instance.client;
                            // Get the single userController instance
                            UserController userController = UserController.Instance;

                            var bytes = await client.GetByteArrayAsync(facebookProfile.Picture.Data.Url);
                            Photo receievedPhoto = new Photo(Convert.ToBase64String(bytes));
                            userController.photoObject = receievedPhoto;

                            ImageSource source = ImageSource.FromStream(() => new MemoryStream(bytes));

                            userController.ProfilePhotoSource = source;

                            HttpStatusCode loginUserResult = await loginAPI.LoginUser(facebookProfile.Id);
                            switch (loginUserResult)
                            {
                                case HttpStatusCode.OK:
                                    //Upload the photo to the server - must happen after a login as the token is required
                                    HttpStatusCode photoUpdated = await userAPI.UpdateUserPhoto();
                                    if (photoUpdated != HttpStatusCode.OK)
                                    {
                                        Console.WriteLine("Error uploading facebook photo to the server");
                                    }
                                    await Navigation.PopModalAsync();
                                    await parentLoginPage.OpenMainPageFromSignUp();
                                    break;
                                case HttpStatusCode.Unauthorized:
                                    await DisplayAlert(
                                        "Failed to Login",
                                        "Incorrect username/password",
                                        "OK");
                                    break;
                                case HttpStatusCode.ServiceUnavailable:
                                    await DisplayAlert(
                                        "Failed to Login",
                                        "Server unavailable, check connection",
                                        "OK");
                                    break;
                                case HttpStatusCode.InternalServerError:
                                    await DisplayAlert(
                                        "Failed to Login",
                                        "Server error",
                                        "OK");
                                    break;
                            }
                            break;
                        case HttpStatusCode.ServiceUnavailable:
                            await DisplayAlert(
                                "Failed to Register",
                                "Server unavailable, check connection",
                                "OK");
                            break;
                        case HttpStatusCode.InternalServerError:
                            await DisplayAlert(
                                "Failed to Register",
                                "Username/Email may be taken",
                                "OK");
                            break;
                    }
                }
            }
        }
    }
}
