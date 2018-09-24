using System;
using System.Collections.Generic;
using System.Text;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace mobileAppClient.Models
{
    [JsonConverter(typeof(StringEnumConverter))]
    public enum Organ
    {
        LIVER, KIDNEY, PANCREAS, HEART, LUNG, INTESTINE, CORNEA, EAR, SKIN, BONE, TISSUE
    }

    public static class OrganExtensions
    {
        public static Organ ToOrgan(string organString)
        {
            switch (organString.ToLower())
            {
                case "kidney": return Organ.KIDNEY;
                case "liver": return Organ.LIVER;
                case "pancreas": return Organ.PANCREAS;
                case "heart": return Organ.HEART;
                case "lung": return Organ.LUNG;
                case "intestine": return Organ.INTESTINE;
                case "cornea": return Organ.CORNEA;
                case "ear": return Organ.EAR;
                case "middle ear": return Organ.EAR;
                case "middle-ear": return Organ.EAR;
                case "skin": return Organ.SKIN;
                case "bone": return Organ.BONE;
                case "bone marrow": return Organ.BONE;
                case "bone-marrow": return Organ.BONE;
                case "tissue": return Organ.TISSUE;
                case "connective tissue": return Organ.TISSUE;
                case "connective-tissue": return Organ.TISSUE;
                default: throw new ArgumentException("Invalid organ");
            }
        }

        public static string ToString(Organ organ)
        {
            switch (organ)
            {
                case Organ.KIDNEY: return "Kidney";
                case Organ.BONE: return "Bone Marrow";
                case Organ.CORNEA: return "Cornea";
                case Organ.EAR: return "Middle Ear";
                case Organ.HEART: return "Heart";
                case Organ.PANCREAS: return "Pancreas";
                case Organ.INTESTINE: return "Intestine";
                case Organ.SKIN: return "Skin";
                case Organ.TISSUE: return "Connective Tissue";
                case Organ.LUNG: return "Lung";
                case Organ.LIVER: return "Liver";
                default: throw new ArgumentException("Invalid organ");
            }
        }
    }
}
