using System;
using System.Collections.Generic;

using Xamarin.Forms;
using System.Globalization;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the single disease page for 
     * showing the details of a single disease of a user.
     */ 
    public partial class SingleDiseasePage : ContentPage
    {
        DateTimeFormatInfo dateTimeFormat = new DateTimeFormatInfo();
        /*
         * Constructor which initialises the entries of the diseases listview.
         */ 
        public SingleDiseasePage(Disease disease)
        {
            InitializeComponent();
            NameEntry.Text = disease.name;
            DateEntry.Text = disease.diagnosisDate.day + " of " + dateTimeFormat.GetAbbreviatedMonthName(disease.diagnosisDate.month) + ", " + disease.diagnosisDate.year;

            if(disease.isChronic) {
                ChronicEntry.Text = "Yes";
            } else {
                ChronicEntry.Text = "No";
            }

            if (disease.isCured)
            {
                CuredEntry.Text = "Yes";
            }
            else
            {
                CuredEntry.Text = "No";
            }

            IDEntry.Text = disease.id.ToString();

        }

        /*
         * Handles the back button being clicked, returning the user to 
         * the diseases page.
         */ 
        async void BackButtonClicked(object sender, EventArgs args) 
        {
            await Navigation.PopModalAsync();
        }
    }
}
