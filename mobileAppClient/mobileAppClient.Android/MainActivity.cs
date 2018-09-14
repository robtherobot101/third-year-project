using System;

using Android.App;
using Android.Content;
using Android.Content.PM;
using Android.OS;
using System.Threading.Tasks;
using System.IO;
using Android.Media;
using Android.Graphics;
using Xamarin.Forms;
using Android.Support.V4.Content;
using Android;
using ImageCircle.Forms.Plugin.Droid;

using Firebase.Messaging;
using Firebase.Iid;
using Android.Util;

namespace mobileAppClient.Droid
{
    [Activity(Label = "mobileAppClient.Android", Icon = "@drawable/donationIcon", LaunchMode = LaunchMode.SingleInstance, Theme = "@style/MainTheme", MainLauncher = true, ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation)]

    [IntentFilter(new [] {Intent.ActionView}, Categories = new [] {Intent.CategoryDefault, Intent.CategoryBrowsable }, DataScheme = "https", DataHost = "csse-s302g3.canterbury.ac.nz", DataPath = "/oauth2redirect")]

    public class MainActivity : global::Xamarin.Forms.Platform.Android.FormsAppCompatActivity
    {

        static readonly string TAG = "MainActivity";

        internal static readonly string CHANNEL_ID = "my_notification_channel";
        internal static readonly int NOTIFICATION_ID = 100;

        internal static Context ActivityContext { get; private set; }

        protected override void OnCreate(Bundle bundle)
        {
            TabLayoutResource = Resource.Layout.Tabbar;
            ToolbarResource = Resource.Layout.Toolbar;

            base.OnCreate(bundle);

            global::Xamarin.Forms.Forms.Init(this, bundle);
            global::SegmentedControl.FormsPlugin.Android.SegmentedControlRenderer.Init();
            global::Plugin.CrossPlatformTintedImage.Android.TintedImageRenderer.Init();
            global::Xamarin.FormsMaps.Init(this, bundle);

            ActivityContext = this;

            // For circular images (on menu drawer)
            ImageCircleRenderer.Init();


            LoadApplication(new App());
        }

        protected override void OnActivityResult(int requestCode, Result resultCode, Intent data)
        {
            base.OnActivityResult(requestCode, resultCode, data);


            //Since we set the request code to 1 for both the camera and photo gallery, that's what we need to check for
            if (requestCode == 0)
            {
                if (resultCode == Result.Ok)
                {
                    Task.Run(() =>
                    {
                        if (App.ImageIdToSave != null)
                        {
                            var documentsDirectry = ActivityContext.GetExternalFilesDir(Android.OS.Environment.DirectoryPictures);
                            string pngFilename = System.IO.Path.Combine(documentsDirectry.AbsolutePath, App.ImageIdToSave + "." + FileFormatEnum.JPEG.ToString());

                            if (File.Exists(pngFilename))
                            {
                                Java.IO.File file = new Java.IO.File(documentsDirectry, App.ImageIdToSave + "." + FileFormatEnum.JPEG.ToString());
                                Android.Net.Uri uri = Android.Net.Uri.FromFile(file);

                                //Read the meta data of the image to determine what orientation the image should be in
                                var originalMetadata = new ExifInterface(pngFilename);
                                int orientation = GetRotation(originalMetadata);

                                var fileName = App.ImageIdToSave + "." + FileFormatEnum.JPEG.ToString();
                                HandleBitmap(uri, orientation, fileName);
                            }
                        }
                    });
                }
            }
            else if (requestCode == 1)
            {
                if (resultCode == Result.Ok)
                {
                    if (data.Data != null)
                    {

                        if (ContextCompat.CheckSelfPermission(this, Manifest.Permission.ReadExternalStorage) == (int)Permission.Granted)
                        {
                            
                            //Grab the Uri which is holding the path to the image
                            Android.Net.Uri uri = data.Data;

                            string fileName = null;

                            if (App.ImageIdToSave != null)
                            {
                                fileName = App.ImageIdToSave + "." + FileFormatEnum.JPEG.ToString();
                                var pathToImage = GetPathToImage(uri);
                                var originalMetadata = new ExifInterface(pathToImage);
                                int orientation = GetRotation(originalMetadata);

                                HandleBitmap(uri, orientation, fileName);
                            }
                        }
                        else
                        {
                            string[] PermissionsStorage =
                            {
                                Manifest.Permission.ReadExternalStorage,
                            };
                            const int RequestStorageId = 0;
                            RequestPermissions(PermissionsStorage, RequestStorageId);
                        }
                    }
                }
            }
        }

        /*
         *  https://stackoverflow.com/questions/26597811/xamarin-choose-image-from-gallery-path-is-null
         */

        private string GetPathToImage(Android.Net.Uri uri)
        {
            string doc_id = "";
            using (var c1 = ContentResolver.Query(uri, null, null, null, null))
            {
                c1.MoveToFirst();
                String document_id = c1.GetString(0);
                doc_id = document_id.Substring(document_id.LastIndexOf(":") + 1);
            }

            string path = null;

            // The projection contains the columns we want to return in our query.
            string selection = Android.Provider.MediaStore.Images.Media.InterfaceConsts.Id + " =? ";
            using (var cursor = ManagedQuery(Android.Provider.MediaStore.Images.Media.ExternalContentUri, null, selection, new string[] { doc_id }, null))
            {
                if (cursor == null) return path;
                var columnIndex = cursor.GetColumnIndexOrThrow(Android.Provider.MediaStore.Images.Media.InterfaceConsts.Data);
                cursor.MoveToFirst();
                path = cursor.GetString(columnIndex);
            }
            return path;
        }

        public int GetRotation(ExifInterface exif)
        {
            try
            {
                var orientation = (Android.Media.Orientation)exif.GetAttributeInt(ExifInterface.TagOrientation, (int)Android.Media.Orientation.Normal);

                switch (orientation)
                {
                    case Android.Media.Orientation.Rotate90:
                        return 90;
                    case Android.Media.Orientation.Rotate180:
                        return 180;
                    case Android.Media.Orientation.Rotate270:
                        return 270;
                    default:
                        return 0;
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
                return 0;
            }
        }

        public async Task HandleBitmap(Android.Net.Uri uri, int orientation, string imageId)
        {
            try
            {
                Bitmap mBitmap = Android.Provider.MediaStore.Images.Media.GetBitmap(this.ContentResolver, uri);
                Bitmap myBitmap = null;

                if (mBitmap != null)
                {
                    //In order to rotate the image we create a Matrix object, rotate if the image is not already in it's correct orientation
                    Matrix matrix = new Matrix();
                    if (orientation != 0)
                    {
                        matrix.PreRotate(orientation);
                    }

                    Console.WriteLine("About to rotate");
                    myBitmap = Bitmap.CreateBitmap(mBitmap, 0, 0, mBitmap.Width, mBitmap.Height, matrix, true);

                    MemoryStream stream = new MemoryStream();

                    Console.WriteLine("About to compress");
                    //Compressing by 50%, feel free to change if file size is not a factor
                    myBitmap.Compress(Bitmap.CompressFormat.Jpeg, 50, stream);

                    Console.WriteLine("About to convert to byte array");
                    byte[] bitmapData = stream.ToArray();

                    //Send image byte array back to UI
                    Console.WriteLine("About to send Image back to UI");

                    if (imageId != null && imageId != "")
                    {
                        SavePictureToDisk(myBitmap, imageId);
                    }

                    MessagingCenter.Send<byte[]>(bitmapData, "ImageSelected");
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
            }
        }

        public void SavePictureToDisk(Bitmap source, string imageName)
        {
            try
            {
                Task.Run(() =>
                {
                    var documentsDirectry = ActivityContext.GetExternalFilesDir(Android.OS.Environment.DirectoryPictures);
                    string pngFilename = System.IO.Path.Combine(documentsDirectry.AbsolutePath, imageName);

                    //If the image already exists, delete, and make way for the updated one
                    if (File.Exists(pngFilename))
                    {
                        File.Delete(pngFilename);
                    }

                    using (FileStream fs = new FileStream(pngFilename, FileMode.OpenOrCreate))
                    {
                        source.Compress(Bitmap.CompressFormat.Jpeg, 50, fs);
                        fs.Close();
                    }

                    Console.WriteLine("Saved photo");
                });
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }

        /// <summary>
        /// Captures the redirect URI from the Google login
        /// </summary>
        /// <param name="intent"></param>
        protected override async void OnNewIntent(Intent intent)
        {
            if (intent.Data != null)
            {
                var data = intent.Data;

                string queryParameter = data.GetQueryParameter("code");
                await UserController.Instance.PassControlToLoginPage(queryParameter);
            }
        }
    }
}

