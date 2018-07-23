using System;
using System.Collections.Generic;
using System.Linq;

using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class ProceduresPage : ContentPage
    {
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

        public ProceduresPage()
        {
            InitializeComponent();

            //FOR SOME REASON IT DOESNT WORK IF I HAVE THESE IN THE CONSTRUCTORS??

            foreach (Procedure item in UserController.Instance.LoggedInUser.pendingProcedures)
            {
                item.DetailString = item.Description + ", due on " + item.Date.day + ", " + item.Date.month + ", " + item.Date.year;
            }
            foreach (Procedure item in UserController.Instance.LoggedInUser.previousProcedures)
            {
                item.DetailString = item.Description + ", due on " + item.Date.day + ", " + item.Date.month + ", " + item.Date.year;
            }

            if (UserController.Instance.LoggedInUser.pendingProcedures.Count == 0)
            {
                NoDataLabel.IsVisible = true;
                ProceduresList.IsVisible = false;
                SortingInput.IsVisible = false;
            }

            ProceduresList.ItemsSource = UserController.Instance.LoggedInUser.pendingProcedures;


        }

        async void Handle_ProcedureTapped(object sender, Xamarin.Forms.ItemTappedEventArgs e)
        {
            if (e == null)
            {
                return; //ItemSelected is called on deselection, which results in SelectedItem being set to null
            }
            var singleProcedurePage = new SingleProcedurePage((Procedure)ProceduresList.SelectedItem);
            await Navigation.PushModalAsync(singleProcedurePage);
        }

        void Handle_SelectedIndexChanged(object sender, System.EventArgs e)
        {
            switch (SortingInput.SelectedItem)
            {                 case "Date":                     if (SegControl.SelectedSegment == 0)
                    {
                        List<Procedure> mylist = UserController.Instance.LoggedInUser.pendingProcedures;
                        List<Procedure> SortedList = mylist.OrderBy(o => o.Date.ToDateTime()).ToList();
                        ProceduresList.ItemsSource = SortedList;
                    }
                    else
                    {
                        List<Procedure> mylist = UserController.Instance.LoggedInUser.previousProcedures;
                        List<Procedure> SortedList = mylist.OrderBy(o => o.Date.ToDateTime()).ToList();
                        ProceduresList.ItemsSource = SortedList;
                    }
                    AscendingDescendingPicker.IsVisible = true;                     break;                 case "Name":
                    if (SegControl.SelectedSegment == 0)
                    {
                        List<Procedure> mylist = UserController.Instance.LoggedInUser.pendingProcedures;
                        List<Procedure> SortedList = mylist.OrderBy(o => o.Summary).ToList();
                        ProceduresList.ItemsSource = SortedList;
                    }
                    else
                    {
                        List<Procedure> mylist = UserController.Instance.LoggedInUser.previousProcedures; 
                        List<Procedure> SortedList = mylist.OrderBy(o => o.Summary).ToList();
                        ProceduresList.ItemsSource = SortedList;
                    }
                    AscendingDescendingPicker.IsVisible = true;                     break;                 case "Clear":
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
                    break;             } 


        }

        void Handle_UpDownChanged(object sender, System.EventArgs e)
        {
            List<Procedure> currentList = (System.Collections.Generic.List<Procedure>)ProceduresList.ItemsSource;
            switch (SortingInput.SelectedItem)
            {
                case "Date":
                    switch(AscendingDescendingPicker.SelectedItem) {
                        case "⬆ (Descending)":
                            List<Procedure> SortedList = currentList.OrderByDescending(o => o.Date.ToDateTime()).ToList();
                            ProceduresList.ItemsSource = SortedList;
                            break;
                        case "⬇ (Ascending)":
                            SortedList = currentList.OrderBy(o => o.Date.ToDateTime()).ToList();
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
                            List<Procedure> SortedList = currentList.OrderByDescending(o => o.Summary).ToList();
                            ProceduresList.ItemsSource = SortedList;
                            break;
                        case "⬇ (Ascending)":
                            SortedList = currentList.OrderBy(o => o.Summary).ToList();
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
    }
}
