﻿using System;
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

            double newLatitude = currentPosition.Latitude + (destinationPosition.Latitude - startPosition.Latitude) / 200;
            double newLongitude = currentPosition.Longitude + (destinationPosition.Longitude - startPosition.Longitude) / 200;
            
            return new Position(newLatitude, newLongitude);
        }

        /// <summary>
        /// Gets the position of where the organ pin should be nested/attached to the helicopter
        /// </summary>
        /// <param name="currentPosition"></param>
        /// <returns></returns>
        public Position getOrganPosition(Position currentPosition)
        {
            return new Position(currentPosition.Latitude + 0.01, currentPosition.Longitude + 0.01);
        }

        public bool hasArrived(Position currentPosition)
        {
            bool latEqual = Math.Abs(currentPosition.Latitude - destinationPosition.Latitude) <= 0.0001;
            bool longEqual = Math.Abs(currentPosition.Longitude - destinationPosition.Longitude) <= 0.0001;

            return latEqual && longEqual;
        }
    }
}
