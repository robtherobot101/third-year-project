using System;
using System.Text;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using mobileAppClient.odmsAPI;
using mobileAppClient;
using System.Threading.Tasks;
using System.Net;
using System.Net.Http;

namespace mobileAppClientUnitTests
{
    /// <summary>
    /// Summary description for UnitTest1
    /// </summary>
    [TestClass]
    public class UserAPITest
    {
        private static LoginAPI loginAPI;
        private static UserAPI userAPI;

        [ClassInitialize]
        public static async Task ClassInitialize(TestContext tc)
        {
            UserController.Instance.isTestMode = true;
            ClinicianController.Instance.isTestMode = true;
            ServerConfig.Instance.serverAddress = "http://csse-s302g3.canterbury.ac.nz:80/testing/api/v1";
            loginAPI = new LoginAPI();
            userAPI = new UserAPI();

            await UnitTestUtils.resetResample();
        }

        private TestContext testContextInstance;

        /// <summary>
        ///  Gets or sets the test context which provides
        ///  information about and functionality for the current test run.
        ///</summary>
        public TestContext TestContext
        {
            get { return testContextInstance; }
            set { testContextInstance = value; }
        }

        #region Additional test attributes
        //
        // You can use the following additional attributes as you write your tests:
        //
        // Use ClassInitialize to run code before running the first test in the class
        // [ClassInitialize()]
        // public static void MyClassInitialize(TestContext testContext) { }
        //
        // Use ClassCleanup to run code after all tests in a class have run
        // [ClassCleanup()]
        // public static void MyClassCleanup() { }
        //
        // Use TestInitialize to run code before running each test 
        // [TestInitialize()]
        // public void MyTestInitialize() { }
        //
        // Use TestCleanup to run code after each test has run
        // [TestCleanup()]
        // public void MyTestCleanup() { }
        //
        #endregion
        /// <summary>
        /// This is a method
        /// </summary>
        [TestMethod]
        public async Task updateUser()
        {
            await loginAPI.LoginUser("buzz", "drowssap");
            UserController.Instance.LoggedInUser.name[0] = "Buzzy";

            HttpStatusCode result = await userAPI.UpdateUser(false);
            Assert.AreEqual(HttpStatusCode.Created, result);

            UserController.Instance.Logout();
            await loginAPI.LoginUser("buzz", "drowssap");
            Assert.AreEqual("Buzzy", UserController.Instance.LoggedInUser.name[0]);
        }

        [TestMethod]
        public async Task getUsers()
        {
            // Check auth
            if (ClinicianController.Instance.AuthToken == null)
            {
                // Login as clinician to grab token with getUsers capability
                await loginAPI.LoginUser("default", "default");
            }

            Tuple<HttpStatusCode, List<User>> result = await userAPI.GetUsers(0, 9, "");
            Assert.AreEqual(HttpStatusCode.OK, result.Item1);
            Assert.AreEqual(8, result.Item2.Count);

            Assert.AreEqual("Andrew", result.Item2[0].name[0]);
            Assert.AreEqual("Nicky", result.Item2[7].name[0]);
        }

        [TestMethod]
        public async Task getUserCount()
        {
            // Ensure DB is reset correctly
            await UnitTestUtils.resetResample();

            // Check auth
            if (ClinicianController.Instance.AuthToken == null)
            {
                // Login as clinician to grab token with getUsers capability
                await loginAPI.LoginUser("default", "default");
            }
          
            Tuple<HttpStatusCode, int> result = await userAPI.GetUserCount();
            Assert.AreEqual(HttpStatusCode.OK, result.Item1);
            Assert.AreEqual(8, result.Item2);
        }

        [TestMethod]
        public async Task checkTakenUsername()
        {
            Tuple<HttpStatusCode, bool> result = await userAPI.isUniqueUsernameEmail("buzz");
            Assert.AreEqual(HttpStatusCode.OK, result.Item1);
            Assert.AreEqual(false, result.Item2);
        }

        [TestMethod]
        public async Task checkAvailableUsername()
        {
            Tuple<HttpStatusCode, bool> result = await userAPI.isUniqueUsernameEmail("mrbobbydong");
            Assert.AreEqual(HttpStatusCode.OK, result.Item1);
            Assert.AreEqual(true, result.Item2);
        }

        [TestMethod]
        public async Task checkAvailableEmail()
        {
            Tuple<HttpStatusCode, bool> result = await userAPI.isUniqueUsernameEmail("yeet@test.co.nz");
            Assert.AreEqual(HttpStatusCode.OK, result.Item1);
            Assert.AreEqual(true, result.Item2);
        }

        [TestMethod]
        public async Task checkTakenEmail()
        {
            Tuple<HttpStatusCode, bool> result = await userAPI.isUniqueUsernameEmail("andy@andy.com");
            Assert.AreEqual(HttpStatusCode.OK, result.Item1);
            Assert.AreEqual(false, result.Item2);
        }
    }
}
