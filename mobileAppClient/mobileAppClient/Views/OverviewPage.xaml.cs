using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class OverviewPage : ContentPage, UserObserver
    {
        public OverviewPage()
        {
            InitializeComponent();
            UserController.Instance.addUserObserver(this);
        }

        public void updateUser()
        {
            
        }
    }
}
