﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;


namespace mobileAppClient
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class MainPage : MasterDetailPage, UserObserver
    {

        public List<MasterPageItem> menuList { get; set; }


        public MainPage()
        {
            OpenLogin();
            InitializeComponent();
            UserController.Instance.addUserObserver(this);

            menuList = new List<MasterPageItem>();

            var overviewPage = new MasterPageItem() { Title = "Overview", Icon = "ic_home.png", TargetType = typeof(OverviewPage) };
            var attributesPage = new MasterPageItem() { Title = "Attributes", Icon = "ic_local_shipping.png", TargetType = typeof(AttributesPage) };
            var organsPage = new MasterPageItem() { Title = "Organs", Icon = "ic_my_location.png", TargetType = typeof(OrgansPage) };
            var loginPage = new MasterPageItem() { Title = "Logout", TargetType = typeof(LoginPage) };
            var diseasesPage = new MasterPageItem() { Title = "Diseases", TargetType = typeof(DiseasesPage) };
            var proceduresPage = new MasterPageItem() { Title = "Procedures", TargetType = typeof(ProceduresPage) };
            var waitingListItemsPage = new MasterPageItem() { Title = "Waiting List", TargetType = typeof(WaitingListItemsPage) };
            var medicationsPage = new MasterPageItem() { Title = "Medications", TargetType = typeof(MedicationsPage) };


            // Adding menu items to menuList
            menuList.Add(overviewPage);
            menuList.Add(attributesPage);
            menuList.Add(organsPage);
            menuList.Add(medicationsPage);
            menuList.Add(diseasesPage);
            menuList.Add(proceduresPage);
            menuList.Add(waitingListItemsPage);
            menuList.Add(loginPage);

            // Setting our list to be ItemSource for ListView in MainPage.xaml
            navigationDrawerList.ItemsSource = menuList;
            // Initial navigation, this can be used for our home page
            Detail = new NavigationPage((Page)Activator.CreateInstance(typeof(OverviewPage)));
            this.BindingContext = new
            {
                Header = "",
                Image = "",
                Footer = "      Welcome To SENG302     "
            };
            
        }

        private async void OpenLogin()
        {
            // Logout any currently stored user
            UserController.Instance.Logout();

            // Open the login page
            var loginPage = new LoginPage();
            await Navigation.PushModalAsync(loginPage);
        }

        public void updateUser()
        {
            this.BindingContext = new
            {
                Header = "",
                Image = "https://5.imimg.com/data5/JP/RF/MY-23184303/doctor-stethoscope-500x500.jpg",
                Footer = "Logged in as " + UserController.Instance.LoggedInUser.name[0]
            };
        }


        private void OnMenuItemSelected(object sender, SelectedItemChangedEventArgs e)
        {
            var item = (MasterPageItem)e.SelectedItem;
            Type page = item.TargetType;

            switch(page.Name)
            {
                case "LoginPage":
                    OpenLogin();
                    break;
                default:
                    updateUser();
                    Detail = new NavigationPage((Page)Activator.CreateInstance(page));
                    IsPresented = false;
                    break;
            }
        }
    }
}
 