using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SolarMonitor.Communication.Messages.Queries;


namespace SolarMonitor.Communication.Test.Messages.Queries
{
    [TestClass]
    public class QpiriMessageTest
    {
        [TestMethod]
        public void TestMethod1()
        {
            var target = new QpiriMessage();
            target.SetResponse(new List<string>() {"2365","256","2305","500","510","2563","6325"});

        }
    }
}
