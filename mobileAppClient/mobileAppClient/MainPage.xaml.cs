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

            var overviewPage = new MasterPageItem() { Title = "Overview", Icon = "home_icon.png", TargetType = typeof(OverviewPage) };
            var attributesPage = new MasterPageItem() { Title = "Attributes", Icon = "attributes_icon.png", TargetType = typeof(AttributesPage) };
            var organsPage = new MasterPageItem() { Title = "Organs", Icon = "organs_icon.png", TargetType = typeof(OrgansPage) };
            var logoutPage = new MasterPageItem() { Title = "Logout", Icon = "logout_icon.png" ,TargetType = typeof(LoginPage) };
            var diseasesPage = new MasterPageItem() { Title = "Diseases", Icon = "diseases_icon.png",TargetType = typeof(DiseasesPage) };
            var proceduresPage = new MasterPageItem() { Title = "Procedures", Icon = "procedures_icon.png", TargetType = typeof(ProceduresPage) };
            var waitingListItemsPage = new MasterPageItem() { Title = "Waiting List", Icon = "waitinglist_icon.png",TargetType = typeof(WaitingListItemsPage) };
            var medicationsPage = new MasterPageItem() { Title = "Medications", Icon = "medications_icon.png",TargetType = typeof(MedicationsPage) };


            // Adding menu items to menuList
            menuList.Add(overviewPage);
            menuList.Add(attributesPage);
            menuList.Add(organsPage);
            menuList.Add(medicationsPage);
            menuList.Add(diseasesPage);
            menuList.Add(proceduresPage);
            menuList.Add(waitingListItemsPage);
            menuList.Add(logoutPage);

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
                Header = "SENG302 - Team300 - ODMS",
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
 