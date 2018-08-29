using System;
namespace mobileAppClient
{
    /*
     * Class to handle all menu items in the slider menu 
     * with title, icon and target type of the page one wishes to visit
     */
    public class MasterPageItem
    {
        public string Title { get; set; }

        public string Icon { get; set; }

        public Type TargetType { get; set; }
    }
}
