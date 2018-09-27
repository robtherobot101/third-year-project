using System;
namespace mobileAppClient
{
    /// <summary>
    /// Holds information about a photo
    /// </summary>
    public class Photo
    {
        public string data { get; set; }

        /// <summary>
        /// Constructs the photo
        /// </summary>
        public Photo(string data)
        {
            this.data = data;
        }
    }
}
