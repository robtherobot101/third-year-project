using System;
using System.Collections.Generic;
namespace mobileAppClient
{
    public class User
    {
        public List<string> Name { get; set; }
        public List<string> PreferredName { get; set; }

        public Gender Gender { get; set; }
        public Gender GenderIdentity { get; set; }
        public BloodType BloodType { get; set; }
        //Could change to Enums however C# doesnt allow for string value Enums. Thoughts??
        public string SmokerStatus { get; set; }
        public string AlcoholConsumption { get; set; }

        //JSON Deserializer cannot seem to parse the dateTime objects currently
        //public DateTime DateOfBirth { get; set; }
        //public DateTime CreationTime { get; set; }
        //public DateTime LastModified { get; set; }

        public double Height { get; set; }
        public double Weight { get; set; }

        public int Id { get; set; }
        public int ZipCode { get; set; } 

        public string CurrentAddress { get; set; }
        public string Region { get; set; }
        public string City { get; set; }
        public string Country { get; set; }
        public string HomePhone { get; set; }
        public string MobilePhone { get; set; }
        public string BloodPressure { get; set; }

        public string Email { get; set; }
        public string Username { get; set; }
        public string Password { get; set; }

        //public List<Organ> Organs { get; set; }


        public User(string email) {
            Email = email;
        }


    }
}
