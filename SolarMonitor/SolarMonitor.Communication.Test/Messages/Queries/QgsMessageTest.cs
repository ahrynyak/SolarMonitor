﻿using System;
using System.Linq;
using System.Net.Http;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Newtonsoft.Json;
using SolarMonitor.Communication.Messages.Queries;
using SolarMonitor.Communication.ThingSpeak;

namespace SolarMonitor.Communication.Test.Messages.Queries
{
    [TestClass]
    public class QgsMessageTest
    {
        //^D1060000,000,2298,500,0022,0009,000,517,517,000,000,000,095,020,022,000,0000,0000,0546,0000,0,2,0,1,2,2,0,0Q%
        [TestMethod]
        public void SetResponseTest()
        {
            QgsMessage target=new QgsMessage();
            target.SetResponse("0000,000,2298,500,0022,0009,000,517,517,000,000,000,095,020,022,000,0000,0000,0546,0000,0,2,0,1,2,2,0,0".Split(',').ToList());
        }
    }
}
