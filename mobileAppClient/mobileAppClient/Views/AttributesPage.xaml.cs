using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;


namespace mobileAppClient
{
    public partial class AttributesPage : ContentPage, UserObserver
    {

        public AttributesPage()
        {
            InitializeComponent();
            UserController.Instance.addUserObserver(this);
        }

        public void updateUser()
        {
            fillFields();
        }

        private void fillFields()
        {
          
        }
    }
}
