using System;
using System.Collections.Generic;
using System.Text;

namespace mobileAppClient.odmsAPI.RequestFormat
{
    /*
     * Used to structure a POST /users request for JSON serialisation
     */
    class RegisterRequest
    {
        public String[] names = new String[3];
        public String username { get; set; }
        public String email { get; set; }
        public String password { get; set; }
        public String dateOfBirth { get; set; }
    }
}
