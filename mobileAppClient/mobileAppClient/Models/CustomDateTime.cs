﻿using System;
namespace mobileAppClient
{
    public class CustomDateTime
    {
        public CustomDate date { get; set; }
        public CustomTime time { get; set; }

        public CustomDateTime(DateTime dateTimeToParse)
        {
            this.date = new CustomDate(dateTimeToParse);
            this.time = new CustomTime(dateTimeToParse);
        }
    }
}
