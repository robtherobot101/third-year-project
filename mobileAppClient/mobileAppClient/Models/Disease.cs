using System;
namespace mobileAppClient
{
    public class Disease
    {
        public string Name { get; set; }
        public CustomDate DiagnosisDate { get; set; }
        public bool IsChronic { get; set; }
        public bool IsCured { get; set; }
        public int Id { get; set; }
        public string DiagnosisDateString { get; set; }

        public Disease(string name, int id)
        {
            this.Name = name;
            this.Id = id;
        }
    }
}
