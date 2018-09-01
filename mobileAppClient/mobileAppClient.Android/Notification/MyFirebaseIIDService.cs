using System;
using Android.App;
using Firebase.Iid;
using Android.Util;
using System.Threading.Tasks;
using Microsoft.WindowsAzure.MobileServices;
using mobileAppClient.Notifications;

namespace mobileAppClient.Droid.Notification
{
    [Service]
    [IntentFilter(new[] { "com.google.firebase.INSTANCE_ID_EVENT" })]
    public class MyFirebaseIIDService : FirebaseInstanceIdService
    {
        const string TAG = "MyFirebaseIIDService";

        public override void OnTokenRefresh()
        {
            var refreshedToken = FirebaseInstanceId.Instance.Token;
            Log.Debug(TAG, "Refreshed token: " + refreshedToken);
            SendRegistrationToServer(refreshedToken);
        }
        void SendRegistrationToServer(string token)
        {
            // Add custom implementation, as needed.
            // This is where we send the token to our server
            Task.Run(async () =>
            {
                await AzureNotificationHubService.RegisterAsync(Azure.Instance.CurrentClient.GetPush(), token);
            });
        }
        public static string GetToken()
        {
            return FirebaseInstanceId.Instance.Token;
        }
    }
}