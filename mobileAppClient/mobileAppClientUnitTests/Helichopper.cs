using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using mobileAppClient;
using mobileAppClient.Models;
using mobileAppClient.odmsAPI;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Xamarin.Forms.Maps;

namespace mobileAppClientUnitTests
{
    [TestClass]
    public class Helichopper
    {
        [TestMethod]
        public void SingleHeliMovement()
        {
            Position currentHelicopterPosition = new Position(-37.9061137, 176.2050742);

            Helicopter heli = new Helicopter
            {
                destinationPosition = new Position(-36.8613687, 174.7676895),
                startPosition = new Position(-37.9061137, 176.2050742),
                isLanding = false
            };
            Assert.AreEqual(new Position(-37.900889975, 176.1978872765), heli.getNewPosition(currentHelicopterPosition));
        }

        [TestMethod]
        public void HeliToDestination()
        {
            Position currentHelicopterPosition = new Position(-37.9061137, 176.2050742);

            Helicopter heli = new Helicopter
            {
                destinationPosition = new Position(-36.8613687, 174.7676895),
                startPosition = new Position(-37.9061137, 176.2050742),
                isLanding = false
            };

            for (int i = 0; i < 200; i++)
            {
                currentHelicopterPosition = heli.getNewPosition(currentHelicopterPosition);
            }

            Assert.IsTrue(heli.hasArrived(currentHelicopterPosition));
        }

        [TestMethod]
        public void HeliOvershootDestination()
        {
            Position currentHelicopterPosition = new Position(-37.9061137, 176.2050742);

            Helicopter heli = new Helicopter
            {
                destinationPosition = new Position(-36.8613687, 174.7676895),
                startPosition = new Position(-37.9061137, 176.2050742),
                isLanding = false
            };

            for (int i = 0; i < 250; i++)
            {
                currentHelicopterPosition = heli.getNewPosition(currentHelicopterPosition);
            }

            Assert.IsTrue(heli.hasArrived(currentHelicopterPosition));
        }
    }
}
