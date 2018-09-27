using System;
namespace mobileAppClient
{
    /*
     * Defines the camera interface
     */
    public interface CameraInterface
    {
        void LaunchCamera(FileFormatEnum imageType, string imageId = null);
        void LaunchGallery(FileFormatEnum imageType, string imageId = null);
    }
}
