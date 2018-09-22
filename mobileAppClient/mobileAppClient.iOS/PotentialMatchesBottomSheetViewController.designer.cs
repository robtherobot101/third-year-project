// WARNING
//
// This file has been generated automatically by Visual Studio from the outlets and
// actions declared in your storyboard file.
// Manual changes to this file will not be maintained.
//
using Foundation;
using System;
using System.CodeDom.Compiler;
using UIKit;

namespace mobileAppClient.iOS
{
    [Register ("PotentialMatchesBottomSheetViewController")]
    partial class PotentialMatchesBottomSheetViewController
    {
        [Outlet]
        [GeneratedCode ("iOS Designer", "1.0")]
        UIKit.UIButton backButton { get; set; }

        [Outlet]
        [GeneratedCode ("iOS Designer", "1.0")]
        UIKit.UIView holdView { get; set; }

        [Outlet]
        [GeneratedCode ("iOS Designer", "1.0")]
        UIKit.UIImageView organImageView { get; set; }

        [Outlet]
        [GeneratedCode ("iOS Designer", "1.0")]
        UIKit.UILabel organNameLabel { get; set; }

        [Outlet]
        [GeneratedCode ("iOS Designer", "1.0")]
        UIKit.UITableView potentialRecipientsTableView { get; set; }

        [Outlet]
        [GeneratedCode ("iOS Designer", "1.0")]
        UIKit.UILabel timeRemainingLabel { get; set; }

        void ReleaseDesignerOutlets ()
        {
            if (backButton != null) {
                backButton.Dispose ();
                backButton = null;
            }

            if (holdView != null) {
                holdView.Dispose ();
                holdView = null;
            }

            if (organImageView != null) {
                organImageView.Dispose ();
                organImageView = null;
            }

            if (organNameLabel != null) {
                organNameLabel.Dispose ();
                organNameLabel = null;
            }

            if (potentialRecipientsTableView != null) {
                potentialRecipientsTableView.Dispose ();
                potentialRecipientsTableView = null;
            }

            if (timeRemainingLabel != null) {
                timeRemainingLabel.Dispose ();
                timeRemainingLabel = null;
            }
        }
    }
}