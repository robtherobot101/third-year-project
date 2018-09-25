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
        public async Task<Tuple<HttpStatusCode, List<Conversation>>> GetConversations(int localUserId, bool isClinicianFetching)
        {
            // Check connection
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                return new Tuple<HttpStatusCode, List<Conversation>>(HttpStatusCode.ServiceUnavailable, new List<Conversation>());
            }

            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            if (isClinicianFetching)
            {
                url = String.Format("{0}/{1}/{2}/conversations", url, "clinicians", localUserId);
            }
            else
            {
                url = String.Format("{0}/{1}/{2}/conversations", url, "users", localUserId);
            }

            var request = new HttpRequestMessage(new HttpMethod("GET"), url);

            request.Headers.Add("token",
                ClinicianController.Instance.isLoggedIn()
                    ? ClinicianController.Instance.AuthToken
                    : UserController.Instance.AuthToken);

            HttpResponseMessage response;
            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException)
            {
                return new Tuple<HttpStatusCode, List<Conversation>>(HttpStatusCode.ServiceUnavailable, new List<Conversation>());
            }
            string body = await response.Content.ReadAsStringAsync();

            try
            {
                return new Tuple<HttpStatusCode, List<Conversation>>
                    (response.StatusCode, JsonConvert.DeserializeObject<List<Conversation>>(body));
            }
            catch (JsonSerializationException)
            {
                return null;
            }
        }

        public async Task<Tuple<HttpStatusCode, Conversation>> GetConversation(int localUserId, int conversationId, bool isClinicianFetching)
        {
            // Check connection
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                return new Tuple<HttpStatusCode, Conversation>(HttpStatusCode.ServiceUnavailable, null);
            }

            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            if (isClinicianFetching)
            {
                url = String.Format("{0}/{1}/{2}/conversations/{3}", url, "clinicians", localUserId, conversationId);
            }
            else
            {
                url = String.Format("{0}/{1}/{2}/conversations/{3}", url, "users", localUserId, conversationId);
            }

            var request = new HttpRequestMessage(new HttpMethod("GET"), url);

            request.Headers.Add("token",
                ClinicianController.Instance.isLoggedIn()
                    ? ClinicianController.Instance.AuthToken
                    : UserController.Instance.AuthToken);


            HttpResponseMessage response;
            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException)
            {
                return new Tuple<HttpStatusCode, Conversation>(HttpStatusCode.ServiceUnavailable, null);
            }
            string body = await response.Content.ReadAsStringAsync();

            try
            {
                return new Tuple<HttpStatusCode, Conversation>
                    (response.StatusCode, JsonConvert.DeserializeObject<Conversation>(body));
            }
            catch (JsonSerializationException)
            {
                return null;
            }
        }

        public async Task<HttpStatusCode> SendMessage(int localUserId, int conversationId, string messageContents, bool isClinicianFetching) 
        {
            // Check connection
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                return HttpStatusCode.ServiceUnavailable;
            }

            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            if (isClinicianFetching)
            {
                url = String.Format("{0}/{1}/{2}/conversations/{3}", url, "clinicians", localUserId, conversationId);
            }
            else
            {
                url = String.Format("{0}/{1}/{2}/conversations/{3}", url, "users", localUserId, conversationId);
            }

            var request = new HttpRequestMessage(new HttpMethod("POST"), url);
            request.Content = new StringContent(messageContents);

            request.Headers.Add("token",
                ClinicianController.Instance.isLoggedIn()
                    ? ClinicianController.Instance.AuthToken
                    : UserController.Instance.AuthToken);

            HttpResponseMessage response;
            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException)
            {
                return HttpStatusCode.ServiceUnavailable;
            }
            return response.StatusCode;
        }

        public async Task<Tuple<HttpStatusCode, int>> CreateConversation(int localUserId, List<int> participants)
        {
            int conversationId = -1;
            // Check connection
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                return new Tuple<HttpStatusCode, int>(HttpStatusCode.ServiceUnavailable, conversationId);
            }

            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            ConversationParticipants conversationParticipants = new ConversationParticipants
            {
                participants = participants
            };

            var request = new HttpRequestMessage(new HttpMethod("POST"), url + "/clinicians/" + localUserId + "/conversations");
            request.Content = new StringContent(JsonConvert.SerializeObject(conversationParticipants));

            request.Headers.Add("token",
                ClinicianController.Instance.isLoggedIn()
                    ? ClinicianController.Instance.AuthToken
                    : UserController.Instance.AuthToken);

            
            HttpResponseMessage response;
            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException)
            {
                return new Tuple<HttpStatusCode, int>(HttpStatusCode.ServiceUnavailable, conversationId);
            }

            if (response.StatusCode == HttpStatusCode.Created)
            {
                conversationId = Int32.Parse(await response.Content.ReadAsStringAsync());
            }

            return new Tuple<HttpStatusCode, int>(response.StatusCode, conversationId);
        }
    }
}
