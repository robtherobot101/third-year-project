using System;
using System.Linq;
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

            //---- if there are no cells to reuse, create a new one
            if (cell == null)
            { cell = new UITableViewCell(UITableViewCellStyle.Subtitle, CellIdentifier); }

            cell.TextLabel.Text = item;
            cell.BackgroundColor = UIColor.Clear;
            cell.TextLabel.TextColor = UIColor.White;
            cell.ImageView.Image = UIImage.FromFile(photoItem);
            cell.Accessory = UITableViewCellAccessory.DisclosureIndicator;
            //SET THE TEXT DETAIL TO BE THE COUNTDOWN

            return cell;
        }

        public override void RowSelected(UITableView tableView, NSIndexPath indexPath)
        {
            UIAlertController okAlertController = UIAlertController.Create("Row Selected", organs[indexPath.Row], UIAlertControllerStyle.Alert);
            okAlertController.AddAction(UIAlertAction.Create("OK", UIAlertActionStyle.Default, null));
            owner.PresentViewController(okAlertController, true, null);
            tableView.DeselectRow(indexPath, true);
        }
    }
}
