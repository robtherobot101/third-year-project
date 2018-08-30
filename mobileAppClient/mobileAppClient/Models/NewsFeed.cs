using Microsoft.Toolkit.Parsers.Rss;
using mobileAppClient.Models.CustomObjects;
using mobileAppClient.odmsAPI;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Net.Http;
using System.Text;

namespace mobileAppClient
{
    class NewsFeed
    {
        public ObservableCollection<RssSchema> rss { get; } = new ObservableCollection<RssSchema>();
        private RssParser rssParser = new RssParser();

        /**
         * Empty constructor for NewsFeed, used for clinicians
         **/
        public NewsFeed()
        {
            string[] p =
            {
                "http://www.adhb.health.nz/about-us/news-and-publications/latest-stories/atom",
                "http://www.tdh.org.nz/news-and-media/news/rss",
                "https://waikatodhbnewsroom.co.nz/feed/",
                "http://www.cdhb.health.nz/News/Media-Releases/_layouts/15/listfeed.aspx?List=%7B55500942%2D4963%2D4321%2DA595%2DB13F9CE967BC%7D&Source=http%3A%2F%2Fwww%2Ecdhb%2Ehealth%2Enz%2FNews%2FMedia%2DReleases%2FPages%2FForms%2FAllItems%2Easpx",
                "https://www.nmdhb.govt.nz/nmdhb-news-and-notices/rss"
            };
            fillFeed(p);
        }

        public NewsFeed(string region)
        {
            switch (region)
            {
                case ("Bay of Plenty"):
                    break;
                case ("Waikato"):
                    fillFeed(new string[] { "https://waikatodhbnewsroom.co.nz/feed/" });
                    break;
                case ("Auckland"):
                    fillFeed(new string[] { "http://www.adhb.health.nz/about-us/news-and-publications/latest-stories/atom" });
                    break;
                case ("Northland"):
                    break;
                default:
                    fillFeed(new string[] { });
                    break;
            }
        }

        private async void getFeed(String feedUrl)
        {
            string rssString = null;
            try
            {
                rssString = await ServerConfig.Instance.client.GetStringAsync(feedUrl);
            }
            catch (HttpRequestException e)
            {
                Console.WriteLine("RSS FEED ERROR: " + e.InnerException.Message);
                return;
            }
            
            foreach (RssSchema element in rssParser.Parse(rssString))
            {
                // If the item does not include an image, use a default one
                if (element.ImageUrl == null)
                {
                    element.ImageUrl = "http://csse-s302g3.canterbury.ac.nz/donationIcon.png";
                }
                rss.Add(element);
            }
        }

        /**
         * Fill the image carousel with images and captions
         */
        private async void fillFeed(IEnumerable<string> feeds)
        {
            if (!await ServerConfig.Instance.IsConnectedToInternet())
            {
                return;
            }
            else
            {
                getFeed("https://www.stuff.co.nz/rss/national/health");
                // For each source
                foreach (String feed in feeds)
                {
                    getFeed(feed);
                }
                rss.OrderByDescending(r => r.PublishDate);
            }

        }
    }
}
