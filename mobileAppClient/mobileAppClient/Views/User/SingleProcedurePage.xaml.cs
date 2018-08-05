using System;
using System.Collections.Generic;
using System.Globalization;

using Xamarin.Forms;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the single procedure page for 
     * showing the details of a single procedure of a user.
     */ 
    public partial class SingleProcedurePage : ContentPage
    {
        DateTimeFormatInfo dateTimeFormat = new DateTimeFormatInfo();
        /*
         * Constructor which initialises the entries of the procedures listview.
         */ 
        public SingleProcedurePage(Procedure procedure)
        {
            InitializeComponent();
            SummaryEntry.Text = procedure.Summary;
            DescriptionEntry.Text = procedure.Description;
            DateDueEntry.Text = procedure.Date.day + " of " + dateTimeFormat.GetAbbreviatedMonthName(procedure.Date.month) + ", " + procedure.Date.year;
            IDEntry.Text = procedure.Id.ToString();
            foreach (string item in procedure.OrgansAffected)
            {
                TextCell cell = new TextCell();
                cell.Text = item;
                cell.TextColor = Color.Gray;
                OrgansAffectedSection.Add(cell);
            }

        }

        /*
         * Handles the back button being clicked, returning the user to 
         * the procedures page.
         */ 
        async void BackButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PopModalAsync();
        }
    }
}
