using mobileAppClient.odmsAPI;
using mobileAppClient.Views;
using System;
using System.Collections.ObjectModel;
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
                ClinicianController.Instance.mainPageController = this;
                LogoutUser();
            }

            // Setting our list to be ItemSource for ListView in MainPage.xaml

            // Initial navigation, this can be used for our home page

            Detail = new NavigationPage((Page)Activator.CreateInstance(typeof(UserOverviewPage)));
            this.BindingContext = new
            {
                Header = "",
                Image = "",
                Footer = "      Welcome To SENG302     "
            };

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
            await Navigation.PushModalAsync(loginPage);
        }

        /*
         * Sets up the Main page for a user's view
         */
        public void userLoggedIn()
        {
            Detail = new NavigationPage((Page)Activator.CreateInstance(typeof(UserOverviewPage)));
            this.BindingContext = new
            {
                Header = "  SENG302 - Team300",
                Image = UserController.Instance.ProfilePhotoSource,
                Footer = "  Logged in as " + UserController.Instance.LoggedInUser.name[0]
            };

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

            // Adding menu items to menuList
            menuList.Add(overviewPage);
            menuList.Add(attributesPage);
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
            this.BindingContext = new
            {
                Header = "  SENG302 - Team300",
                Footer = "  Logged in as CLINICIAN: " + ClinicianController.Instance.LoggedInClinician.name
            };

            menuList.Clear();

            var overviewPage = new MasterPageItem() { Title = "Overview", Icon = "home_icon.png", TargetType = typeof(ClinicianOverviewPage) };
            var userSearchPage = new MasterPageItem() { Title = "User Search", Icon = "users_icon.png", TargetType = typeof(UserSearchPage) };
            var attributesPage = new MasterPageItem() { Title = "Attributes", Icon = "attributes_icon.png", TargetType = typeof(AttributesPageClinician) };
            var logoutPage = new MasterPageItem() { Title = "Logout", Icon = "logout_icon.png", TargetType = typeof(LoginPage) };
            var mapPage = new MasterPageItem() { Title = "Map", TargetType = typeof(ClinicianMapPage) };

            // Adding menu items to menuList
            menuList.Add(overviewPage);
            menuList.Add(userSearchPage);
            menuList.Add(attributesPage);
            menuList.Add(mapPage);
            menuList.Add(logoutPage);
        }

        public void clinicianViewingUser()
        {
            Detail = new NavigationPage((Page)Activator.CreateInstance(typeof(UserOverviewPage)));
            BindingContext = new
            {
                Header = "  SENG302 - Team300",
                Image = UserController.Instance.ProfilePhotoSource,
                Footer = "  Viewing user " + UserController.Instance.LoggedInUser.name[0]
            };

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
         * Handles when a given page is selected in the menu slider and sends the user to that page.
         */
        private void OnMenuItemSelected(object sender, SelectedItemChangedEventArgs e)
        {
            var item = (MasterPageItem)e.SelectedItem;
            Type page = item.TargetType;

            switch(page.Name)
            {
                case "LoginPage":
                    LogoutUser();
                    break;
                default:
                    Detail = new NavigationPage((Page)Activator.CreateInstance(page));
                    IsPresented = false;
                    break;
            }
        }

        public void updateMenuPhoto() {
            this.BindingContext = new
            {
                Header = "  SENG302 - Team300",
                Image = UserController.Instance.ProfilePhotoSource,
                Footer = "  Logged in as " + UserController.Instance.LoggedInUser.name[0]
            };
        }
    }
}
 