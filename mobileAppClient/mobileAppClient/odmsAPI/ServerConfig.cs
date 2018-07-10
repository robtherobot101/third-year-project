using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Text;

namespace mobileAppClient.odmsAPI
{
    /*
     * Provides a singleton container that tracks the current server address and holds a single HttpClient to be used across API calls
     */
    sealed class ServerConfig
    {
        public String serverAddress { get; set; }

        public HttpClient client;

        private static readonly Lazy<ServerConfig> lazy =
        new Lazy<ServerConfig>(() => new ServerConfig());

        public static ServerConfig Instance { get { return lazy.Value; } }

        private ServerConfig()
        {
            client = new HttpClient();
        }
    }
}
