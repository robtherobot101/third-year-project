using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using Xamarin.Forms;
using mobileAppClient.odmsAPI;

namespace mobileAppClient
{
    public partial class PhotoSettingsPage : ContentPage
    {
        public string photoBase64String { get; set; }

        public PhotoSettingsPage(bool FromMainMenu)
        {
            InitializeComponent();
            if(UserController.Instance.ProfilePhotoSource != null) {
                imageView.Source = UserController.Instance.ProfilePhotoSource;
                DeletePhotoCell.IsEnabled = true;
                NoPhotoLabel.IsVisible = false;

            }
            //Whenever we send the byte array, we Sent the byte array with the string "ImageSelected", so we have to subscribe
            //with the same format of a byte array and that string.
            MessagingCenter.Subscribe<byte[]>(this, "ImageSelected", (args) =>
            {
                photoBase64String = Convert.ToBase64String((byte[])args);
                Device.BeginInvokeOnMainThread(() =>
                {
                    //Set the source of the image view with the byte array
                    imageView.Source = ImageSource.FromStream(() => new MemoryStream((byte[])args));
                    save();
                });
            });
            if(FromMainMenu){
                ToolbarItems.Add(new ToolbarItem("Back", null, () =>
                {
                    Navigation.PopModalAsync();
                }));
            }
        }

        public async void SelectImageClicked(object sender, EventArgs args)
        {
            var action = await DisplayActionSheet("Add Photo", "Cancel", null, "Choose Existing", "Take Photo");

            if (action == "Choose Existing")
            {
                Device.BeginInvokeOnMainThread(() =>
                {
                    var fileName = SetImageFileName();
                    DependencyService.Get<CameraInterface>().LaunchGallery(FileFormatEnum.JPEG, fileName);
                });
            }
            else if (action == "Take Photo")
            {
                Device.BeginInvokeOnMainThread(() =>
                {
                    var fileName = SetImageFileName();
                    DependencyService.Get<CameraInterface>().LaunchCamera(FileFormatEnum.JPEG, fileName);
                });
            }
        }

        /*
         *  Setting the file name is really only needed for Android, when in the OnActivityResult method you need
         *  a way to know the file name passed into the intent when launching the camera/gallery. In this case,
         *  1 image will be saved to the file system using the value of App.DefaultImageId, this is required for the 
         *  FileProvider implemenation that is needed on newer Android OS versions. Using the same file name will 
         *  keep overwriting the existing image so you will not fill up the app's memory size over time. 
         * 
         *  This of course assumes your app has NO need to save images locally. But if your app DOES need to save images 
         *  locally, then pass the file name you want to use into the method SetImageFileName (do NOT include the file extension in the name,
         *  that will be handled down the road based on the FileFormatEnum you pick). 
         * 
         *  NOTE: When saving images, if you decide to pick PNG format, you may notice your app runs slower 
         *  when processing the image. If your image doesn't need to respect any Alpha values, use JPEG, it's faster. 
         */

        private string SetImageFileName(string fileName = null)
        {
            if (Device.RuntimePlatform == Device.Android)
            {
                if (fileName != null)
                    App.ImageIdToSave = fileName;
                else
                    App.ImageIdToSave = App.DefaultImageId;

                return App.ImageIdToSave;
            }
            else
            {
                //To iterate, on iOS, if you want to save images to the devie, set 
                if (fileName != null)
                {
                    App.ImageIdToSave = fileName;
                    return fileName;
                }
                else
                    return null;
            }
        }

        async void save()
        {
            Photo uploadedPhoto = new Photo(photoBase64String);
            UserController.Instance.photoObject = uploadedPhoto;
            UserAPI userAPI = new UserAPI();
            DeletePhotoCell.IsEnabled = true;
            NoPhotoLabel.IsVisible = false;
            HttpStatusCode photoUpdated = await userAPI.UpdateUserPhoto();

            switch (photoUpdated)
            {
                case HttpStatusCode.OK:
                    UserController.Instance.ProfilePhotoSource = imageView.Source;
                    UserController.Instance.mainPageController.updateMenuPhoto();
                    await DisplayAlert("",
                    "User photo successfully updated",
                    "OK");
                    break;
                case HttpStatusCode.BadRequest:
                    DeletePhotoCell.IsEnabled = false;
                    NoPhotoLabel.IsVisible = true;
                    await DisplayAlert("",
                    "User photo update failed (400)",
                    "OK");
                    break;
                case HttpStatusCode.ServiceUnavailable:
                    DeletePhotoCell.IsEnabled = false;
                    NoPhotoLabel.IsVisible = true;
                    await DisplayAlert("",
                    "Server unavailable, check connection",
                    "OK");
                    break;
                case HttpStatusCode.Unauthorized:
                    DeletePhotoCell.IsEnabled = false;
                    NoPhotoLabel.IsVisible = true;
                    await DisplayAlert("",
                    "Unauthorised to modify photo",
                    "OK");
                    break;
                case HttpStatusCode.InternalServerError:
                    DeletePhotoCell.IsEnabled = false;
                    NoPhotoLabel.IsVisible = true;
                    await DisplayAlert("",
                    "Server error, please try again (500)",
                    "OK");
                    break;
            }
        }

        async void Handle_DeleteTapped(object sender, System.EventArgs e)
        {
            UserAPI userAPI = new UserAPI();
            HttpStatusCode photoUpdated = await userAPI.DeleteUserPhoto();

            switch (photoUpdated)
            {
                case HttpStatusCode.OK:
                    DeletePhotoCell.IsEnabled = false;
                    NoPhotoLabel.IsVisible = true;
                    imageView.Source = null;
                    UserController.Instance.ProfilePhotoSource = null;
                    UserController.Instance.mainPageController.updateMenuPhoto();
                    await DisplayAlert("",
                    "User photo successfully deleted",
                    "OK");
                    break;
                case HttpStatusCode.BadRequest:
                    await DisplayAlert("",
                    "User photo delete failed (400)",
                    "OK");
                    break;
                case HttpStatusCode.ServiceUnavailable:
                    await DisplayAlert("",
                    "Server unavailable, check connection",
                    "OK");
                    break;
                case HttpStatusCode.Unauthorized:
                    await DisplayAlert("",
                    "Unauthorised to modify photo",
                    "OK");
                    break;
                case HttpStatusCode.InternalServerError:
                    await DisplayAlert("",
                    "Server error, please try again (500)",
                    "OK");
                    break;
            }
        }

    }
}
