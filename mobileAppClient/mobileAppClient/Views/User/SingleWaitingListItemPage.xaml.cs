using System;
using System.Collections.Generic;

using Xamarin.Forms;
using System.Globalization;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the single waiting list item page for 
     * showing the details of a single waiting list item of a user.
     */ 
    public partial class SingleWaitingListItemPage : ContentPage
    {
        DateTimeFormatInfo dateTimeFormat = new DateTimeFormatInfo();
        /*
         * Constructor which initialises the entries of the waiting list items listview.
         */ 
        public SingleWaitingListItemPage(WaitingListItem waitingListItem)
        {
            InitializeComponent();
            OrganTypeEntry.Text = waitingListItem.OrganType;
            RegisteredDateEntry.Text = waitingListItem.OrganRegisteredDate.day + " of " + dateTimeFormat.GetAbbreviatedMonthName(waitingListItem.OrganRegisteredDate.month) + ", " + waitingListItem.OrganRegisteredDate.year;
            DeregisteredDateEntry.Text =
                waitingListItem.OrganDeregisteredDate != null ? 
                                     waitingListItem.OrganDeregisteredDate.day + " of " + dateTimeFormat.GetAbbreviatedMonthName(waitingListItem.OrganDeregisteredDate.month) + ", " + waitingListItem.OrganDeregisteredDate.year
                                     : "N/A";
            //DeregisterCodeEntry.Text =
                //waitingListItem.OrganDeregisteredCode != 0 ? waitingListItem.OrganDeregisteredCode.ToString() : "N/A";
            

            IDEntry.Text = waitingListItem.Id.ToString();
        }

    }
}
