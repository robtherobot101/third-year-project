using mobileAppClient;
using mobileAppClient.odmsAPI;
using System;
using System.Collections.Generic;
using System.Linq;
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

            await client.SendAsync(resetRequest);
            await client.SendAsync(resampleRequest);

            UserController.Instance.Logout();
            ClinicianController.Instance.Logout();
        }
    }
}
