using System;
using CoreGraphics;
using UIKit;
using Xamarin.Forms;

[assembly: Dependency(typeof(mobileAppClient.iOS.MessagingEntryClass))]
namespace mobileAppClient.iOS
{
    public class MessagingEntryClass : CustomMessagingInterface
    {
        public MessagingEntryClass()
        {
        }

        public void CreateMessagingPage() {
            var window = UIApplication.SharedApplication.KeyWindow;
            var messagesVC = new ChatViewController();

            var rootVC = window.RootViewController;

            rootVC.AddChildViewController(messagesVC);
            rootVC.View.AddSubview(messagesVC.View);
            messagesVC.DidMoveToParentViewController(rootVC);

            var height = window.Frame.Height;
            var width = window.Frame.Width;
            messagesVC.View.Frame = new CGRect(0, window.Frame.GetMaxY(), width, height);

            //messagesVC.ModalPresentationStyle = UIModalPresentationStyle.OverCurrentContext;

            //rootVC.PresentViewController(messagesVC, true, null);

            //            let storyBoard = UIStoryboard(name: "Main", bundle: nil)
            //let mainViewController = storyBoard.instantiateViewController(withIdentifier: "Identifier")
            //self.navigationController?.pushViewController(mainViewController, animated: true)
            //var messagingVC = new ChatViewController();



            ////rootVC.ShowViewController(messagingVC, rootVC);

            //rootVC.PresentViewController(messagingVC, false, null);



        }
    }
}
