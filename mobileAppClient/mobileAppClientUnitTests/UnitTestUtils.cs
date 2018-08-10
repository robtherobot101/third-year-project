using mobileAppClient;
using mobileAppClient.odmsAPI;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace mobileAppClientUnitTests
{
    public static class UnitTestUtils
    {
        public static async Task resetResample()
        {
            HttpClient client = ServerConfig.Instance.client;
            String url = ServerConfig.Instance.serverAddress;

            var resetRequest = new HttpRequestMessage(new HttpMethod("POST"), url + "/reset");
            resetRequest.Headers.Add("token", "masterToken");

            var resampleRequest = new HttpRequestMessage(new HttpMethod("POST"), url + "/resample");
            resampleRequest.Headers.Add("token", "masterToken");

            var resetResult = await client.SendAsync(resetRequest);
            var resampleResult = await client.SendAsync(resampleRequest);

            if (resetResult.StatusCode != HttpStatusCode.OK || resampleResult.StatusCode != HttpStatusCode.OK)
            {
                throw new Exception("Failed to reset/resample");
            }

            UserController.Instance.Logout();
            ClinicianController.Instance.Logout();
        }
    }
}
