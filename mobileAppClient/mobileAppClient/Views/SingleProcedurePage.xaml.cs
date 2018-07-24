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
            DateDueEntry.Text = procedure.Date.day + ", " + procedure.Date.month + ", " + procedure.Date.year;
            IDEntry.Text = procedure.Id.ToString();
            foreach (string item in procedure.OrgansAffected)
            {
                TextCell cell = new TextCell();
                cell.Text = item;
                cell.TextColor = Color.Gray;
                OrgansAffectedSection.Add(cell);
            }

        }

        async void BackButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PopModalAsync();
        }
    }
}
