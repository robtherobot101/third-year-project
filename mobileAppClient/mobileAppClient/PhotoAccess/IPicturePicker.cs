using System;
using System.IO;
using System.Threading.Tasks;

namespace mobileAppClient.PhotoAccess
{
    public interface IPicturePicker
    {
        Task<Stream> GetImageStreamAsync();
    }
}
