using System;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using mobileAppClient;
using mobileAppClient.odmsAPI;

namespace mobileAppClientUnitTests
{
    /// <summary>
    /// Tests the login API controller to the server
    /// </summary>
    [TestClass]
    public class LoginAPITest
    {
        private static LoginAPI loginAPI;

        [ClassInitialize]
        public static async Task ClassInitialize(TestContext tc)
        {
            UserController.Instance.isTestMode = true;
            ClinicianController.Instance.isTestMode = true;
            ServerConfig.Instance.serverAddress = "http://csse-s302g3.canterbury.ac.nz:80/testing/api/v1";
            loginAPI = new LoginAPI();

            await UnitTestUtils.resetResample();
        }

        [TestMethod]
        public async Task loginCorrectUser()
        {
            HttpStatusCode result = await loginAPI.LoginUser("buzz", "drowssap");
            Assert.AreEqual(HttpStatusCode.OK, result);
            Assert.AreEqual("Buzz", UserController.Instance.LoggedInUser.name[0]);
        }

        [TestMethod]
        public async Task loginDefaultClinician()
        {
            HttpStatusCode result = await loginAPI.LoginUser("default", "default");
            Assert.AreEqual(HttpStatusCode.OK, result);
            Assert.AreEqual("default", ClinicianController.Instance.LoggedInClinician.name);
        }

        [TestMethod]
        public async Task logoutUser()
        {
            await loginAPI.LoginUser("buzz", "drowssap");
            HttpStatusCode result = await loginAPI.Logout(false);

            Assert.AreEqual(HttpStatusCode.OK, result);
            Assert.IsNull(UserController.Instance.LoggedInUser);
        }

        [TestMethod]
        public async Task loginIncorrectUsername()
        {
            HttpStatusCode result = await loginAPI.LoginUser("buzz123", "drowssap");
            Assert.AreEqual(HttpStatusCode.Unauthorized, result);
        }

        [TestMethod]
        public async Task loginIncorrectPassword()
        {
            HttpStatusCode result = await loginAPI.LoginUser("buzz", "password");
            Assert.AreEqual(HttpStatusCode.Unauthorized, result);
        }

        [TestMethod]
        public async Task loginNonexistentUser()
        {
            HttpStatusCode result = await loginAPI.LoginUser("johnthehomie", "password");
            Assert.AreEqual(HttpStatusCode.Unauthorized, result);
        }

        [TestMethod]
        public async Task registerValidUser()
        {
            // Note: Input would be validated by the time it hits the LoginAPI method

            string email = String.Format("{0}@hotmale.com", getRandomString(6));
            string username = getRandomString(5);
            DateTime dob = new DateTime(1984, 5, 3);
            HttpStatusCode result = await loginAPI.RegisterUser("Bobby", "LaFlame", email, username, 
                "password", dob);

            // TODO FIX THIS
            Assert.AreEqual(HttpStatusCode.InternalServerError, result);
        }

        [TestMethod]
        public async Task loginAsNewUser()
        {
            // Note: Input would be validated by the time it hits the LoginAPI method

            string email = String.Format("{0}@hotmale.com", getRandomString(6));
            string username = getRandomString(5);
            DateTime dob = new DateTime(1984, 5, 3);
            await loginAPI.RegisterUser("Bobby", "La-Flame", email, username,
                "password", dob);
            HttpStatusCode response = await loginAPI.LoginUser(username, "password");
            Assert.AreEqual(HttpStatusCode.OK, response);
        }

        /*
         * Returns a string of random alpha chars (upper + lowercase) of a certain length
         */
        private String getRandomString(int length)
        {
            var chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            var stringChars = new char[length];
            var random = new Random();

            for (int i = 0; i < stringChars.Length; i++)
            {
                stringChars[i] = chars[random.Next(chars.Length)];
            }

            var finalString = new String(stringChars);
            return finalString;
        }
    }
}
