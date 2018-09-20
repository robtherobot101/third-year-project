using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using mobileAppClient.Models;
using Newtonsoft.Json;

namespace mobileAppClient.odmsAPI
{
    class MessagingAPI
    {
        public async Task<Tuple<HttpStatusCode, List<Conversation>>> GetConversation(int localUserId)
        {
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            var request = new HttpRequestMessage(new HttpMethod("GET"), url + localUserId + "/conversations");

            request.Headers.Add("token",
                ClinicianController.Instance.isLoggedIn()
                    ? ClinicianController.Instance.AuthToken
                    : UserController.Instance.AuthToken);


            var response = await client.SendAsync(request);
            string body = await response.Content.ReadAsStringAsync();

            try
            {
                return new Tuple<HttpStatusCode, List<Conversation>>
                    (response.StatusCode, JsonConvert.DeserializeObject<List<Conversation>>(body));
            }
            catch (JsonSerializationException jse)
            {
                return null;
            }
        }

        public async Task<HttpStatusCode> SendMessage(int localUserId, int conversationId, string messageContents) 
        {
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            var request = new HttpRequestMessage(new HttpMethod("POST"), url + localUserId + "/conversations/" + conversationId);
            request.Content = new StringContent(messageContents);

            request.Headers.Add("token",
                ClinicianController.Instance.isLoggedIn()
                    ? ClinicianController.Instance.AuthToken
                    : UserController.Instance.AuthToken);

            return (await client.SendAsync(request)).StatusCode;
        }

        public async Task<HttpStatusCode> CreateConversation(int localUserId, List<int> participants)
        {
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            var request = new HttpRequestMessage(new HttpMethod("POST"), url + localUserId + "/conversations");
            request.Content = new StringContent(JsonConvert.SerializeObject(participants));

            request.Headers.Add("token",
                ClinicianController.Instance.isLoggedIn()
                    ? ClinicianController.Instance.AuthToken
                    : UserController.Instance.AuthToken);

            return (await client.SendAsync(request)).StatusCode;
        }
    }
}
