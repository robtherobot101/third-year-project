using System;
using System.Collections.Generic;
namespace mobileAppClient
{
    public class User
    {
        public List<string> name { get; set; }
        public List<string> preferredName { get; set; }

        public Gender gender { get; set; }
        public Gender genderIdentity { get; set; }
        public BloodType bloodType { get; set; }
        //Could change to Enums however C# doesnt allow for string value Enums. Thoughts??
        public string smokerStatus { get; set; }
        public string alcoholConsumption { get; set; }

        //Use their own custom objects as the JSON deserializer cannot deserialize the incoming
        // Objects as C# DateTime objects
        public CustomDate dateOfBirth { get; set; }
        public CustomDate dateOfDeath { get; set; }
        public CustomDateTime creationTime { get; set; }
        public CustomDateTime lastModified { get; set; }

        public double height { get; set; }
        public double weight { get; set; }

        public int id { get; set; }
        //public int ZipCode { get; set; } 

        public string currentAddress { get; set; }
        public string region { get; set; }
        //public string City { get; set; }
        //public string Country { get; set; }
        //public string HomePhone { get; set; }
        //public string MobilePhone { get; set; }
        public string bloodPressure { get; set; }

        public string email { get; set; }
        public string username { get; set; }
        public string password { get; set; }

        public List<String> organs { get; set; }

        public List<Medication> currentMedications { get; set; }
        public List<Medication> historicMedications { get; set; }

        public List<Disease> currentDiseases { get; set; }
        public List<Disease> curedDiseases { get; set; }

        public List<Procedure> pendingProcedures { get; set; }
        public List<Procedure> previousProcedures { get; set; }

        public List<WaitingListItem> waitingListItems { get; set; }

        //TO BE TESTED
        public List<HistoryItem> historyItems { get; set; }

        public User(string email) {
            this.email = email;
        }

        public double getAge()
        {
            double age;
            if (this.dateOfDeath == null)
            {
                age = (DateTime.Now - this.dateOfBirth.ToDateTime()).Days / 365.00;
            }
            else
            {
                age = (this.dateOfDeath.ToDateTime() - this.dateOfBirth.ToDateTime()).Days / 365.00;
            }
            return age;

        }


    }
}
