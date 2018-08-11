using System;
using System.Collections.Generic;
using System.Linq;
using System.Globalization;
using System.Net;
using System.Windows.Input;
using mobileAppClient.odmsAPI;
using Xamarin.Forms;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the procedures page for 
     * showing all of a users pending and previous procedures.
     */ 
    public partial class ProceduresPage : ContentPage
    {
        private bool isClinicianAccessing;

        /*
         * Event handler to handle when a user switches between pending and previous procedures
         * resetting the sorting and changing the listview items.
         */ 
        void Handle_ProcedureChanged(object sender, SegmentedControl.FormsPlugin.Abstractions.ValueChangedEventArgs e)
        {
            switch (e.NewValue)
            {
                case 0:
                    if (UserController.Instance.LoggedInUser.pendingProcedures.Count == 0)
                    {
                        NoDataLabel.IsVisible = true;
                        ProceduresList.IsVisible = false;
                        SortingInput.IsVisible = false;

                    }
                    else
                    {
                        NoDataLabel.IsVisible = false;
                        ProceduresList.IsVisible = true;
                        SortingInput.IsVisible = true;

                    }
                    ProceduresList.ItemsSource = UserController.Instance.LoggedInUser.pendingProcedures;
                    SortingInput.SelectedIndex = -1;
                    AscendingDescendingPicker.SelectedIndex = -1;
                    AscendingDescendingPicker.IsVisible = false;

                    break;
                case 1:
                    if (UserController.Instance.LoggedInUser.previousProcedures.Count == 0)
                    {
                        NoDataLabel.IsVisible = true;
                        ProceduresList.IsVisible = false;
                        SortingInput.IsVisible = false;
                    }
                    else
                    {
                        NoDataLabel.IsVisible = false;
                        ProceduresList.IsVisible = true;
                        SortingInput.IsVisible = true;
                    }
                    ProceduresList.ItemsSource = UserController.Instance.LoggedInUser.previousProcedures;
                    SortingInput.SelectedIndex = -1;
                    AscendingDescendingPicker.SelectedIndex = -1;
                    AscendingDescendingPicker.IsVisible = false;

                    break;
            }
        }

        /*
         * Constructor which sets the detail strings for each text cell 
         * and also sets the visibility of the no data label and sorting picker.
         */ 
        public ProceduresPage()
        {
            InitializeComponent();
            CheckIfClinicianAccessing();

            if (UserController.Instance.LoggedInUser.pendingProcedures.Count == 0)
            {
                NoDataLabel.IsVisible = true;
                ProceduresList.IsVisible = false;
                SortingInput.IsVisible = false;
            }

            ProceduresList.ItemsSource = UserController.Instance.LoggedInUser.pendingProcedures;
        }

        /**
         * Checks if a clinician is viewing the user
         */
        private void CheckIfClinicianAccessing()
        {
            isClinicianAccessing = ClinicianController.Instance.isLoggedIn();

            if (isClinicianAccessing)
            {
                var addItem = new ToolbarItem
                {
                    Command = OpenAddProcedure,
                    Icon = "add_icon.png",
                };

                this.ToolbarItems.Add(addItem);
            }
        }

        public void refreshProcedures(int pageToSelect)
        {
            if (pageToSelect != 1 && pageToSelect != 0)
            {
                return;
            }

            if (pageToSelect == 0)
            {
                SegControl.SelectedSegment = 1;
            }
            else
            {
                SegControl.SelectedSegment = 0;
            }

            SegControl.SelectedSegment = pageToSelect;
        }

        private ICommand OpenAddProcedure
        {
            get
            {
                return new Command(() => { Navigation.PushAsync(new SingleProcedurePage(this)); });
            }
        }

        /*
         * Handles when a single procedure is tapped, sending a user to the single procedure page 
         * of that given procedure.
         */ 
        async void Handle_ProcedureTapped(object sender, Xamarin.Forms.ItemTappedEventArgs e)
        {
            if (e == null)
            {
                return; //ItemSelected is called on deselection, which results in SelectedItem being set to null
            }
            var singleProcedurePage = new SingleProcedurePage((Procedure)ProceduresList.SelectedItem);
            await Navigation.PushAsync(singleProcedurePage);
        }

        /*
         * Handles when a user selects a given attribute of the sorting dropdown 
         * to sort by, sorting the given items in the list view.
         */ 
        void Handle_SelectedIndexChanged(object sender, System.EventArgs e)
        {
            switch (SortingInput.SelectedItem)
            {
                case "Date":
                    if (SegControl.SelectedSegment == 0)
                    {
                        List<Procedure> mylist = UserController.Instance.LoggedInUser.pendingProcedures;
                        List<Procedure> SortedList = mylist.OrderBy(o => o.date.ToDateTime()).ToList();
                        ProceduresList.ItemsSource = SortedList;
                    }
                    else
                    {
                        List<Procedure> mylist = UserController.Instance.LoggedInUser.previousProcedures;
                        List<Procedure> SortedList = mylist.OrderBy(o => o.date.ToDateTime()).ToList();
                        ProceduresList.ItemsSource = SortedList;
                    }
                    AscendingDescendingPicker.IsVisible = true;
                    break;
                case "Name":
                    if (SegControl.SelectedSegment == 0)
                    {
                        List<Procedure> mylist = UserController.Instance.LoggedInUser.pendingProcedures;
                        List<Procedure> SortedList = mylist.OrderBy(o => o.summary).ToList();
                        ProceduresList.ItemsSource = SortedList;
                    }
                    else
                    {
                        List<Procedure> mylist = UserController.Instance.LoggedInUser.previousProcedures; 
                        List<Procedure> SortedList = mylist.OrderBy(o => o.summary).ToList();
                        ProceduresList.ItemsSource = SortedList;
                    }
                    AscendingDescendingPicker.IsVisible = true;
                    break;
                case "Clear":
                    if (SegControl.SelectedSegment == 0)
                    {
                        ProceduresList.ItemsSource = UserController.Instance.LoggedInUser.pendingProcedures;
                        SortingInput.SelectedIndex = -1;
                    }
                    else
                    {
                        ProceduresList.ItemsSource = UserController.Instance.LoggedInUser.previousProcedures;
                        SortingInput.SelectedIndex = -1;
                    }
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
            List<Procedure> currentList = (System.Collections.Generic.List<Procedure>)ProceduresList.ItemsSource;
            switch (SortingInput.SelectedItem)
            {
                case "Date":
                    switch(AscendingDescendingPicker.SelectedItem) {
                        case "⬆ (Descending)":
                            List<Procedure> SortedList = currentList.OrderByDescending(o => o.date.ToDateTime()).ToList();
                            ProceduresList.ItemsSource = SortedList;
                            break;
                        case "⬇ (Ascending)":
                            SortedList = currentList.OrderBy(o => o.date.ToDateTime()).ToList();
                            ProceduresList.ItemsSource = SortedList;
                            break;
                        case "Clear":
                            ProceduresList.ItemsSource = currentList;
                            AscendingDescendingPicker.SelectedIndex = -1;
                            break;
                    }
                    break;
                case "Name":
                    switch (AscendingDescendingPicker.SelectedItem)
                    {
                        case "⬆ (Descending)":
                            List<Procedure> SortedList = currentList.OrderByDescending(o => o.summary).ToList();
                            ProceduresList.ItemsSource = SortedList;
                            break;
                        case "⬇ (Ascending)":
                            SortedList = currentList.OrderBy(o => o.summary).ToList();
                            ProceduresList.ItemsSource = SortedList;
                            break;
                        case "Clear":
                            ProceduresList.ItemsSource = currentList;
                            AscendingDescendingPicker.SelectedIndex = -1;
                            break;
                    }
                    break;
            }
        }
        async void Handle_EditClicked(object sender, System.EventArgs e)
        {
            var mi = ((MenuItem)sender);
            Procedure selectedProcedure = mi.CommandParameter as Procedure;
            Console.WriteLine("Not implemented");
        }

        async void Handle_DeleteClicked(object sender, System.EventArgs e)
        {
            var mi = ((MenuItem)sender);
            Procedure selectedProcedure = mi.CommandParameter as Procedure;
            bool answer = await DisplayAlert("Are you sure?", "Do you want to remove '" + selectedProcedure.summary + "' from " + UserController.Instance.LoggedInUser.FullName + "?", "Yes", "No");
            if (answer == true)
            {
                if (SegControl.SelectedSegment == 0)
                {
                    UserController.Instance.LoggedInUser.pendingProcedures.Remove(selectedProcedure);
                    refreshProcedures(0);
                }
                else
                {
                    UserController.Instance.LoggedInUser.previousProcedures.Remove(selectedProcedure);
                    refreshProcedures(1);
                }

                UserAPI userAPI = new UserAPI();
                HttpStatusCode userUpdated = await userAPI.UpdateUser(true);
                switch (userUpdated)
                {
                    case HttpStatusCode.Created:
                        await DisplayAlert("",
                            "User procedure successfully removed",
                            "OK");
                        break;
                    case HttpStatusCode.BadRequest:
                        await DisplayAlert("",
                            "User procedure deletion failed (400)",
                            "OK");
                        break;
                    case HttpStatusCode.ServiceUnavailable:
                        await DisplayAlert("",
                            "Server unavailable, check connection",
                            "OK");
                        break;
                    case HttpStatusCode.InternalServerError:
                        await DisplayAlert("",
                            "Server error, please try again",
                            "OK");
                        break;
                }
            }
        }
    }
}
