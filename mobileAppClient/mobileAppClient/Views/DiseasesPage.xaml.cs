using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class DiseasesPage : ContentPage
    {
        ObservableCollection<Disease> diseasesList = new ObservableCollection<Disease>();

        public DiseasesPage()
        {
            InitializeComponent();

            diseasesList.Add(new Disease("David", 2));
            diseasesList.Add(new Disease("Andy", 2));
            diseasesList.Add(new Disease("Tom", 2));
            diseasesList.Add(new Disease("AHAHAHAHHAHAHHAHAHAHAHHA", 2));
            DiseasesList.ItemsSource = diseasesList;

        }

        void OnDiseaseItemSelection(object sender, SelectedItemChangedEventArgs e)
        {
            if (e.SelectedItem == null)
            {
                return; //ItemSelected is called on deselection, which results in SelectedItem being set to null
            }
            DisplayAlert("Item Selected", e.SelectedItem.ToString(), "Ok");
            //((ListView)sender).SelectedItem = null; //uncomment line if you want to disable the visual selection state.
        }
    }
}
