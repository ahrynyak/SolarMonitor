using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace SolarMonitor.Communication.ThingSpeak
{
    public class ThingSpeakClient
    {
        private readonly int channelId;
        private readonly string apiKey;
        
        public ThingSpeakClient(int channelId, string apiKey)
        {
            this.channelId = channelId;
            this.apiKey = apiKey;
        }

        public string GetLastEntryInFieldFeed(int fieldId)
        {
            var requestUri = string.Format("https://api.thingspeak.com/channels/{0}/fields/{1}/last.json?api_key={2}",
                channelId, fieldId, apiKey);
            HttpClient httpClient = new HttpClient();
            var task = httpClient.GetStringAsync(requestUri);
            task.Wait();
            return task.Result;
        }


    }
}
