using System;
using System.Collections.Generic;
using System.Text;
using Xamarin.Forms.Maps;

namespace mobileAppClient.Models
{
    public class Helicopter
    {
        // Current location is not stored here as it is already within the Pin
        public Position destinationPosition { get; set; }
        public Position startPosition { get; set; }
        public bool isLanding { get; set; }
        public int duration { get; set; }


        /// <summary>
        /// Gets the position of the helicopter after 1 tick
        /// </summary>
        /// <param name="currentPosition"></param>
        /// <returns></returns>
        public Position getNewPosition(Position currentPosition)
        {
            if (this.hasArrived(currentPosition))
            {
                return destinationPosition;
            }

            double newLatitude = currentPosition.Latitude + (destinationPosition.Latitude - startPosition.Latitude) / (5*duration);
            double newLongitude = currentPosition.Longitude + (destinationPosition.Longitude - startPosition.Longitude) / (5*duration);
            
            return new Position(newLatitude, newLongitude);
        }

        public bool hasArrived(Position currentPosition)
        {
            bool latEqual = Math.Abs(currentPosition.Latitude - destinationPosition.Latitude) <= 0.0001;
            bool longEqual = Math.Abs(currentPosition.Longitude - destinationPosition.Longitude) <= 0.0001;

            return latEqual && longEqual;
        }
    }
}
