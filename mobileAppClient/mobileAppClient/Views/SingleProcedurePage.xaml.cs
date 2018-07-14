using System;
using System.Collections.Generic;

using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class SingleProcedurePage : ContentPage
    {
        public SingleProcedurePage(Procedure procedure)
        {
            InitializeComponent();
            SummaryEntry.Text = procedure.Summary;
            DescriptionEntry.Text = procedure.Description;
            DateDueEntry.Text = "Due on " + procedure.Date.Day + ", " + procedure.Date.Month + ", " + procedure.Date.Year;
            OrgansAffectedEntry.Text = String.Join(", ", procedure.OrgansAffected);
            IDEntry.Text = procedure.Id.ToString();

        }

        async void BackButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PopModalAsync();
        }
    }
}
