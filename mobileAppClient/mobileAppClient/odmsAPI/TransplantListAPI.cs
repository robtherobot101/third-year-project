using mobileAppClient.Models;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace mobileAppClient.odmsAPI
{
    class TransplantListAPI
    {
        public async Task<List<WaitingListItem>> getItems(String query)
        {
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                Console.WriteLine("ERROR: Failed to get transplant items: no internet");
                return new List<WaitingListItem>();
            }

            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;


            String fullURL = url + "/waitingListItems" + query;
            Console.WriteLine("GET: " + fullURL);
            var request = new HttpRequestMessage(new HttpMethod("GET"), url + "/waitingListItems" + query);
            request.Headers.Add("token", ClinicianController.Instance.AuthToken);

            HttpResponseMessage response;
            try
            {
                response = await client.SendAsync(request);
            } catch (HttpRequestException)
            {
                return new List<WaitingListItem>();
            }
            
            string body = await response.Content.ReadAsStringAsync();
          
            try
            {
                IEnumerable<WaitingListItem> items = JsonConvert.DeserializeObject<IEnumerable<WaitingListItem>>(body);
                List<WaitingListItem> itemss = new List<WaitingListItem>();
                foreach(WaitingListItem item in items)
                {
                    itemss.Add(item);
                }
                return itemss;
            }
            catch (JsonSerializationException)
            {
                return new List<WaitingListItem>();
            }
            catch(JsonReaderException) {
                return new List<WaitingListItem>();

            }
        }

        public async Task<HttpStatusCode> updateItem(WaitingListItem item)
        {
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                Console.WriteLine("ERROR: Failed to update transplant item: no internet");
                return HttpStatusCode.ServiceUnavailable;
            }

            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            String itemRequestBody = JsonConvert.SerializeObject(item);
            
            HttpContent body = new StringContent(itemRequestBody);

            System.Diagnostics.Debugger.Log(0, "", itemRequestBody);

            var request = new HttpRequestMessage(new HttpMethod("PATCH"), url + "/users/" + item.userId + "/waitingListItems/" + item.id);
            request.Content = body;
            request.Headers.Add("token", ClinicianController.Instance.AuthToken);

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

        public async Task<List<WaitingListItem>> getAllUserItems(int userId)
        {
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                Console.WriteLine("ERROR: Failed to get all user transplant items: no internet");
                return new List<WaitingListItem>();
            }

            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;


            var request = new HttpRequestMessage(new HttpMethod("GET"), url + "/users/" + userId + "/waitingListItems");
            request.Headers.Add("token", ClinicianController.Instance.AuthToken);

            HttpResponseMessage response;
            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException)
            {
                return new List<WaitingListItem>();
            }
            string body = await response.Content.ReadAsStringAsync();

            try
            {
                IEnumerable<WaitingListItem> items = JsonConvert.DeserializeObject<IEnumerable<WaitingListItem>>(body);
                List<WaitingListItem> itemss = new List<WaitingListItem>();
                foreach (WaitingListItem item in items)
                {
                    itemss.Add(item);
                }
                return itemss;
            }
            catch (JsonSerializationException)
            {
                return new List<WaitingListItem>();
            }
            catch (JsonReaderException)
            {
                return new List<WaitingListItem>();

            }
        }

        public async Task<HttpStatusCode> patchAllUserItems(List<WaitingListItem> items, int userId)
        {
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                Console.WriteLine("ERROR: Failed to patch all user transplant items: no internet");
                return HttpStatusCode.ServiceUnavailable;
            }

            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            String itemRequestBody = JsonConvert.SerializeObject(items);

            HttpContent body = new StringContent(itemRequestBody);


            var request = new HttpRequestMessage(new HttpMethod("PATCH"), url + "/users/" + userId + "/waitingListItems");
            request.Content = body;
            request.Headers.Add("token", ClinicianController.Instance.AuthToken);

            HttpResponseMessage response;
            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException)
            {
                return HttpStatusCode.ServiceUnavailable;
            }

            Console.Write("Patch all user items request body: " + itemRequestBody);
            Console.Write("Patch all user items response: " + response);

            return response.StatusCode;
        }

        public async Task<List<OrganTransfer>> GetAllTransfers()
        {
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                Console.WriteLine("ERROR: Failed to get all transfers: no internet");
                return new List<OrganTransfer>();
            }

            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;


            var request = new HttpRequestMessage(new HttpMethod("GET"), url + "/transfer");
            request.Headers.Add("token", ClinicianController.Instance.AuthToken);

            HttpResponseMessage response;
            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException)
            {
                return new List<OrganTransfer>();
            }
            string body = await response.Content.ReadAsStringAsync();

            try
            {
                //return JsonConvert.DeserializeObject<List<OrganTransfer>>(body);
                IEnumerable<OrganTransfer> items = JsonConvert.DeserializeObject<IEnumerable<OrganTransfer>>(body);
                List<OrganTransfer> itemss = new List<OrganTransfer>();
                foreach (OrganTransfer item in items)
                {
                    itemss.Add(item);
                }
                return itemss;
            }
            catch (JsonSerializationException)
            {
                return new List<OrganTransfer>();
            }
            catch (JsonReaderException e)
            {
                Console.WriteLine(e.Message);
                return new List<OrganTransfer>();

            }
        }

        public async Task DeleteTransfer(int id)
        {
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                Console.WriteLine("ERROR: Failed to delete transfer: no internet");
                return;
            }

            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            var request = new HttpRequestMessage(new HttpMethod("Delete"), url + "/transfer/" + id);
            request.Headers.Add("token", ClinicianController.Instance.AuthToken);

            HttpResponseMessage response;
            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException)
            {
                return;
            }
        }

        public async Task DeleteWaitingListItem(int organId)
        {
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                Console.WriteLine("ERROR: Failed to delete waiting list item: no internet");
                return;
            }

            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;


            var request = new HttpRequestMessage(new HttpMethod("PATCH"), url + "/waitingListItems/" + organId);
            request.Headers.Add("token", ClinicianController.Instance.AuthToken);

            HttpResponseMessage response;
            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException)
            {
                return;
            }
        }

        public async Task<bool> SetInTransfer(int organId, int transferNum)
        {
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                Console.WriteLine("ERROR: Failed to set in transfer: no internet");
                return false;
            }

            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;


            var request = new HttpRequestMessage(new HttpMethod("PATCH"), url + "/organs/" + organId + "/" + transferNum);
            request.Headers.Add("token", ClinicianController.Instance.AuthToken);

            HttpResponseMessage response;
            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException)
            {
                return false;
            }

            return response.StatusCode == HttpStatusCode.Created;
        }

        public async Task<HttpStatusCode> InsertTransfer(OrganTransfer transfer)
        {
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                return HttpStatusCode.ServiceUnavailable;
            }

            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;


            //Create the request with token as a header
            String uploadTransfer = JsonConvert.SerializeObject(transfer);
            HttpContent body = new StringContent(uploadTransfer);

            var request = new HttpRequestMessage(new HttpMethod("POST"), url + "/transfer");
            request.Content = body;
            request.Headers.Add("token", UserController.Instance.AuthToken);

            try
            {
                var response = await client.SendAsync(request);

                if (response.StatusCode == HttpStatusCode.Created)
                {
                    Console.WriteLine("Success on uploading transfer");
                    return HttpStatusCode.OK;
                }
                else
                {
                    Console.WriteLine(String.Format("Failed to upload transfer ({0})", response.StatusCode));
                    return response.StatusCode;
                }
            }
            catch (HttpRequestException)
            {
                return HttpStatusCode.ServiceUnavailable;
            }
        }

        public async Task<int> GetWaitingListId(int userId, Organ organ)
        {
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                Console.WriteLine("ERROR: Failed to get waiting list id: no internet");
                return 0;
            }

            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;


            var request = new HttpRequestMessage(new HttpMethod("GET"), url + "/" + userId + "/waitingListOrgan/" + organ);
            request.Headers.Add("token", ClinicianController.Instance.AuthToken);

            HttpResponseMessage response;
            try {
                response = await client.SendAsync(request);
            } catch (HttpRequestException) {
                Console.WriteLine("TransplantAPI: FAILED TO GET WAITING LIST ID !!!");
                return 0;
            }

            string body = await response.Content.ReadAsStringAsync();

            try
            {
                //return JsonConvert.DeserializeObject<List<OrganTransfer>>(body);
                int id = JsonConvert.DeserializeObject<int>(body);
                return id;
            }
            catch (JsonSerializationException jse)
            {
                Console.WriteLine(jse.Message);
                return 0;
            }
            catch (JsonReaderException e)
            {
                Console.WriteLine(e.Message);
                return 0;

            }
        }

        public async Task<List<DonatableOrgan>> GetSingleUsersDonatableOrgans(int userId)
        {
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                Console.WriteLine("ERROR: Failed to get single users donatable organs: no internet");
                return new List<DonatableOrgan>();
            }

            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;


            var request = new HttpRequestMessage(new HttpMethod("GET"), url + "/organs/" + userId);
            request.Headers.Add("token", ClinicianController.Instance.AuthToken);

            var response = await client.SendAsync(request);
            string body = await response.Content.ReadAsStringAsync();

            try
            {
                //return JsonConvert.DeserializeObject<List<OrganTransfer>>(body);
                 return JsonConvert.DeserializeObject<List<DonatableOrgan>>(body);
            }
            catch (JsonSerializationException jse)
            {
                Console.WriteLine(jse.Message);
                return new List<DonatableOrgan>();
            }
            catch (JsonReaderException e)
            {
                Console.WriteLine(e.Message);
                return new List<DonatableOrgan>();

            }
        }
    }
}
