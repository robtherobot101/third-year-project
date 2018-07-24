using System;
using System.Collections.Generic;
using System.Linq;

using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class WaitingListItemsPage : ContentPage
    {
        public WaitingListItemsPage()
        {
            InitializeComponent();

            //FOR SOME REASON IT DOESNT WORK IF I HAVE THESE IN THE CONSTRUCTORS??

            foreach (WaitingListItem item in UserController.Instance.LoggedInUser.waitingListItems)
            {
                item.DetailString = "Registered on " + item.OrganRegisteredDate.day + ", " + item.OrganRegisteredDate.month + ", " + item.OrganRegisteredDate.year;
                if(item.OrganDeregisteredDate != null) {
                    item.DetailString = "Deregistered on " + item.OrganRegisteredDate.day + ", " + item.OrganDeregisteredDate.month + ", " + item.OrganDeregisteredDate.year;
                } 
            }

            if (UserController.Instance.LoggedInUser.waitingListItems.Count == 0)
            {
                NoDataLabel.IsVisible = true;
                WaitingListItemsList.IsVisible = false;
                SortingInput.IsVisible = false;
            }

            WaitingListItemsList.ItemsSource = UserController.Instance.LoggedInUser.waitingListItems;
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

        void Handle_SelectedIndexChanged(object sender, System.EventArgs e)
        {
            List<WaitingListItem> currentList = (System.Collections.Generic.List<WaitingListItem>)WaitingListItemsList.ItemsSource;
            switch (SortingInput.SelectedItem)
            {
                case "Organ":
                    
                    List<WaitingListItem> SortedList = currentList.OrderBy(o => o.OrganType).ToList();
                    WaitingListItemsList.ItemsSource = SortedList;

                    AscendingDescendingPicker.IsVisible = true;
                    break;
                case "Reg. Date":
                    SortedList = currentList.OrderBy(o => o.OrganRegisteredDate.ToDateTime()).ToList();
                    WaitingListItemsList.ItemsSource = SortedList;

                    AscendingDescendingPicker.IsVisible = true;
                    break;
                case "Dereg. Date":

                    List<WaitingListItem> finalList = new List<WaitingListItem>();
                    List<WaitingListItem> deregisteredList = new List<WaitingListItem>();
                    List<WaitingListItem> nonDeregisteredList = new List<WaitingListItem>();

                    foreach (WaitingListItem item in currentList)
                    {
                        if (item.OrganDeregisteredDate != null)
                        {
                            deregisteredList.Add(item);
                        }
                        else
                        {
                            nonDeregisteredList.Add(item);
                        }
                    }

                    List<WaitingListItem> sortedDeregisteredList = deregisteredList.OrderBy(o => o.OrganDeregisteredDate.ToDateTime()).ToList();

                    foreach (WaitingListItem item in sortedDeregisteredList)
                    {
                        finalList.Add(item);
                    }
                    foreach (WaitingListItem item in nonDeregisteredList)
                    {
                        finalList.Add(item);
                    }

                    WaitingListItemsList.ItemsSource = finalList;

                    AscendingDescendingPicker.IsVisible = true;
                    break;
                case "Dereg. Code":
                    SortedList = currentList.OrderBy(o => o.OrganDeregisteredCode).ToList();
                    WaitingListItemsList.ItemsSource = SortedList;

                    AscendingDescendingPicker.IsVisible = true;
                    break;
                case "Clear":

                    WaitingListItemsList.ItemsSource = currentList;                        
                    SortingInput.SelectedIndex = -1;

                    AscendingDescendingPicker.IsVisible = false;
                    break;
            }
        }

        void Handle_UpDownChanged(object sender, System.EventArgs e)
        {
            List<WaitingListItem> currentList = (System.Collections.Generic.List<WaitingListItem>)WaitingListItemsList.ItemsSource;
            switch (SortingInput.SelectedItem)
            {
                case "Organ":
                    switch (AscendingDescendingPicker.SelectedItem)
                    {
                        case "⬆ (Descending)":
                            List<WaitingListItem> SortedList = currentList.OrderByDescending(o => o.OrganType).ToList();
                            WaitingListItemsList.ItemsSource = SortedList;
                            break;
                        case "⬇ (Ascending)":
                            SortedList = currentList.OrderBy(o => o.OrganType).ToList();
                            WaitingListItemsList.ItemsSource = SortedList;
                            break;
                        case "Clear":
                            WaitingListItemsList.ItemsSource = currentList;
                            AscendingDescendingPicker.SelectedIndex = -1;
                            break;
                    }
                    break;
                case "Reg. Date":
                    switch (AscendingDescendingPicker.SelectedItem)
                    {
                        case "⬆ (Descending)":
                            List<WaitingListItem> SortedList = currentList.OrderByDescending(o => o.OrganRegisteredDate.ToDateTime()).ToList();
                            WaitingListItemsList.ItemsSource = SortedList;
                            break;
                        case "⬇ (Ascending)":
                            SortedList = currentList.OrderBy(o => o.OrganRegisteredDate.ToDateTime()).ToList();
                            WaitingListItemsList.ItemsSource = SortedList;
                            break;
                        case "Clear":
                            WaitingListItemsList.ItemsSource = currentList;
                            AscendingDescendingPicker.SelectedIndex = -1;

                            break;
                    }
                    break;
                case "Dereg. Date":
                    switch (AscendingDescendingPicker.SelectedItem)
                    {
                        case "⬆ (Descending)":
                            List<WaitingListItem> finalList = new List<WaitingListItem>();
                            List<WaitingListItem> deregisteredList = new List<WaitingListItem>();
                            List<WaitingListItem> nonDeregisteredList = new List<WaitingListItem>();

                            foreach (WaitingListItem item in currentList)
                            {
                                if (item.OrganDeregisteredDate != null)
                                {
                                    deregisteredList.Add(item);
                                }
                                else
                                {
                                    nonDeregisteredList.Add(item);
                                }
                            }

                            List<WaitingListItem> sortedDeregisteredList = deregisteredList.OrderByDescending(o => o.OrganDeregisteredDate.ToDateTime()).ToList();

                            foreach (WaitingListItem item in sortedDeregisteredList)
                            {
                                finalList.Add(item);
                            }
                            foreach (WaitingListItem item in nonDeregisteredList)
                            {
                                finalList.Add(item);
                            }

                            WaitingListItemsList.ItemsSource = finalList;

                            break;
                        case "⬇ (Ascending)":
                            finalList = new List<WaitingListItem>();
                            deregisteredList = new List<WaitingListItem>();
                            nonDeregisteredList = new List<WaitingListItem>();

                            foreach (WaitingListItem item in currentList)
                            {
                                if (item.OrganDeregisteredDate != null)
                                {
                                    deregisteredList.Add(item);
                                }
                                else
                                {
                                    nonDeregisteredList.Add(item);
                                }
                            }

                            sortedDeregisteredList = deregisteredList.OrderBy(o => o.OrganDeregisteredDate.ToDateTime()).ToList();

                            foreach (WaitingListItem item in sortedDeregisteredList)
                            {
                                finalList.Add(item);
                            }
                            foreach (WaitingListItem item in nonDeregisteredList)
                            {
                                finalList.Add(item);
                            }

                            WaitingListItemsList.ItemsSource = finalList;

                            break;
                        case "Clear":
                            WaitingListItemsList.ItemsSource = currentList;
                            AscendingDescendingPicker.SelectedIndex = -1;

                            break;
                    }
                    break;
                case "Dereg. Code":
                    switch (AscendingDescendingPicker.SelectedItem)
                    {
                        case "⬆ (Descending)":
                            List<WaitingListItem> SortedList = currentList.OrderByDescending(o => o.OrganDeregisteredCode).ToList();
                            WaitingListItemsList.ItemsSource = SortedList;
                            break;
                        case "⬇ (Ascending)":
                            SortedList = currentList.OrderBy(o => o.OrganDeregisteredCode).ToList();
                            WaitingListItemsList.ItemsSource = SortedList;
                            break;
                        case "Clear":
                            WaitingListItemsList.ItemsSource = currentList;
                            AscendingDescendingPicker.SelectedIndex = -1;
                            break;
                    }
                    break;
            }      
        }
    }
}
