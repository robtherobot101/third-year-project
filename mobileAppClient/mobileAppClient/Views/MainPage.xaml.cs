using mobileAppClient.odmsAPI;
using mobileAppClient.Views;
using System;
using System.Collections.ObjectModel;
using System.Reflection;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;


namespace mobileAppClient
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    /*
     * Class to handle all functionality regarding the main menu slider for 
     * displaying all user navigation options.
     */ 
    public partial class MainPage : MasterDetailPage
    {
        public ObservableCollection<MasterPageItem> menuList { get; set; }
    
        /*
         * Constructor which adds all of the menu items with given icons and titles.
         * Also sets the landing page to be the overview page.
         */
        public MainPage(bool isClinicianView)
        {
            InitializeComponent();
            menuList = new ObservableCollection<MasterPageItem>();
            navigationDrawerList.ItemsSource = menuList;

            // If a clinician is entering into a user's view
            if (isClinicianView)
            {
                clinicianViewingUser();
            } else
            {
                UserController.Instance.mainPageController = this;
            }
        }

        /*
         * Method which is used when a user logs out, opening the login page again.
         */
        private async void LogoutUser()
        {
            // Remove token from server
            LoginAPI loginAPI = new LoginAPI();
            await loginAPI.Logout(false);

            // Logout any currently stored user
            UserController.Instance.Logout();

            // Open the login page
            var loginPage = new LoginPage();
            await Navigation.PopModalAsync(true);
        }

        /*
         * Method which is used when a user logs out, opening the login page again.
         */
        private async void LogoutClinician()
        {
            // Remove token from server
            LoginAPI loginAPI = new LoginAPI();
            await loginAPI.Logout(true);

            // Clear any previously selected user
            UserController.Instance.Logout();

            // Logout clinician
            ClinicianController.Instance.Logout();

            // Open the login page
            var loginPage = new LoginPage();
            await Navigation.PopModalAsync(true);
        }

        /*
         * Sets up the Main page for a user's view
         */
        public void userLoggedIn()
        {
            Detail = new NavigationPage((Page)Activator.CreateInstance(typeof(UserOverviewPage)));
            updateUserProfileBar();

            menuList.Clear();

            var overviewPage = new MasterPageItem() { Title = "Overview", Icon = "home_icon.png", TargetType = typeof(UserOverviewPage) };
            var attributesPage = new MasterPageItem() { Title = "Attributes", Icon = "attributes_icon.png", TargetType = typeof(AttributesPage) };
            var organsPage = new MasterPageItem() { Title = "Organs", Icon = "organs_icon.png", TargetType = typeof(OrgansPage) };
            var logoutPage = new MasterPageItem() { Title = "Logout", Icon = "logout_icon.png", TargetType = typeof(LoginPage) };
            var diseasesPage = new MasterPageItem() { Title = "Diseases", Icon = "diseases_icon.png", TargetType = typeof(DiseasesPage) };
            var proceduresPage = new MasterPageItem() { Title = "Procedures", Icon = "procedures_icon.png", TargetType = typeof(ProceduresPage) };
            var waitingListItemsPage = new MasterPageItem() { Title = "Waiting List", Icon = "waitinglist_icon.png",TargetType = typeof(WaitingListItemsPage) };
            var medicationsPage = new MasterPageItem() { Title = "Medications", Icon = "medications_icon.png",TargetType = typeof(MedicationsPage) };
            var userSettingsPage = new MasterPageItem() { Title = "Settings", Icon = "settings_icon.png", TargetType = typeof(UserSettings) };
            var livesSavedPage = new MasterPageItem() { Title = "Lives Saved", Icon = "score_icon.png", TargetType = typeof(PointsPage) };

            // Adding menu items to menuList
            menuList.Add(overviewPage);
            menuList.Add(attributesPage);
            menuList.Add(livesSavedPage);
            menuList.Add(organsPage);
            menuList.Add(medicationsPage);
            menuList.Add(diseasesPage);
            menuList.Add(proceduresPage);
            menuList.Add(waitingListItemsPage);
            menuList.Add(userSettingsPage);
            menuList.Add(logoutPage);
        }

        /*
         * Sets up the Main page for a clinician's view
         */
        public void clinicianLoggedIn()
        {
            Detail = new NavigationPage((Page)Activator.CreateInstance(typeof(ClinicianOverviewPage)));
            updateClinicianProfileBar();

            menuList.Clear();

            var overviewPage = new MasterPageItem() { Title = "Overview", Icon = "home_icon.png", TargetType = typeof(ClinicianOverviewPage) };
            var userSearchPage = new MasterPageItem() { Title = "User Search", Icon = "users_icon.png", TargetType = typeof(UserSearchPage) };
            var attributesPage = new MasterPageItem() { Title = "Attributes", Icon = "attributes_icon.png", TargetType = typeof(AttributesPageClinician) };
            var transplantListPage = new MasterPageItem() { Title = "Transplant List", Icon = "waitinglist_icon.png", TargetType = typeof(TransplantListPage) };
            var logoutPage = new MasterPageItem() { Title = "Logout", Icon = "logout_icon.png", TargetType = typeof(LoginPage) };
            var mapPage = new MasterPageItem() { Title = "Map", Icon = "map_icon.png", TargetType = typeof(ClinicianMapPage) };
            var messagesPage = new MasterPageItem() { Title = "Messages", Icon = "messages_icon.png", TargetType = typeof(ClinicianMessagingPage) };

            // Adding menu items to menuList
            menuList.Add(overviewPage);
            menuList.Add(userSearchPage);
            menuList.Add(attributesPage);
            menuList.Add(transplantListPage);
            menuList.Add(mapPage);
            menuList.Add(messagesPage);
            menuList.Add(logoutPage);
        }

        public void clinicianViewingUser()
        {
            Detail = new NavigationPage((Page)Activator.CreateInstance(typeof(UserOverviewPage)));
            updateUserViewerProfileBar();
           

            menuList.Clear();

            var overviewPage = new MasterPageItem() { Title = "Overview", Icon = "home_icon.png", TargetType = typeof(UserOverviewPage) };
            var attributesPage = new MasterPageItem() { Title = "Attributes", Icon = "attributes_icon.png", TargetType = typeof(AttributesPage) };
            var organsPage = new MasterPageItem() { Title = "Organs", Icon = "organs_icon.png", TargetType = typeof(OrgansPage) };
            var diseasesPage = new MasterPageItem() { Title = "Diseases", Icon = "diseases_icon.png", TargetType = typeof(DiseasesPage) };
            var proceduresPage = new MasterPageItem() { Title = "Procedures", Icon = "procedures_icon.png", TargetType = typeof(ProceduresPage) };
            var waitingListItemsPage = new MasterPageItem() { Title = "Waiting List", Icon = "waitinglist_icon.png", TargetType = typeof(WaitingListItemsPage) };
            var medicationsPage = new MasterPageItem() { Title = "Medications", Icon = "medications_icon.png", TargetType = typeof(MedicationsPage) };
            

            // Adding menu items to menuList
            menuList.Add(overviewPage);
            menuList.Add(attributesPage);
            menuList.Add(organsPage);
            menuList.Add(medicationsPage);
            menuList.Add(diseasesPage);
            menuList.Add(proceduresPage);
            menuList.Add(waitingListItemsPage);
        }

        /*
         * Function used to Stops the back button from working and 
         * opening the main view without a logged in user
         */
        protected override bool OnBackButtonPressed()
        {
            return true;
        }

        /*
         * Handles when a given page is selected in the menu slider and sends the user to that page.
         */
        private void OnMenuItemSelected(object sender, SelectedItemChangedEventArgs e)
        {
            var item = (MasterPageItem)e.SelectedItem;
            Type page = item.TargetType;

            switch(page.Name)
            {
                case "LoginPage":
                    if (ClinicianController.Instance.isLoggedIn())
                    {
                        LogoutClinician();
                    }
                    else
                    {
                        LogoutUser();
                    }
                    break;
                default:
                    try
                    {
                        NavigationPage content = new NavigationPage((Page)Activator.CreateInstance(page));
                        Detail = content;
                        IsPresented = false;

                    }
                    catch (TargetInvocationException ie)
                    {
                        Console.WriteLine("Exception: " + ie.InnerException);
                        Console.WriteLine("Exception: " + ie.InnerException.InnerException);
                        Console.WriteLine("Exception: " + ie.InnerException.InnerException.InnerException);
                    }
                    break;
            }
        }

        /// <summary>
        /// Public accessor for updating the menu bar
        /// </summary>
        public void updateMenuPhoto() {
            updateUserProfileBar();
        }

        private void updateUserViewerProfileBar()
        {
            BindingContext = new
            {
                ProfileImage = "viewing_user_photo.png",
                FullName = "Viewing User: " + UserController.Instance.LoggedInUser.FullName,
                BorderColor = "White"
            };
        }
        private void updateUserProfileBar()
        {
            // Update for a logged in user
            string profileImagePath;
            if (UserController.Instance.ProfilePhotoSource == null)
            {
                // No photo provided, use default
                profileImagePath = "default_user_photo.png";
            }
            else
            {
                // Use photo from server
                ProfilePhotoImage.Source = UserController.Instance.ProfilePhotoSource;
            }

            BindingContext = new
            {
                FullName = UserController.Instance.LoggedInUser.FullName,
                BorderColor = "White"
            };
            
        }

        private void updateClinicianProfileBar()
        {
            // Update for a logged in clinician
            BindingContext = new
            {
                ProfileImage = "default_clinician_photo.png",
                FullName = "Clinician: " + ClinicianController.Instance.LoggedInClinician.name,
                BorderColor = "White"
            };
        }

        /// <summary>
        /// Activated when the profile photo is tapped -> opens profile photo settings
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="args"></param>
        async void OnProfilePhotoTapped(object sender, EventArgs args)
        {
            // Do nothing if it is a clinician
            if (ClinicianController.Instance.isLoggedIn())
            {
                return;
            }

            await Navigation.PushModalAsync(new NavigationPage(new PhotoSettingsPage()));
        }
    }
}
 