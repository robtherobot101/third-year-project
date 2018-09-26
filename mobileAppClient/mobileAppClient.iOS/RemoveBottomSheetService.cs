using System;
using mobileAppClient.iOS;
using mobileAppClient.Maps;
using mobileAppClient.Views.Clinician;
using UIKit;

[assembly: Xamarin.Forms.Dependency(typeof(RemoveBottomSheetService))]

namespace mobileAppClient.iOS
{
    public class RemoveBottomSheetService : BottomSheetMapInterface
    {

        BottomSheetViewController currentBottomSheet;
        PotentialMatchesBottomSheetViewController currentPotentialMatchesBottomSheet;

        public RemoveBottomSheetService()
        {
        }

        public async void removeBottomSheetWhenViewingAUser()
        {
            //Get rid of bottom sheet if its there
            var window = UIApplication.SharedApplication.KeyWindow;
            var rootVC = window.RootViewController;
            if (rootVC is BottomSheetViewController)
            {
                BottomSheetViewController bottomSheet = (BottomSheetViewController)rootVC;
                await bottomSheet.slideMenuToLeft();
                bottomSheet.StopTimers();
                bottomSheet.Dispose();
                bottomSheet = null;
                window.RootViewController = rootVC.ChildViewControllers[0];

            }
            else
            {
                PotentialMatchesBottomSheetViewController matchesSheet = (PotentialMatchesBottomSheetViewController)rootVC;
                await matchesSheet.slideMenuToLeft();
                matchesSheet.StopTimers();
                matchesSheet.Dispose();
                matchesSheet = null;
                window.RootViewController = rootVC.ChildViewControllers[0];
            }
        }

        public async void removeBottomSheet(bool isPresented, MasterPageItem selectedMenuItem)
        {
            UIApplication.SharedApplication.InvokeOnMainThread(async () =>
            {
                var window = UIApplication.SharedApplication.KeyWindow;
                var rootVC = window.RootViewController;

                if (!isPresented)
                {

                    if (selectedMenuItem.TargetType != typeof(ClinicianMapPage))
                    {
                        if (rootVC is BottomSheetViewController)
                        {
                            currentBottomSheet.StopTimers();
                            currentBottomSheet.Dispose();
                            currentBottomSheet = null;
                            window.RootViewController = rootVC.ChildViewControllers[0];

                        }
                        else if (rootVC is PotentialMatchesBottomSheetViewController)
                        {
                            currentPotentialMatchesBottomSheet.StopTimers();
                            currentPotentialMatchesBottomSheet.Dispose();
                            currentPotentialMatchesBottomSheet = null;
                            window.RootViewController = rootVC.ChildViewControllers[0];
                        }
                        else
                        {
                            return;
                        }
                    }
                    else
                    {
                        if (currentBottomSheet != null)
                        {
                            window.RootViewController = currentBottomSheet;
                            await currentBottomSheet.slideMenuBackInFromRight();

                        }
                        else if (currentPotentialMatchesBottomSheet != null)
                        {
                            window.RootViewController = currentPotentialMatchesBottomSheet;
                            await currentPotentialMatchesBottomSheet.slideMenuBackInFromRight();

                        }
                        else
                        {
                            return;
                        }
                    }



                }
                else
                {
                    if (rootVC is BottomSheetViewController)
                    {
                        BottomSheetViewController bottomSheet = (BottomSheetViewController)rootVC;
                        currentBottomSheet = bottomSheet;
                        await bottomSheet.slideMenuToRight();


                    }
                    else if (rootVC is PotentialMatchesBottomSheetViewController)
                    {
                        PotentialMatchesBottomSheetViewController matchesSheet = (PotentialMatchesBottomSheetViewController)rootVC;
                        currentPotentialMatchesBottomSheet = matchesSheet;
                        await matchesSheet.slideMenuToRight();

                    }
                    else
                    {

                        //if (currentBottomSheet != null)
                        //{
                        //    currentBottomSheet.StopTimers();
                        //    currentBottomSheet.Dispose();
                        //    currentBottomSheet = null;
                        //    window.RootViewController = rootVC.ChildViewControllers[0];
                        //}
                        //else if (currentPotentialMatchesBottomSheet != null)
                        //{
                        //    currentPotentialMatchesBottomSheet.StopTimers();
                        //    currentPotentialMatchesBottomSheet.Dispose();
                        //    currentPotentialMatchesBottomSheet = null;
                        //    window.RootViewController = rootVC.ChildViewControllers[0];
                        //}
                        //else
                        //{
                        return;
                        //}
                    }



                }

            });
        }
    }
}
