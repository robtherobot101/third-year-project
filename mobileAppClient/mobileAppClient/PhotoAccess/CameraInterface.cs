using System;
namespace mobileAppClient
{
    public interface CameraInterface
    {
        void LaunchCamera(FileFormatEnum imageType, string imageId = null);
        void LaunchGallery(FileFormatEnum imageType, string imageId = null);
    }
}
