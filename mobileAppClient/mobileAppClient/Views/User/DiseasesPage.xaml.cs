using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using Xamarin.Forms;
using System.Linq;
using System.Globalization;
using System.Net;
using System.Reflection;
using System.Windows.Input;
using mobileAppClient.odmsAPI;
using Xamarin.Forms.Internals;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the diseases page for 
     * showing all of a users current and cured diseases.
     */ 
    public partial class DiseasesPage : ContentPage
    {
        DateTimeFormatInfo dateTimeFormat = new DateTimeFormatInfo();

        public bool isClinicianAccessing;

        /*
         * Event handler to handle when a user switches between current and cured diseases
         * resetting the sorting and changing the listview items.
         */ 
        void Handle_ValueChanged(object sender, SegmentedControl.FormsPlugin.Abstractions.ValueChangedEventArgs e)
        {
            switch (e.NewValue)
            {
                case 0:
                    if (UserController.Instance.LoggedInUser.currentDiseases.Count == 0)
                    {
                        NoDataLabel.IsVisible = true;
                        DiseasesList.IsVisible = false;
                        SortingInput.IsVisible = false;

                    }
                    else
                    {
                        NoDataLabel.IsVisible = false;
                        DiseasesList.IsVisible = true;
                        SortingInput.IsVisible = true;

                    }
                    DiseasesList.ItemsSource = UserController.Instance.LoggedInUser.currentDiseases;
                    SortingInput.SelectedIndex = -1;
                    AscendingDescendingPicker.SelectedIndex = -1;
                    AscendingDescendingPicker.IsVisible = false;
                    break;
                case 1:
                    if (UserController.Instance.LoggedInUser.curedDiseases.Count == 0)
                    {
                        NoDataLabel.IsVisible = true;
                        DiseasesList.IsVisible = false;
                        SortingInput.IsVisible = false;
                    } else {
                        NoDataLabel.IsVisible = false;
                        DiseasesList.IsVisible = true;
                        SortingInput.IsVisible = true;
                    }
                    DiseasesList.ItemsSource = UserController.Instance.LoggedInUser.curedDiseases;
                    SortingInput.SelectedIndex = -1;
                    AscendingDescendingPicker.SelectedIndex = -1;
                    AscendingDescendingPicker.IsVisible = false;
                    break;
            }
        }

        /*
         * Function to return all of the users current diseases with the chronic 
         * diseases at the top of the list, unsorted.
         */ 
        public List<Disease> returnCurrentDiseasesWithChronicAtTop() {
            List<Disease> finalList = new List<Disease>();
            List<Disease> chronicList = new List<Disease>();
            List<Disease> nonChronicList = new List<Disease>();

            foreach (Disease item in UserController.Instance.LoggedInUser.currentDiseases)
            {
                if (item.isChronic)
                {
                    chronicList.Add(item);
                }
                else
                {
                    nonChronicList.Add(item);
                }
            }

            foreach (Disease item in chronicList)
            {
                finalList.Add(item);
            }
            foreach (Disease item in nonChronicList)
            {
                finalList.Add(item);
            }
            return finalList;
        }

        /*
         * Constructor which sets the chronic items to have red text and also sets the 
         * detail strings for each text cell.
         */ 
        public DiseasesPage()
        {
            InitializeComponent();
            CheckIfClinicianAccessing();

            if (UserController.Instance.LoggedInUser.currentDiseases.Count == 0)
            {
                NoDataLabel.IsVisible = true;
                DiseasesList.IsVisible = false;
                SortingInput.IsVisible = false;
            }

            DiseasesList.ItemsSource = returnCurrentDiseasesWithChronicAtTop();
        }

        /**
         * Checks if a clinician is viewing the user
         */
        private void CheckIfClinicianAccessing()
        {
            if (ClinicianController.Instance.isLoggedIn())
            {
                isClinicianAccessing = true;
                AddDiseaseButton.IsVisible = true;
            }
        }

        /// <summary>
        /// Changes the selected tab to pageToSelect and ensures the view is refreshed
        /// </summary>
        /// <param name="pageToSelect"></param>
        public void refreshDiseases(int pageToSelect)
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

        /*
         * Handles when a single disease it tapped, sending a user to the single disease page 
         * of that given disease.
         */
        async void Handle_ItemTapped(object sender, ItemTappedEventArgs e)
        {
            if (e == null)
            {
                return; //ItemSelected is called on deselection, which results in SelectedItem being set to null
            }
            var singleDiseasePage = new SingleDiseasePage(this, (Disease) DiseasesList.SelectedItem);
            await Navigation.PushAsync(singleDiseasePage);
        }

        /*
         * Handles when a user selects a given attribute of the sorting dropdown 
         * to sort by, sorting the given items in the list view.
         */ 
        void Handle_SelectedIndexChanged(object sender, EventArgs e)
        {
            switch (SortingInput.SelectedItem)
            {
                case "Date":
                    if (SegControl.SelectedSegment == 0)
                    {
                        //Add in chronic special

                        List<Disease> finalList = new List<Disease>();
                        List<Disease> chronicList = new List<Disease>();
                        List<Disease> nonChronicList = new List<Disease>();

                        foreach(Disease item in UserController.Instance.LoggedInUser.currentDiseases) {
                            if(item.isChronic) {
                                chronicList.Add(item);
                            } else {
                                nonChronicList.Add(item);
                            }
                        }

                        List<Disease> sortedChronicList = chronicList.OrderBy(o => o.diagnosisDate.ToDateTime()).ToList();
                        List<Disease> sortedNonChronicList = nonChronicList.OrderBy(o => o.diagnosisDate.ToDateTime()).ToList();

                        foreach(Disease item in sortedChronicList) {
                            finalList.Add(item);
                        }
                        foreach (Disease item in sortedNonChronicList)
                        {
                            finalList.Add(item);
                        }

                        DiseasesList.ItemsSource = finalList;

                    }
                    else
                    {
                        List<Disease> mylist = UserController.Instance.LoggedInUser.curedDiseases;
                        List<Disease> SortedList = mylist.OrderBy(o => o.diagnosisDate.ToDateTime()).ToList();
                        DiseasesList.ItemsSource = SortedList;
                    }
                    AscendingDescendingPicker.IsVisible = true;
                    break;
                case "Name":
                    if (SegControl.SelectedSegment == 0)
                    {
                        //Add in chronic special
                        List<Disease> finalList = new List<Disease>();
                        List<Disease> chronicList = new List<Disease>();
                        List<Disease> nonChronicList = new List<Disease>();

                        foreach (Disease item in UserController.Instance.LoggedInUser.currentDiseases)
                        {
                            if (item.isChronic)
                            {
                                chronicList.Add(item);
                            }
                            else
                            {
                                nonChronicList.Add(item);
                            }
                        }

                        List<Disease> sortedChronicList = chronicList.OrderBy(o => o.name).ToList();
                        List<Disease> sortedNonChronicList = nonChronicList.OrderBy(o => o.name).ToList();

                        foreach (Disease item in sortedChronicList)
                        {
                            finalList.Add(item);
                        }
                        foreach (Disease item in sortedNonChronicList)
                        {
                            finalList.Add(item);
                        }

                        DiseasesList.ItemsSource = finalList;
                    }
                    else
                    {
                        List<Disease> mylist = UserController.Instance.LoggedInUser.curedDiseases;
                        List<Disease> SortedList = mylist.OrderBy(o => o.name).ToList();
                        DiseasesList.ItemsSource = SortedList;
                    }
                    AscendingDescendingPicker.IsVisible = true;
                    break;
                case "Clear":
                    if (SegControl.SelectedSegment == 0)
                    {
                        
                        DiseasesList.ItemsSource = returnCurrentDiseasesWithChronicAtTop();
                        SortingInput.SelectedIndex = -1;
                    }
                    else
                    {
                        DiseasesList.ItemsSource = UserController.Instance.LoggedInUser.curedDiseases;
                        SortingInput.SelectedIndex = -1;
                    }
                    AscendingDescendingPicker.IsVisible = false;
                    break;
            }
        }


        /*
         * Removes the disease from the user and saves the changes
         */
        async void Handle_DeleteClicked(object sender, EventArgs e)
        {
            var mi = ((MenuItem)sender);
            Disease selectedDisease = mi.CommandParameter as Disease;
            bool answer = await DisplayAlert("Are you sure?", "Do you want to remove '" + selectedDisease.name + "' from " + UserController.Instance.LoggedInUser.FullName + "?", "Yes", "No");
            if (answer == true)
            {
                if (SegControl.SelectedSegment == 0)
                {
                    UserController.Instance.LoggedInUser.currentDiseases.Remove(selectedDisease);
                    refreshDiseases(0);
                }
                else
                {
                    UserController.Instance.LoggedInUser.curedDiseases.Remove(selectedDisease);
                    refreshDiseases(1);
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

        /*
         * Handles when a user changes the orientation of the sorting to be either ascending or 
         * descending, changing the order in which items are sorted in the list view.
         */
        void Handle_UpDownChanged(object sender, EventArgs e)
        {
            List<Disease> currentList = (System.Collections.Generic.List<Disease>)DiseasesList.ItemsSource;
            switch (SortingInput.SelectedItem)
            {
                case "Date":
                    switch (AscendingDescendingPicker.SelectedItem)
                    {
                        case "⬆ (Descending)":
                            //Add in chronic special
                            List<Disease> finalList = new List<Disease>();
                            List<Disease> chronicList = new List<Disease>();
                            List<Disease> nonChronicList = new List<Disease>();

                            foreach (Disease item in currentList)
                            {
                                if (item.isChronic)
                                {
                                    chronicList.Add(item);
                                }
                                else
                                {
                                    nonChronicList.Add(item);
                                }
                            }

                            List<Disease> sortedChronicList = chronicList.OrderByDescending(o => o.diagnosisDate.ToDateTime()).ToList();
                            List<Disease> sortedNonChronicList = nonChronicList.OrderByDescending(o => o.diagnosisDate.ToDateTime()).ToList();

                            foreach (Disease item in sortedChronicList)
                            {
                                finalList.Add(item);
                            }
                            foreach (Disease item in sortedNonChronicList)
                            {
                                finalList.Add(item);
                            }

                            DiseasesList.ItemsSource = finalList;

                            break;
                        case "⬇ (Ascending)":
                            //Add in chronic special
                            finalList = new List<Disease>();
                            chronicList = new List<Disease>();
                            nonChronicList = new List<Disease>();

                            foreach (Disease item in currentList)
                            {
                                if (item.isChronic)
                                {
                                    chronicList.Add(item);
                                }
                                else
                                {
                                    nonChronicList.Add(item);
                                }
                            }

                            sortedChronicList = chronicList.OrderBy(o => o.diagnosisDate.ToDateTime()).ToList();
                            sortedNonChronicList = nonChronicList.OrderBy(o => o.diagnosisDate.ToDateTime()).ToList();

                            foreach (Disease item in sortedChronicList)
                            {
                                finalList.Add(item);
                            }
                            foreach (Disease item in sortedNonChronicList)
                            {
                                finalList.Add(item);
                            }

                            DiseasesList.ItemsSource = finalList;

                            break;
                        case "Clear":
                            DiseasesList.ItemsSource = currentList;
                            AscendingDescendingPicker.SelectedIndex = -1;
                            break;
                    }
                    break;
                case "Name":
                    switch (AscendingDescendingPicker.SelectedItem)
                    {
                        case "⬆ (Descending)":
                            //Add in chronic special
                            List<Disease> finalList = new List<Disease>();
                            List<Disease> chronicList = new List<Disease>();
                            List<Disease> nonChronicList = new List<Disease>();

                            foreach (Disease item in currentList)
                            {
                                if (item.isChronic)
                                {
                                    chronicList.Add(item);
                                }
                                else
                                {
                                    nonChronicList.Add(item);
                                }
                            }

                            List<Disease> sortedChronicList = chronicList.OrderByDescending(o => o.name).ToList();
                            List<Disease> sortedNonChronicList = nonChronicList.OrderByDescending(o => o.name).ToList();

                            foreach (Disease item in sortedChronicList)
                            {
                                finalList.Add(item);
                            }
                            foreach (Disease item in sortedNonChronicList)
                            {
                                finalList.Add(item);
                            }

                            DiseasesList.ItemsSource = finalList;
                            break;
                        case "⬇ (Ascending)":
                            //Add in chronic special
                            finalList = new List<Disease>();
                            chronicList = new List<Disease>();
                            nonChronicList = new List<Disease>();

                            foreach (Disease item in currentList)
                            {
                                if (item.isChronic)
                                {
                                    chronicList.Add(item);
                                }
                                else
                                {
                                    nonChronicList.Add(item);
                                }
                            }

                            sortedChronicList = chronicList.OrderBy(o => o.name).ToList();
                            sortedNonChronicList = nonChronicList.OrderBy(o => o.name).ToList();

                            foreach (Disease item in sortedChronicList)
                            {
                                finalList.Add(item);
                            }
                            foreach (Disease item in sortedNonChronicList)
                            {
                                finalList.Add(item);
                            }

                            DiseasesList.ItemsSource = finalList;
                            break;
                        case "Clear":
                            DiseasesList.ItemsSource = currentList;
                            AscendingDescendingPicker.SelectedIndex = -1;
                            break;
                    }
                    break;
            }
        }

        /// <summary>
        /// Opens a single disease adding page when the Add Disease button is pressed
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private async void AddDisease_OnClicked(object sender, EventArgs e)
        {
            Console.WriteLine("Opening single procedure...");
            var singleDiseasePage = new SingleDiseasePage(this);
            await Navigation.PushAsync(singleDiseasePage);
        }
    }
}
