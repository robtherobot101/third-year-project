using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class DiseasesPage : ContentPage
    {
        void Handle_ValueChanged(object sender, SegmentedControl.FormsPlugin.Abstractions.ValueChangedEventArgs e)
        {
            switch (e.NewValue)
            {
                case 0:
                    DiseasesList.ItemsSource = UserController.Instance.LoggedInUser.currentDiseases;
                    break;
                case 1:
                    DiseasesList.ItemsSource = UserController.Instance.LoggedInUser.curedDiseases;
                    break;
            }
        }

        public DiseasesPage()
        {
            InitializeComponent();

            //FOR SOME REASON IT DOESNT WORK IF I HAVE THESE IN THE CONSTRUCTORS??

            foreach(Disease item in UserController.Instance.LoggedInUser.currentDiseases) {
                item.DiagnosisDateString = "Diagnosed on " + item.DiagnosisDate.day + ", " + item.DiagnosisDate.month + ", " + item.DiagnosisDate.year;
            }
            foreach (Disease item in UserController.Instance.LoggedInUser.curedDiseases)
            {
                item.DiagnosisDateString = "Diagnosed on " + item.DiagnosisDate.day + ", " + item.DiagnosisDate.month + ", " + item.DiagnosisDate.year;
            }

            DiseasesList.ItemsSource = UserController.Instance.LoggedInUser.currentDiseases;
  

        }




        async void Handle_ItemTapped(object sender, Xamarin.Forms.ItemTappedEventArgs e)
        {
            if (e == null)
            {
                return; //ItemSelected is called on deselection, which results in SelectedItem being set to null
            }
            var singleDiseasePage = new SingleDiseasePage((Disease)DiseasesList.SelectedItem);
            await Navigation.PushModalAsync(singleDiseasePage);
        }
    }
}
