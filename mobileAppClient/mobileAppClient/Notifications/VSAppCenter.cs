using Microsoft.AppCenter;
using Microsoft.AppCenter.Analytics;
using Microsoft.AppCenter.Crashes;
using Microsoft.AppCenter.Push;
using System;
using System.Collections.Generic;
using System.Dynamic;
using System.Text;
using Xamarin.Forms;

namespace mobileAppClient.Notifications
{
    class VSAppCenter
    {
        /**
         * Create a dynamic object from a dictionary
         **/ 
        private static dynamic DictionaryToObject(IDictionary<String, Object> dictionary)
        {
            var expandoObj = new ExpandoObject();
            var expandoObjCollection = (ICollection<KeyValuePair<String, Object>>)expandoObj;

            foreach (var keyValuePair in dictionary)
            {
                expandoObjCollection.Add(keyValuePair);
            }
            dynamic eoDynamic = expandoObj;
            return eoDynamic;
        }

        /**
         * Create an instance of the given object, given a dictionary
         **/
        private static T DictionaryToObject<T>(IDictionary<String, Object> dictionary) where T : class
        {
            return DictionaryToObject(dictionary) as T;
        }

        public async static void Setup()
        {

            // This should come before AppCenter.Start() is called
            // Avoid duplicate event registration:
            if (!AppCenter.Configured)
            {
                Push.PushNotificationReceived += (sender, e) =>
                {
                    if (e.CustomData.ContainsKey("message"))
                    {
                        Message message = DictionaryToObject((IDictionary<String, Object>) e.CustomData);
                        System.Diagnostics.Debug.WriteLine(message.id);
                        System.Diagnostics.Debug.WriteLine(message.text);
                        System.Diagnostics.Debug.WriteLine(message.timestamp);
                        System.Diagnostics.Debug.WriteLine(message.userId);
                    }
                };
            }

            AppCenter.Start("android=95e48718-8158-4eef-ab58-fac02629e859;" +
                            "ios=14d06e7a-6ff3-4e01-8838-59cbb905dbc2;",
                            typeof(Analytics), typeof(Crashes), typeof(Push));
            var id = await AppCenter.GetInstallIdAsync();
            System.Diagnostics.Debug.WriteLine(id.ToString());

        }
    }
}
