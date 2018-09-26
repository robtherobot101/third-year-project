using System;
using System.Collections.Generic;

namespace mobileAppClient.Google
{
    public class GoogleTokenResponse
    {
        public string id { get; set; }
        public string access_token { get; set; }
        public List<Email> emails { get; set; }
        public Name name { get; set; }
        public Image image { get; set; }


        public class Name
        {
            public string givenName { get; set; }
            public string familyName { get; set; }

        }

        public class Image
        {
            public string url { get; set; }
        }

        public class Email
        {
            public string value { get; set; }
        }

        public GoogleTokenResponse()
        {
        }
    }
}

