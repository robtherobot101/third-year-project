using System;
using System.Collections.Generic;
using System.Text;
using Newtonsoft.Json;
using Xamarin.Forms.Maps;

namespace mobileAppClient.Models
{
    /// <summary>
    /// Stores values we can carry around through the URL of a pin, hacky: maybe, works well: maybe?
    /// </summary>
    public class MagicPinURL
    {
        public int userID { get; set; }
        public Position pinPosition { get; set; }
        public List<string> organImageList { get; set; }

        public MagicPinURL()
        {
        }

        public static string Pack(MagicPinURL magicPinUrlToPack)
        {
            return JsonConvert.SerializeObject(magicPinUrlToPack);
        }

        public static MagicPinURL Unpack(String magicPinUrlToUnpack)
        {
            return JsonConvert.DeserializeObject<MagicPinURL>(magicPinUrlToUnpack);
        }
    }
}
