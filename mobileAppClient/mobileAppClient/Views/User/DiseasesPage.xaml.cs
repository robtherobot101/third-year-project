using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using Xamarin.Forms;
using System.Linq;
using System.Globalization;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the diseases page for 
     * showing all of a users current and cured diseases.
     */ 
    public partial class DiseasesPage : ContentPage
    {
        DateTimeFormatInfo dateTimeFormat = new DateTimeFormatInfo();
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
                if (item.IsChronic)
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

            //FOR SOME REASON IT DOESNT WORK IF I HAVE THESE IN THE CONSTRUCTORS??

            foreach(Disease item in UserController.Instance.LoggedInUser.currentDiseases) {
                if(item.IsChronic) {
                    item.CellText = item.Name + " (CHRONIC)";
                    item.CellColour = Color.Red;
                } else {
                    item.CellText = item.Name;
                    item.CellColour = Color.Blue;
                }
                item.DiagnosisDateString = "Diagnosed on " + item.DiagnosisDate.day + " of " + dateTimeFormat.GetAbbreviatedMonthName(item.DiagnosisDate.month) + ", " + item.DiagnosisDate.year;



            }
            foreach (Disease item in UserController.Instance.LoggedInUser.curedDiseases)
            {
                item.DiagnosisDateString = "Diagnosed on " + item.DiagnosisDate.day + " of " + dateTimeFormat.GetAbbreviatedMonthName(item.DiagnosisDate.month) + ", " + item.DiagnosisDate.year;
                item.CellText = item.Name;
                item.CellColour = Color.Blue;
            }

            if (UserController.Instance.LoggedInUser.currentDiseases.Count == 0)
            {
                NoDataLabel.IsVisible = true;
                DiseasesList.IsVisible = false;
                SortingInput.IsVisible = false;
            }

            DiseasesList.ItemsSource = returnCurrentDiseasesWithChronicAtTop();
  

        }

        /*
         * Handles when a single disease it tapped, sending a user to the single disease page 
         * of that given disease.
         */ 
        async void Handle_ItemTapped(object sender, Xamarin.Forms.ItemTappedEventArgs e)
        {
            if (e == null)
            {
                return; //ItemSelected is called on deselection, which results in SelectedItem being set to null
            }
            var singleDiseasePage = new SingleDiseasePage((Disease)DiseasesList.SelectedItem);
            await Navigation.PushAsync(singleDiseasePage);
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
                        //Add in chronic special

                        List<Disease> finalList = new List<Disease>();
                        List<Disease> chronicList = new List<Disease>();
                        List<Disease> nonChronicList = new List<Disease>();

                        foreach(Disease item in UserController.Instance.LoggedInUser.currentDiseases) {
                            if(item.IsChronic) {
                                chronicList.Add(item);
                            } else {
                                nonChronicList.Add(item);
                            }
                        }

                        List<Disease> sortedChronicList = chronicList.OrderBy(o => o.DiagnosisDate.ToDateTime()).ToList();
                        List<Disease> sortedNonChronicList = nonChronicList.OrderBy(o => o.DiagnosisDate.ToDateTime()).ToList();

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
                        List<Disease> SortedList = mylist.OrderBy(o => o.DiagnosisDate.ToDateTime()).ToList();
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
                            if (item.IsChronic)
                            {
                                chronicList.Add(item);
                            }
                            else
                            {
                                nonChronicList.Add(item);
                            }
                        }

                        List<Disease> sortedChronicList = chronicList.OrderBy(o => o.Name).ToList();
                        List<Disease> sortedNonChronicList = nonChronicList.OrderBy(o => o.Name).ToList();

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
                        List<Disease> SortedList = mylist.OrderBy(o => o.Name).ToList();
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
         * Handles when a user changes the orientation of the sorting to be either ascending or 
         * descending, changing the order in which items are sorted in the list view.
         */ 
        void Handle_UpDownChanged(object sender, System.EventArgs e)
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
                                if (item.IsChronic)
                                {
                                    chronicList.Add(item);
                                }
                                else
                                {
                                    nonChronicList.Add(item);
                                }
                            }

                            List<Disease> sortedChronicList = chronicList.OrderByDescending(o => o.DiagnosisDate.ToDateTime()).ToList();
                            List<Disease> sortedNonChronicList = nonChronicList.OrderByDescending(o => o.DiagnosisDate.ToDateTime()).ToList();

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
                                if (item.IsChronic)
                                {
                                    chronicList.Add(item);
                                }
                                else
                                {
                                    nonChronicList.Add(item);
                                }
                            }

                            sortedChronicList = chronicList.OrderBy(o => o.DiagnosisDate.ToDateTime()).ToList();
                            sortedNonChronicList = nonChronicList.OrderBy(o => o.DiagnosisDate.ToDateTime()).ToList();

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
                                if (item.IsChronic)
                                {
                                    chronicList.Add(item);
                                }
                                else
                                {
                                    nonChronicList.Add(item);
                                }
                            }

                            List<Disease> sortedChronicList = chronicList.OrderByDescending(o => o.Name).ToList();
                            List<Disease> sortedNonChronicList = nonChronicList.OrderByDescending(o => o.Name).ToList();

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
                                if (item.IsChronic)
                                {
                                    chronicList.Add(item);
                                }
                                else
                                {
                                    nonChronicList.Add(item);
                                }
                            }

                            sortedChronicList = chronicList.OrderBy(o => o.Name).ToList();
                            sortedNonChronicList = nonChronicList.OrderBy(o => o.Name).ToList();

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
    }
}
