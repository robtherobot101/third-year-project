using System;
using System.Linq;
using CoreGraphics;
using Foundation;
using UIKit;

namespace mobileAppClient.iOS
{
    public class TableSource : UITableViewSource
    {


        string CellIdentifier = "TableCell";
        BottomSheetViewController owner;
        CustomPin pin;
        string[] organs;

        public TableSource(CustomPin pin, BottomSheetViewController owner)
        {
            this.pin = pin;
            this.owner = owner;
            organs = pin.Url.Split(',');
            organs = organs.Take(organs.Length - 1).ToArray();
        }

        public override nint RowsInSection(UITableView tableview, nint section)
        {
            return organs.Length;
        }

        public override UITableViewCell GetCell(UITableView tableView, NSIndexPath indexPath)
        {
            UITableViewCell cell = tableView.DequeueReusableCell(CellIdentifier);

            string photoItem = organs[indexPath.Row];
            string item = organs[indexPath.Row].Replace("_icon.png", "");
            item = char.ToUpper(item[0]) + item.Substring(1);

            //---- if there are no cells to reuse, create a new one
            if (cell == null)
            { cell = new UITableViewCell(UITableViewCellStyle.Subtitle, CellIdentifier); }

            cell.TextLabel.Text = item;
            cell.BackgroundColor = UIColor.Clear;
            cell.TextLabel.TextColor = UIColor.White;
            cell.ImageView.Image = UIImage.FromFile(photoItem);
            cell.Accessory = UITableViewCellAccessory.DisclosureIndicator;
            cell.DetailTextLabel.Text = "INSERT COUNTDOWN HERE";
            cell.DetailTextLabel.TextColor = UIColor.Red;
            //SET THE TEXT DETAIL TO BE THE COUNTDOWN

            return cell;
        }

        public override void RowSelected(UITableView tableView, NSIndexPath indexPath)
        {
            //Do API call to get all recipients

            var potentialRecipientsController = new PotentialMatchesBottomSheetViewController(pin);
            
            var window = UIApplication.SharedApplication.KeyWindow;

            var rootVC = window.RootViewController;

            owner.View.RemoveFromSuperview();

            rootVC.AddChildViewController(potentialRecipientsController);
            rootVC.View.AddSubview(potentialRecipientsController.View);
            potentialRecipientsController.DidMoveToParentViewController(rootVC);

            var height = window.Frame.Height;
            var width = window.Frame.Width;
            potentialRecipientsController.View.Frame = new CGRect(0, window.Frame.GetMaxY(), width, height);




        }
    }
}
