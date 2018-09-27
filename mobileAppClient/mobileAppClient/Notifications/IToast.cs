using System;
using System.Collections.Generic;
using System.Text;

namespace mobileAppClient.Notifications
{
    /*
     * Defines the interface for a toast
     */
    public interface IToast
    {
            void LongAlert(string message);
            void ShortAlert(string message);
    }
}
