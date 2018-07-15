using System;
using System.Collections.Generic;

using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class WaitingListItemsPage : ContentPage
    {
        public WaitingListItemsPage()
        {
            InitializeComponent();

            //FOR SOME REASON IT DOESNT WORK IF I HAVE THESE IN THE CONSTRUCTORS??

            foreach (WaitingListItem item in UserController.Instance.LoggedInUser.WaitingListItems)
            {
                item.DetailString = "Registered on " + item.OrganRegisteredDate.Day + ", " + item.OrganRegisteredDate.Month + ", " + item.OrganRegisteredDate.Year;
                if(item.OrganDeregisteredDate != null) {
                    item.DetailString += ", deregistered on " + item.OrganRegisteredDate.Day + ", " + item.OrganDeregisteredDate.Month + ", " + item.OrganDeregisteredDate.Year;
                }
            }

            WaitingListItemsList.ItemsSource = UserController.Instance.LoggedInUser.WaitingListItems;
        }

        async void Handle_WaitingListItemTapped(object sender, Xamarin.Forms.ItemTappedEventArgs e)
        {
            if (e == null)
            {
                return; //ItemSelected is called on deselection, which results in SelectedItem being set to null
            }
            var singleWaitingListItemPage = new SingleWaitingListItemPage((WaitingListItem)WaitingListItemsList.SelectedItem);
            await Navigation.PushModalAsync(singleWaitingListItemPage);
        }
    }
}
