using System;
using System.Collections.Generic;

using Xamarin.Forms;
using System.Globalization;
using System.Windows.Input;
using mobileAppClient.odmsAPI;
using System.Net;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the single disease page for 
     * showing the details of a single disease of a user.
     */ 
    public partial class SingleDiseasePage : ContentPage
    {
        DateTimeFormatInfo dateTimeFormat = new DateTimeFormatInfo();

        public SingleDiseasePage()
        {
            InitializeComponent();
            NameEntry.Placeholder = "Disease name";
            DateEntry.MaximumDate = DateTime.Today;
            ChronicEntry.On = false;
            CuredEntry.On = false;

            if (ClinicianController.Instance.isLoggedIn())
            {
                NameEntry.IsEnabled = true;
                DateEntry.IsEnabled = true;
                ChronicEntry.IsEnabled = true;
                CuredEntry.IsEnabled = true;
                 
                var addItem = new ToolbarItem
                {
                    Command = AddDisease,
                    Text = "Add"
                };
                this.ToolbarItems.Add(addItem);
            }
        }


        /*
* Constructor which initialises the entries of the diseases listview.
*/
        public SingleDiseasePage(Disease disease)
        {
            InitializeComponent();
            
            NameEntry.Text = disease.Name;
            DateEntry.Date = disease.DiagnosisDate.ToDateTime();

            if (disease.IsChronic)
            {
                ChronicEntry.On = true;
            }
            else
            {
                ChronicEntry.On = false;
            }

            if (disease.IsCured)
            {
                CuredEntry.On = true;
            }
            else
            {
                CuredEntry.On = false;
            }


            if (ClinicianController.Instance.isLoggedIn())
            {
                NameEntry.IsEnabled = true;
                DateEntry.MaximumDate = DateTime.Today;
                DateEntry.IsEnabled = true;
                ChronicEntry.IsEnabled = true;
                CuredEntry.IsEnabled = true;

                var saveItem = new ToolbarItem
                {
                    Command = SaveDisease,
                    CommandParameter = disease.Id,
                    Text = "Save",
                };

                this.ToolbarItems.Add(saveItem);
            }

        }
        private ICommand AddDisease
        {
            get
            {
                return new Command(async () =>
                {
                    Console.WriteLine("Saving disease...");
                    if(!InputValidation.IsValidTextInput(NameEntry.Text, true, false))
                    {
                        await DisplayAlert("", "Disease name cannot be empty.", "OK");
                    }
               
                    Disease disease = new Disease(NameEntry.Text, new CustomDate(DateEntry.Date), ChronicEntry.On, CuredEntry.On);
                    UserController.Instance.LoggedInUser.currentDiseases.Add(disease);

                    await SendDiseaseAsync();

                });
            }
        }

        private ICommand SaveDisease
        {
            get
            {
                return new Command(async (Object diseaseToUpdate) =>
                {
                    Console.WriteLine("Saving disease...");
                    if (!InputValidation.IsValidTextInput(NameEntry.Text, true, false))
                    {

                        await DisplayAlert("", "Disease name cannot be empty.", "OK");
                    }
                    else if (ChronicEntry.On && CuredEntry.On)
                    {

                        await DisplayAlert("", "Disease cannot be both cured and chronic.", "OK");
                    }
                    else
                    {

                        User user = UserController.Instance.LoggedInUser;
                        foreach (Disease disease in user.currentDiseases)
                        {
                            if (disease.Id == int.Parse(DiseaseId.Text))
                            {
                                disease.Name = NameEntry.Text;
                                disease.DiagnosisDate = new CustomDate(DateEntry.Date);
                                disease.IsChronic = ChronicEntry.On;
                                disease.IsCured = CuredEntry.On;
                                await SendDiseaseAsync();
                                break;
                            }
                        }
                    }
                });
            }
        }

        private async System.Threading.Tasks.Task SendDiseaseAsync()
        {
            UserAPI userAPI = new UserAPI();
            HttpStatusCode userUpdated = await userAPI.UpdateUser(true);
            switch (userUpdated)
            {
                case HttpStatusCode.Created:
                    await DisplayAlert("",
                    "User Successfully updated",
                    "OK");
                    break;
                case HttpStatusCode.BadRequest:
                    await DisplayAlert("",
                    "User Update Failed (400)",
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

        private void ChronicCheck(object sender, ToggledEventArgs e)
        {
            if (ChronicEntry.On && CuredEntry.On)
            {
                CuredEntry.On = false;
            } 
        }

        private void CuredCheck(object sender, ToggledEventArgs e)
        {
            if (ChronicEntry.On && CuredEntry.On)
            {
                ChronicEntry.On = false;
            }
        }
    }
}
