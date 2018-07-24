using System;
using System.Collections.Generic;
using System.Text;

namespace mobileAppClient
{
    /*
     * Interface used in the Observer Pattern to update anything the user observes.
     */ 
    interface UserObserver
    {
        void updateUser();
    }
}
