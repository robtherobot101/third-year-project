using System;
using System.Collections.Generic;
using System.Text;

namespace mobileAppClient
{
    sealed class UserController
    {
        public User LoggedInUser { get; set; }
        private String AuthToken { get; set; }

        private static readonly Lazy<UserController> lazy =
        new Lazy<UserController>(() => new UserController());

        public static UserController Instance { get { return lazy.Value; } }

        private UserController()
        {
        }
    }
}
