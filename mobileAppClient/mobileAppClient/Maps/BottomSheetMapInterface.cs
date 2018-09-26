using System;

namespace mobileAppClient.Maps
{
    public interface BottomSheetMapInterface
    {
        void removeBottomSheet(bool isPresented, MasterPageItem selectedMenuItem);
        void removeBottomSheetWhenViewingAUser();
    }
}
