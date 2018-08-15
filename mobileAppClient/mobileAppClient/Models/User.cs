using System;
using System.Collections.Generic;
namespace mobileAppClient
{
    /*
     * Stores details regarding a user for JSON (de)serialisation and storage
     */
    public class User
    {
        public List<string> name { get; set; }
        public List<string> preferredName { get; set; }

        public string gender { get; set; }
        public string genderIdentity { get; set; }
        public string bloodType { get; set; }
        //Could change to Enums however C# doesnt allow for string value Enums. Thoughts??
        public string smokerStatus { get; set; }
        public string alcoholConsumption { get; set; }

        public CustomDate dateOfBirth { get; set; }
        public CustomDateTime dateOfDeath { get; set; }
        public CustomDateTime creationTime { get; set; }
        public CustomDateTime lastModified { get; set; }

        public double height { get; set; }
        public double weight { get; set; }

        public int id { get; set; }
        //public int ZipCode { get; set; } 

        public string currentAddress { get; set; }
        public string region { get; set; }
        //public string city { get; set; }
        //public string country { get; set; }
        public string cityOfDeath { get; set; }
        //public string countryOfDeath { get; set; }
        public string regionOfDeath { get; set; }
        //public string homePhone { get; set; }
        //public string mobilePhone { get; set; }
        public string bloodPressure { get; set; }

        public string email { get; set; }
        public string username { get; set; }
        public string password { get; set; }
        public int points { get; set; }

        public List<String> organs { get; set; }
        public List<Medication> currentMedications { get; set; }
        public List<Medication> historicMedications { get; set; }

        public List<Disease> currentDiseases { get; set; }
        public List<Disease> curedDiseases { get; set; }

        public List<Procedure> pendingProcedures { get; set; }
        public List<Procedure> previousProcedures { get; set; }

        public List<WaitingListItem> waitingListItems { get; set; }

        public List<HistoryItem> userHistory { get; set; }

        public string FullName
        {
            get
            {
                return String.Join(" ", name);
            }
        }

        public User(string email) {
            this.email = email;
        }

        public User()
        {
        }

        public User ShallowCopy()
        {
            return (User)this.MemberwiseClone();
        }

        /*
         * Simply calculates the user's age
         */
        public int Age
        {
            get {
                DateTime today = DateTime.Today;
                DateTime dob = this.dateOfBirth.ToDateTime();
                int age = today.Year - dob.Year;

                if (dob > today.AddYears(-age))
                    age--;

                return age;
            }
        }
    }
}
