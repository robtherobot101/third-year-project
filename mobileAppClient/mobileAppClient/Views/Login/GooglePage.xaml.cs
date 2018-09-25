using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using mobileAppClient.Google;
using mobileAppClient.odmsAPI;
using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class GooglePage : ContentPage
    {
        private LoginPage parentLoginPage;
        private User googleUser;
        private string profileImageURL;

        private bool firstNameNeeded;
        private bool lastNameNeeded;

        public GooglePage(LoginPage loginPage, User googleUser, string profileImageURL)
        {
            InitializeComponent();
            this.googleUser = googleUser;
            this.profileImageURL = profileImageURL;
            this.parentLoginPage = loginPage;

            if (String.IsNullOrEmpty(googleUser.name[0]))
            {
                firstNameInput.IsVisible = true;
                firstNameNeeded = true;
            }

            if (String.IsNullOrEmpty(googleUser.name[2]))
            {
                lastNameInput.IsVisible = true;
                lastNameNeeded = true;
            }
        }


        async void Handle_CancelClicked(object sender, System.EventArgs e)
        {
            await Navigation.PopModalAsync();
        }

        public async Task RegisterNewUser()
        {
            UserAPI userAPI = new UserAPI();
            LoginAPI loginAPI = new LoginAPI();

            HttpStatusCode registerUserResult = await loginAPI.RegisterUser(googleUser);

            switch (registerUserResult)
            {
                case HttpStatusCode.Created:
                    //Set the local profile picture to the picture object

                    HttpClient client = ServerConfig.Instance.client;
                    // Get the single userController instance
                    UserController userController = UserController.Instance;

                    var bytes = await client.GetByteArrayAsync(profileImageURL);
                    Photo receievedPhoto = new Photo(Convert.ToBase64String(bytes));
                    userController.photoObject = receievedPhoto;

                    ImageSource source = ImageSource.FromStream(() => new MemoryStream(bytes));

                    userController.ProfilePhotoSource = source;

                    HttpStatusCode loginUserResult = await loginAPI.LoginUser(googleUser.email, googleUser.password);
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
                            await parentLoginPage.Navigation.PopModalAsync();
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

        private async void Button_OnClicked(object sender, EventArgs e)
        {
            if (!InputValidation.IsValidNhiInput(NHIEntry.Text))
            {
                await DisplayAlert("", "Please enter a valid NHI number", "OK");
                return;
            }

            if (BirthGenderInput.SelectedItem == null)
            {
                await DisplayAlert("", "Please enter a gender", "OK");
                return;
            }

            if (firstNameNeeded)
            {
                if (!InputValidation.IsValidTextInput(firstNameInput.Text, false, false))
                {
                    await DisplayAlert("",
                        "Please enter a valid first name",
                        "OK");
                    return;
                }
                else
                {
                    googleUser.name[0] = firstNameInput.Text;
                    googleUser.preferredName[0] = firstNameInput.Text;
                }
            }

            if (lastNameNeeded)
            {
                if (!InputValidation.IsValidTextInput(lastNameInput.Text, false, false))
                {
                    await DisplayAlert("",
                        "Please enter a valid last name",
                        "OK");
                    return;
                }
                else
                {
                    googleUser.name[2] = lastNameInput.Text;
                    googleUser.preferredName[2] = lastNameInput.Text;
                }
            }

            googleUser.nhi = NHIEntry.Text;
            googleUser.gender = BirthGenderInput.SelectedItem.ToString().ToUpper();
            googleUser.dateOfBirth = new CustomDate(dobInput.Date);
            await RegisterNewUser();
        }
    }
}
