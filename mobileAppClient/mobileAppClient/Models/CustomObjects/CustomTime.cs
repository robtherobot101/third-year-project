using System;
namespace mobileAppClient
{
    /*
     * Class to handle any time objects received from the server user object.
     */ 
    public class CustomTime
    {
        public int hour { get; set; }
        public int minute { get; set; }
        public int second { get; set; }
        public long nano { get; set; }

        public CustomTime(DateTime timeToParse)
        {
            this.hour = Convert.ToInt32(timeToParse.ToString("HH"));
            this.minute = Convert.ToInt32(timeToParse.ToString("mm"));
            this.second = Convert.ToInt32(timeToParse.ToString("ss"));
            this.nano = 0;
        }
    }
}
