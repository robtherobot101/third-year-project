using System;
using System.Collections.Generic;
using System.Text;

namespace mobileAppClient.Google
{
    class GoogleServices
    {
        public string apiRequest;

        public GoogleServices()
        {
            string redirect_uri = "http://csse-s302g3.canterbury.ac.nz/";
            string client_id = "990254303378-v3ma54dlo3dpdee4mdh3481dtmelemgb.apps.googleusercontent.com";
            apiRequest =
                $"https://accounts.google.com/o/oauth2/v2/auth?response_type=code&scope=openid&redirect_uri={redirect_uri}&client_id={client_id}";
        }
}
}
