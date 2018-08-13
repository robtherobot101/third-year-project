﻿using System;
using Newtonsoft.Json;

namespace mobileAppClient
{
    public class FacebookProfile
    {

        public string Name { get; set; }
        public Picture Picture { get; set; }
        public string Link { get; set; }
        public Cover Cover { get; set; }
        [JsonProperty("age_range")]
        public AgeRange AgeRange { get; set; }
        [JsonProperty("first_name")]
        public string FirstName { get; set; }
        [JsonProperty("last_name")]
        public string LastName { get; set; }
        public string Gender { get; set; }
        public bool IsVerified { get; set; }
        public string Id { get; set; }
        public string Email { get; set; }
        public string Birthday { get; set; }
        public Location Location { get; set; }
    }

    public class Location
    {
        public string Id { get; set; }
        public string Name { get; set; }
    }
    

    public class Picture
    {
        public Data Data { get; set; }
    }

    public class Data
    {
        public bool IsSilhouette { get; set; }
        public string Url { get; set; }
    }

    public class Cover
    {
        public string Id { get; set; }
        public int OffsetY { get; set; }
        public string Source { get; set; }
    }

    public class AgeRange
    {
        public int Min { get; set; }
    }


}