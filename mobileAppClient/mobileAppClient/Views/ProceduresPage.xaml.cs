using System;
using System.Collections.Generic;

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
                    ProceduresList.ItemsSource = UserController.Instance.LoggedInUser.pendingProcedures;
                    break;
                case 1:
                    ProceduresList.ItemsSource = UserController.Instance.LoggedInUser.previousProcedures;
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
    }
}
