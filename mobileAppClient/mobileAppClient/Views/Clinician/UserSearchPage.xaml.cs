using mobileAppClient.Models.CustomObjects;
using mobileAppClient.odmsAPI;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Linq;
using System.Net;
using System.Threading.Tasks;
using System.Windows.Input;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace mobileAppClient.Views
{

    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class UserSearchPage : ContentPage
    {
        // Loading represents fetching more users at the bottom of the list
        private bool _IsLoading;
        public bool IsLoading
        {
            get { return _IsLoading; }
            set
            {
                _IsLoading = value;
                if (_IsLoading == true)
                {
                    LoadingIndicator.IsVisible = true;
                    LoadingIndicator.IsRunning = true;
                } else
                {
                    LoadingIndicator.IsVisible = false;
                    LoadingIndicator.IsRunning = false;
                }
            }
        }
        
        private int currentIndex;
        private bool endOfUsers;
        
        public CustomObservableCollection<User> UserList { get; set; }

        public UserSearchPage()
        {
            InitializeComponent();
            IsLoading = false;
            endOfUsers = false;
            currentIndex = 0;
            UserList = new CustomObservableCollection<User>();
            UserListView.ItemsSource = UserList;
            UserListView.RefreshCommand = RefreshCommand;

            UserListView.ItemAppearing += (sender, e) =>
            {
                if (IsLoading || UserList.Count == 0 || endOfUsers)
                    return;

                // Hit the bottom
                if (e.Item == UserList[UserList.Count - 1])
                {
                    LoadItems();
                }
            };

            LoadItems();
        }

        /*
         * Loads items from the DB and appends them to the bottom of the list. Infinity Scroll™
         */
        private async void LoadItems()
        {
            IsLoading = true;

            // This is where users will be populated from
            UserList.AddRange(await getUsers(currentIndex, 20));
            currentIndex += 20;

            IsLoading = false; 
        }

        /*
         * Loads items from the DB and appends them to the bottom of the list. Infinity Scroll™
         * -Doesn't show an activity indicator
         */
        private async void LoadItemsQuiet()
        { 
            // This is where users will be populated from
            UserList.AddRange(await getUsers(currentIndex, 20));
            currentIndex += 20;
        }

        /*
         * Fetches the users and checks if all users have been taken from DB
         */
        private async Task<List<User>> getUsers(int startIndex, int count)
        {
            UserAPI userAPI = new UserAPI();
            Tuple<HttpStatusCode, List<User>> users = await userAPI.GetUsers(startIndex, count);
            if (users.Item1 != HttpStatusCode.OK)
            {
                return new List<User>();
            } 

            if (users.Item2.Count < 20)
            {
                endOfUsers = true;
            }
            return users.Item2;
        }

        /*
         * Resets the endOfUsers flag and grabs the start of the user list from DB, called by pull to refresh
         */
        public ICommand RefreshCommand
        {
            get
            {
                return new Command(() =>
                {
                    UserListView.IsRefreshing = true;

                    UserList.Clear();
                    currentIndex = 0;
                    LoadItemsQuiet();

                    UserListView.IsRefreshing = false;
                });
            }
        }

        async void Handle_UserTapped(object sender, ItemTappedEventArgs e)
        {
            if (e.Item == null)
                return;

            // Deselect Item
            ((ListView)sender).SelectedItem = null;

            User tappedUser = (User) e.Item;
            UserController.Instance.LoggedInUser = tappedUser;

            MainPage mainPage = new MainPage(true);
            mainPage.Title = String.Format("User Viewer: {1}, {0}", tappedUser.name[0], tappedUser.name[2]);
            
            await Navigation.PushAsync(mainPage);
        }
    }
}
