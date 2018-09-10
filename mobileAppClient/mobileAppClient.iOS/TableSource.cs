using System;
using System.Collections.Generic;
using System.Linq;
using CoreGraphics;
using Foundation;
using MapKit;
using UIKit;

namespace mobileAppClient.iOS
{
    public class TableSource : UITableViewSource
    {
        string CellIdentifier = "TableCell";
        BottomSheetViewController owner;
        CustomPin pin;
        string[] organs;
        int userId;
        CustomMap map;
        MKMapView nativeMap;

        public TableSource(CustomPin pin, CustomMap map, MKMapView nativeMap, BottomSheetViewController owner)
        {
            this.nativeMap = nativeMap;
            this.map = map;
            this.pin = pin;
            this.owner = owner;
            organs = pin.Url.Split(',');
            userId = Int32.Parse(organs[organs.Length - 1]);
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
            //SET THE TEXT DETAIL TO BE THE COUNTDOWN
            cell.DetailTextLabel.Text = "INSERT COUNTDOWN HERE";
            //Change colour based on severity
            cell.DetailTextLabel.TextColor = UIColor.Red;

            return cell;
        }

        public void removeOverlays()
        {
            if (nativeMap.Overlays != null && nativeMap.Overlays.Length > 0)
            {
                nativeMap.Overlays[0].Dispose();
                nativeMap.RemoveOverlay(nativeMap.Overlays[0]);

            }

        }

        public override void RowSelected(UITableView tableView, NSIndexPath indexPath)
        {
            removeOverlays();

            string organ = organs[indexPath.Row].Replace("_icon.png", "");

            var potentialRecipientsController = new PotentialMatchesBottomSheetViewController(pin, map, nativeMap, organ);
            
            var window = UIApplication.SharedApplication.KeyWindow;

            var rootVC = window.RootViewController;

            owner.View.RemoveFromSuperview();
            owner.View.Dispose();
            owner.View = null;
            owner.RemoveFromParentViewController();

            rootVC.AddChildViewController(potentialRecipientsController);
            rootVC.View.AddSubview(potentialRecipientsController.View);
            potentialRecipientsController.DidMoveToParentViewController(rootVC);

            var height = window.Frame.Height;
            var width = window.Frame.Width;
            potentialRecipientsController.View.Frame = new CGRect(0, window.Frame.GetMaxY(), width, height);

        }
    }
}
