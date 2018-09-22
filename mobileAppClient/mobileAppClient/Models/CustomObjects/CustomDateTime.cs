using System;
using System.Globalization;

namespace mobileAppClient
{
    /*
     * Class to handle any datetime objects received from the server user object.
     */ 
    public class CustomDateTime
    {
        public CustomDate date { get; set; }
        public CustomTime time { get; set; }

        public CustomDateTime(DateTime dateTimeToParse)
        {
            this.date = new CustomDate(dateTimeToParse);
            this.time = new CustomTime(dateTimeToParse);
        }

        /*
         * Method to convert a CustomDate to a C# DateTime variable with the
         * given day, month and year.
        */
        public DateTime ToDateTime()
        {
            String rawDate = String.Format("{0}/{1}/{2}", this.date.day, this.date.month, this.date.year);
            DateTime dt = DateTime.ParseExact(rawDate, "d/M/yyyy", CultureInfo.InvariantCulture);

            return dt;
        }

        /*
         * Method to convert a CustomDate to a C# DateTime variable with the
         * given day, month and year, hours, minutes and seconds.
        */
        public DateTime ToDateTimeWithSeconds() {
            String rawDate = String.Format("{0}/{1}/{2}/{3}/{4}/{5}", this.date.day, this.date.month, this.date.year,this.time.hour, this.time.minute, this.time.second);
            DateTime dt = DateTime.ParseExact(rawDate, "d/M/yyyy/H/m/s", CultureInfo.InvariantCulture);

            return dt;
        }

    }
}
