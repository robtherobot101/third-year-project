using System;
namespace mobileAppClient
{
    public class CustomDate
    {
        public int year { get; set; }
        public int month { get; set; }
        public int day { get; set; }

        public CustomDate(DateTime dateToParse)
        {
            this.day = Convert.ToInt32(dateToParse.ToString("dd"));
            this.month = Convert.ToInt32(dateToParse.ToString("MM"));
            this.year = Convert.ToInt32(dateToParse.ToString("yyyy"));
        }
    }
}
