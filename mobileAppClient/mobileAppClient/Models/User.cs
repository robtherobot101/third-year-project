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

        //Use their own custom objects as the JSON deserializer cannot deserialize the incoming
        // Objects as C# DateTime objects
        public CustomDate DateOfBirth { get; set; }
        public CustomDate DateOfDeath { get; set; }
        public CustomDateTime CreationTime { get; set; }
        public CustomDateTime LastModified { get; set; }

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

        public List<String> Organs { get; set; }

        public List<Medication> CurrentMedications { get; set; }
        public List<Medication> HistoricMedications { get; set; }

        public List<Disease> CurrentDiseases { get; set; }
        public List<Disease> CuredDiseases { get; set; }

        public List<Procedure> PendingProcedures { get; set; }
        public List<Procedure> PreviousProcedures { get; set; }

        public List<WaitingListItem> WaitingListItems { get; set; }

        //TO BE TESTED
        public List<HistoryItem> HistoryItems { get; set; }

        public User(string email) {
            Email = email;
        }


    }
}
