using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsoleApplication2
{
    public static class SECFormat
    {
        public static String addPollHeader(string senddata, int crcLength)
        {
            int len = senddata.Length;
            len += crcLength;
            return "^P" + string.Format("{0:000}", len + 1) + senddata;
        }
    }

}
