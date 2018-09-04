using System;
using System.Collections.Generic;
using Foundation;
using UIKit;

namespace mobileAppClient.iOS
{
    public class RecipientsTableSource : UITableViewSource
    {

        string CellIdentifier = "TableCell";
        PotentialMatchesBottomSheetViewController owner;
        List<string> recipients;
        int userId;


        public RecipientsTableSource(List<string> recipients, PotentialMatchesBottomSheetViewController owner)
        {
            this.owner = owner;
            this.recipients = recipients;
        }

        public override nint RowsInSection(UITableView tableview, nint section)
        {
            return recipients.Count;
        }

        public override UITableViewCell GetCell(UITableView tableView, NSIndexPath indexPath)
        {
            UITableViewCell cell = tableView.DequeueReusableCell(CellIdentifier);

            string item = recipients[indexPath.Row];
            item = char.ToUpper(item[0]) + item.Substring(1);

            //---- if there are no cells to reuse, create a new one
            if (cell == null)
            { cell = new UITableViewCell(UITableViewCellStyle.Subtitle, CellIdentifier); }

            cell.TextLabel.Text = item;
            cell.BackgroundColor = UIColor.Clear;
            cell.TextLabel.TextColor = UIColor.White;
            cell.ImageView.Image = UIImage.FromFile("donationIcon.png");
            cell.Accessory = UITableViewCellAccessory.DisclosureIndicator;
            //SET THE TEXT DETAIL TO BE THE COUNTDOWN
            cell.DetailTextLabel.Text = "INSERT ADDRESS AND REGION HERE HERE";
            //Change colour based on severity
            cell.DetailTextLabel.TextColor = UIColor.White;

            return cell;
        }

        public override void RowSelected(UITableView tableView, NSIndexPath indexPath)
        {
            UIAlertController okAlertController = UIAlertController.Create("Row Selected", recipients[indexPath.Row], UIAlertControllerStyle.Alert);
            okAlertController.AddAction(UIAlertAction.Create("OK", UIAlertActionStyle.Default, null));
            owner.PresentViewController(okAlertController, true, null);
            tableView.DeselectRow(indexPath, true);

        }

    }
}
