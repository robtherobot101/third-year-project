using System;
using System.Collections.Generic;

using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class SingleDiseasePage : ContentPage
    {


        public SingleDiseasePage(Disease disease)
        {
            InitializeComponent();
            NameEntry.Text = disease.Name;
            DateEntry.Text = disease.DiagnosisDateString;

            if(disease.IsChronic) {
                ChronicEntry.Text = "Yes";
            } else {
                ChronicEntry.Text = "No";
            }

            if (disease.IsCured)
            {
                CuredEntry.Text = "Yes";
            }
            else
            {
                CuredEntry.Text = "No";
            }

            IDEntry.Text = disease.Id.ToString();

        }


        async void BackButtonClicked(object sender, EventArgs args) 
        {
            await Navigation.PopModalAsync();
        }
    }
}
