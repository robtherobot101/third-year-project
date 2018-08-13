using Microsoft.Toolkit.Parsers.Rss;
using mobileAppClient.Models.CustomObjects;
using mobileAppClient.odmsAPI;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;

namespace mobileAppClient
{
    class NewsFeed
    {
        public ObservableCollection<RssSchema> rss { get; } = new ObservableCollection<RssSchema>();
        private RssParser rssParser = new RssParser();

        private string[] sources = {"http://www.adhb.health.nz/about-us/news-and-publications/latest-stories/atom",
                "http://www.tdh.org.nz/news-and-media/news/rss",
            "https://waikatodhbnewsroom.co.nz/feed/",
            "http://www.cdhb.health.nz/News/Media-Releases/_layouts/15/listfeed.aspx?List=%7B55500942%2D4963%2D4321%2DA595%2DB13F9CE967BC%7D&Source=http%3A%2F%2Fwww%2Ecdhb%2Ehealth%2Enz%2FNews%2FMedia%2DReleases%2FPages%2FForms%2FAllItems%2Easpx",
            "https://www.nmdhb.govt.nz/nmdhb-news-and-notices/rss"
            };

        public NewsFeed()
        {
            fillFeed();
        }

        /**
         * Fill the image carousel with images and captions
         */
        private async void fillFeed()
        {
            // For each source
            foreach (var source in sources)
            {
                string rssString = await ServerConfig.Instance.client.GetStringAsync(source);
                foreach (var element in rssParser.Parse(rssString))
                {
                    // If the item does not include an image, use a default one
                    if (element.ImageUrl == null)
                    {
                        element.ImageUrl = "http://csse-s302g3.canterbury.ac.nz/donationIcon.png";
                    }
                    rss.Add(element);
                }
            }
            rss.OrderByDescending(r => r.PublishDate);
        }
    }
}
