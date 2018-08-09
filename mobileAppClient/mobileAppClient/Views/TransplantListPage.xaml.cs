using Android.App;
using mobileAppClient.odmsAPI;
using mobileAppClient.Views;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace mobileAppClient
{
	[XamlCompilation(XamlCompilationOptions.Compile)]

	public partial class TransplantListPage : ContentPage
	{
        DateTimeFormatInfo dateTimeFormat = new DateTimeFormatInfo();
        public TransplantListPage ()
		{
            Console.WriteLine("About initialize component");

            InitializeComponent();
            Console.WriteLine("About setup items");

            setupItems();

        }

        public async Task setupItems()
        {
            Console.WriteLine("About to prepare items");
            List<WaitingListItem> waitingOn = await prepareWaitingListItems();

            Console.WriteLine("Items in list: " + waitingOn.Count);
            if (waitingOn.Count == 0)
            {
                Console.WriteLine("No items, about to hide list.");
                NoDataLabel.IsVisible = true;
                TransplantList.IsVisible = false;
                SortingInput.IsVisible = false;
                AscendingDescendingPicker.IsVisible = false;
            } else
            {
                NoDataLabel.IsVisible = false;
                TransplantList.IsVisible = true;
                SortingInput.IsVisible = true;
                AscendingDescendingPicker.IsVisible = true;
            }
            Console.WriteLine("About to set ItemSource");
            TransplantList.ItemsSource = waitingOn;
        }

        public async Task<List<WaitingListItem>> prepareWaitingListItems()
        {
            String query = prepareQuery();
            Console.WriteLine("About to query with: " + query);
            List<WaitingListItem> items = await new TransplantListAPI().getItems(query);
            items = waitingOn(items);
            items = style(items);
            return items;
        }

        protected override void OnAppearing()
        {
            refreshPage();
        }

        public void refreshPage()
        {
            Handle_FilterChange(null, null);
        }

        public String prepareQuery()
        {
            String query = "";
            if((String)OrganFilter.SelectedItem != "Any")
            {
                String[] words = ((String)OrganFilter.SelectedItem).Split(' ');
                String organ = String.Join("-", words);
                organ = organ.ToLower();
                query += "organ=" + organ;
            }


            if(RegionFilter.Text != null && RegionFilter.Text != "")
            {
                if (query.Length > 0)
                {
                    query = query + "&";
                }

                String[] words = ((String)RegionFilter.Text).Split(' ');
                String region = String.Join("+", words);
                query += "region=" + region;
            }

            if (query.Length > 0)
            {
                query = "?" + query;
            }
            return query;
        }

        public async void Handle_ItemTapped()
        {
            WaitingListItem tapped = (WaitingListItem)TransplantList.SelectedItem;
            if(tapped != null)
            {
                await Navigation.PushModalAsync(new SingleWaitingListItemPage(tapped, true));
            }
        }


        public List<WaitingListItem> waitingOn(List<WaitingListItem> items)
        {
            foreach (WaitingListItem item in new List<WaitingListItem>(items))
            {
                if (item.organDeregisteredDate != null)
                {
                    items.Remove(item);
                }
            }
            return items;
        }

        public List<WaitingListItem> style(List<WaitingListItem> items)
        {
            foreach (WaitingListItem item in items)
            {
                if (item.isConflicting)
                {
                    item.CellColour = Color.Red;
                    item.DetailString = "(Conflicting)" + "Registered on " + item.organRegisteredDate.day + " of " + dateTimeFormat.GetAbbreviatedMonthName(item.organRegisteredDate.month) + ", " + item.organRegisteredDate.year;
                }
                else
                {
                    item.CellColour = Color.Black;
                    item.DetailString = "Registered on " + item.organRegisteredDate.day + " of " + dateTimeFormat.GetAbbreviatedMonthName(item.organRegisteredDate.month) + ", " + item.organRegisteredDate.year;
                }
            }
            return items;
        }

        async void Handle_FilterChange(object sender, System.EventArgs e)
        {
            await setupItems();
            Handle_SelectedIndexChanged(null,null);
            Handle_UpDownChanged(null, null);
        }

        /*
         * Handles when a user selects a given attribute of the sorting dropdown 
         * to sort by, sorting the given items in the list view.
         */
        void Handle_SelectedIndexChanged(object sender, System.EventArgs e)
        {
            List<WaitingListItem> currentList = (List<WaitingListItem>)TransplantList.ItemsSource;
            switch (SortingInput.SelectedItem)
            {
                case "Organ":
                    List<WaitingListItem> SortedList = currentList.OrderBy(o => o.organType).ToList();
                    TransplantList.ItemsSource = SortedList;

                    AscendingDescendingPicker.IsVisible = true;
                    break;
                case "Reg. Date":
                    SortedList = currentList.OrderBy(o => o.organRegisteredDate.ToDateTime()).ToList();
                    TransplantList.ItemsSource = SortedList;

                    AscendingDescendingPicker.IsVisible = true;
                    break;
                case "Clear":

                    TransplantList.ItemsSource = currentList;
                    SortingInput.SelectedIndex = -1;

                    AscendingDescendingPicker.IsVisible = false;
                    break;
            }
        }

        /*
         * Handles when a user changes the orientation of the sorting to be either ascending or 
         * descending, changing the order in which items are sorted in the list view.
         */
        void Handle_UpDownChanged(object sender, System.EventArgs e)
        {
            List<WaitingListItem> currentList = (List<WaitingListItem>)TransplantList.ItemsSource;
            switch (SortingInput.SelectedItem)
            {
                case "Organ":
                    switch (AscendingDescendingPicker.SelectedItem)
                    {
                        case "⬆ (Descending)":
                            List<WaitingListItem> SortedList = currentList.OrderByDescending(o => o.organType).ToList();
                            TransplantList.ItemsSource = SortedList;
                            break;
                        case "⬇ (Ascending)":
                            SortedList = currentList.OrderBy(o => o.organType).ToList();
                            TransplantList.ItemsSource = SortedList;
                            break;
                        case "Clear":
                            TransplantList.ItemsSource = currentList;
                            AscendingDescendingPicker.SelectedIndex = -1;
                            break;
                    }
                    break;
                case "Reg. Date":
                    switch (AscendingDescendingPicker.SelectedItem)
                    {
                        case "⬆ (Descending)":
                            List<WaitingListItem> SortedList = currentList.OrderByDescending(o => o.organRegisteredDate.ToDateTime()).ToList();
                            TransplantList.ItemsSource = SortedList;
                            break;
                        case "⬇ (Ascending)":
                            SortedList = currentList.OrderBy(o => o.organRegisteredDate.ToDateTime()).ToList();
                            TransplantList.ItemsSource = SortedList;
                            break;
                        case "Clear":
                            TransplantList.ItemsSource = currentList;
                            AscendingDescendingPicker.SelectedIndex = -1;

                            break;
                    }
                    break;
            }
        }
    }
}