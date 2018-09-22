using System;
using System.Threading.Tasks;
using mobileAppClient;
using mobileAppClient.odmsAPI;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace mobileAppClientUnitTests
{
    [TestClass]
    public class DrugInteractionTest
    {
        private static DrugInteractionAPI drugAPI;
        private static LoginAPI loginAPI;

        [ClassInitialize]
        public static async Task ClassInitialize(TestContext tc)
        {
			UserController.Instance.isTestMode = true;
			ServerConfig.Instance.serverAddress = "http://csse-s302g3.canterbury.ac.nz:80/testing/api/v1";
            loginAPI = new LoginAPI();
            drugAPI = new DrugInteractionAPI();

            await UnitTestUtils.resetResample();
        }

        [TestMethod]
        public async Task testValidInteraction()
        {
            // Login to provide DrugAPI with gender + age

            await loginAPI.LoginUser("buzz", "drowssap");

            DrugInteractionResult result = await drugAPI.RetrieveDrugInteractions("Ibuprofen", "Asacol");
            Assert.AreEqual(69, result.ageInteractions.Count);
        }
    }
}
