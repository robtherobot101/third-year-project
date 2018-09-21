

using mobileAppClient.Models.CustomObjects;
using mobileAppClient.odmsAPI;
using System;
using System.Collections.Generic;
using System.Net;
using System.Threading.Tasks;
using System.Windows.Input;
using mobileAppClient.Models;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace mobileAppClient
{
    /// <summary>
    /// Clinician only page that contains a Listview of users alongside searching functionality
    /// </summary>
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class CreateConversationPage : ContentPage
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
                }
                else
                {
                    LoadingIndicator.IsVisible = false;
                    LoadingIndicator.IsRunning = false;
                }
            }
        }

        private int currentIndex;
        private bool endOfUsers;
        private string searchQuery;

        public CustomObservableCollection<User> UserList { get; set; }

        public CreateConversationPage()
        {
            InitializeComponent();
            IsLoading = false;
            endOfUsers = false;
            currentIndex = 0;
            UserList = new CustomObservableCollection<User>();
            UserListView.ItemsSource = UserList;
            UserListView.RefreshCommand = RefreshCommand;
            UserSearchBar.SearchCommand = SearchCommand;
            UserSearchBar.TextChanged += UserSearchBar_TextChanged;
            Title = "New Conversation";
            UserListView.ItemAppearing += HitBottomOfList;
        }

        private void HitBottomOfList(object sender, ItemVisibilityEventArgs e)
        {
            if (IsLoading || UserList.Count == 0 || endOfUsers)
                return;

            // Hit the bottom
            if (e.Item == UserList[UserList.Count - 1])
            {
                LoadItems();
            }
        }

        /// <summary>
        /// Activated whenever focus is on this page
        /// </summary>
        protected override async void OnAppearing()
        {
            UserList.Clear();
            currentIndex = 0;
            await LoadItems();
        }

        /// <summary>
        /// Method that is called EVERY time the text within the UserSearchBox is changed
        /// - Updates the current search query
        /// - Calls a reset when the search is cleared
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void UserSearchBar_TextChanged(object sender, TextChangedEventArgs e)
        {
            // Update the current search param
            searchQuery = InputValidation.Trim(e.NewTextValue);

            // Has input been cleared?
            if (string.IsNullOrEmpty(e.NewTextValue))
            {
                ResetItemsQuiet();
            }
        }

        /*
         * Loads items from the DB and appends them to the bottom of the list. Infinity Scroll™
         */
        private async Task LoadItems()
        {
            IsLoading = true;

            // This is where users will be populated from
            UserList.AddRange(await getUsers(currentIndex, 20));
            currentIndex += 20;

            IsLoading = false;
        }

        /*
         * Loads items from the DB and appends them to the bottom of the list. Infinity Scroll™
         * - Doesn't show an activity indicator
         */
        private async Task ResetItemsQuiet()
        {
            UserList.Clear();
            currentIndex = 0;

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
            Tuple<HttpStatusCode, List<User>> users = await userAPI.GetUsers(startIndex, count, searchQuery);
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

        /// <summary>
        /// Resets the endOfUsers flag and grabs the start of the user list from DB, called by pull to refresh
        /// </summary>
        private ICommand RefreshCommand
        {
            get
            {
                return new Command(async () =>
                {
                    UserListView.IsRefreshing = true;

                    await ResetItemsQuiet();

                    UserListView.IsRefreshing = false;
                });
            }
        }

        /// <summary>
        /// Is called when the UserSearchBox calls its search method
        /// </summary>
        private ICommand SearchCommand
        {
            get
            {
                return new Command(async () =>
                {
                    await ResetItemsQuiet();
                });
            }
        }

        /// <summary>
        /// Called when a User entry on the ListView is tapped on
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        async void Handle_UserTapped(object sender, ItemTappedEventArgs e)
        {
            if (e.Item == null)
                return;

            // Deselect Item
            ((ListView)sender).SelectedItem = null;

            User tappedUser = (User)e.Item;
            

            // Create conversation here
            
            await CreateConversation(tappedUser.id);
        }

        async Task CreateConversation(int TappedUserId)
        {
            MessagingAPI messagingApi = new MessagingAPI();
            int localClincianId = ClinicianController.Instance.LoggedInClinician.staffID;

            Tuple<HttpStatusCode, int> returnStatus = await messagingApi.CreateConversation(localClincianId,
                new List<int> {localClincianId, TappedUserId});
            switch (returnStatus.Item1)
            {
                case HttpStatusCode.BadRequest:
                    await DisplayAlert("", "Bad Request", "OK");
                    return;
                case HttpStatusCode.InternalServerError:
                    await DisplayAlert("", "Server error, please try again", "OK");
                    return;
                case HttpStatusCode.Created:
                    // JUMP INTO CONVO
                    Conversation newlyCreatedConversation = await GetNewlyCreatedConversation(returnStatus.Item2);

                    if (newlyCreatedConversation != null)
                    {
                        await Navigation.PushAsync(new ConversationPage(newlyCreatedConversation));
                    }

                    return;
            }
        }

        async Task<Conversation> GetNewlyCreatedConversation(int conversationId)
        {
            MessagingAPI messagingApi = new MessagingAPI();
            int localClincianId = ClinicianController.Instance.LoggedInClinician.staffID;

            Tuple<HttpStatusCode, Conversation> returnStatus = await messagingApi.GetConversation(localClincianId, conversationId, true);

            switch (returnStatus.Item1)
            {
                case HttpStatusCode.OK:
                    // JUMP INTO CONVO
                    return returnStatus.Item2;
                default:
                    await DisplayAlert("", "Failed to get newly created conversation (" + returnStatus.Item1 + ")", "OK");
                    return null;         
            }
        }

        // TODO
        async Task RefreshCurrentConversations()
        {
            MessagingAPI messagingApi = new MessagingAPI();
            int localClincianId = ClinicianController.Instance.LoggedInClinician.staffID;

            Tuple<HttpStatusCode, List<Conversation>> returnStatus = await messagingApi.GetConversations(localClincianId, true);

            switch (returnStatus.Item1)
            {
                case HttpStatusCode.BadRequest:
                    await DisplayAlert("", "Bad Request", "OK");
                    return;
                case HttpStatusCode.InternalServerError:
                    await DisplayAlert("", "Server error, please try again", "OK");
                    return;
                case HttpStatusCode.Created:
                    // JUMP INTO CONVO
                    return;
            }
        }
    }
}
