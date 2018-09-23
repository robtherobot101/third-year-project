using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Threading.Tasks;
using Foundation;
using mobileAppClient.odmsAPI;
using UIKit;

namespace mobileAppClient.iOS
{
    public class RecipientsTableSource : UITableViewSource
    {

        string CellIdentifier = "TableCell";
        PotentialMatchesBottomSheetViewController owner;
        List<User> recipients;
        Dictionary<int, UIImage> UserPhotos;
        DonatableOrgan currentOrgan;


        public RecipientsTableSource(PotentialMatchesBottomSheetViewController owner 
                                     , DonatableOrgan currentOrgan)
        {
            this.owner = owner;
            this.UserPhotos = new Dictionary<int, UIImage>();
            this.recipients = currentOrgan.topReceivers;
            this.currentOrgan = currentOrgan;
        }

        public RecipientsTableSource() {

        }


        public override nint RowsInSection(UITableView tableview, nint section)
        {
            return recipients.Count;
        }

        public async Task GetAllUserPhotos()
        {

            UserAPI userAPI = new UserAPI();
            string profilePhoto = "";
            UIImage Image;

            foreach (User user in recipients)
            {

                Tuple<HttpStatusCode, string> photoResponse = await userAPI.GetUserPhotoForMapObjects(user.id);
                if (photoResponse.Item1 != HttpStatusCode.OK)
                {
                    Console.WriteLine("Failed to retrieve profile photo or no profile photo found.");
                    Image = UIImage.FromFile("donationIcon.png");
                }
                else
                {
                    Console.WriteLine("Successfully retrieved user photo.");
                    profilePhoto = photoResponse.Item2;
                    var imageBytes = Convert.FromBase64String(profilePhoto);
                    var imageData = NSData.FromArray(imageBytes);
                    Image = UIImage.LoadFromData(imageData);

                }
                UserPhotos.Add(user.id, Image);
            }

        }


        public override UITableViewCell GetCell(UITableView tableView, NSIndexPath indexPath)
        {
            UITableViewCell cell = tableView.DequeueReusableCell(CellIdentifier);

            User item = recipients[indexPath.Row];

            //---- if there are no cells to reuse, create a new one
            if (cell == null)
            { cell = new UITableViewCell(UITableViewCellStyle.Subtitle, CellIdentifier); }

            cell.TextLabel.Text = item.FullName;
            cell.BackgroundColor = UIColor.Clear;
            cell.TextLabel.TextColor = UIColor.White;
            //GET USER ICON HERE

            cell.ImageView.Image = UIImage.FromFile("donationIcon.png");

            cell.Accessory = UITableViewCellAccessory.DisclosureIndicator;
            //SET THE TEXT DETAIL TO BE THE COUNTDOWN

            string bloodTypeString = BloodTypeExtensions.ToString(BloodTypeExtensions.ToBloodTypeJSON(item.bloodType));
            string genderString = char.ToUpper(item.gender[0]) + item.gender.Substring(1).ToLower();

            cell.DetailTextLabel.Text = "Blood Type: " + bloodTypeString + "  Gender: " + genderString + "  Age: " + item?.Age;
            //Change colour based on severity
            cell.DetailTextLabel.TextColor = UIColor.White;

            return cell;
        }

        public override void RowSelected(UITableView tableView, NSIndexPath indexPath)
        {
            UIAlertController okAlertController = UIAlertController.Create("Begin transfer process?", 
                                                                           "Would you like to transfer " + currentOrgan.organType + " to " + recipients[indexPath.Row].FullName + "?", 
                                                                           UIAlertControllerStyle.Alert);
            okAlertController.AddAction(UIAlertAction.Create("Yes", UIAlertActionStyle.Default, BeginTransferProcess));
            okAlertController.AddAction(UIAlertAction.Create("No", UIAlertActionStyle.Cancel, null));
            owner.PresentViewController(okAlertController, true, null);
            tableView.DeselectRow(indexPath, true);

        }

        void BeginTransferProcess(UIAlertAction obj)
        {
            Console.WriteLine("LET US BEGIN.");
        }

    }
}
