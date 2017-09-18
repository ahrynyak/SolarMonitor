//using System;
//using System.Collections.Generic;
//using System.Linq;
//using System.Text;
//using System.Threading.Tasks;

//namespace ConsoleApplication2
//{
//    public class SECFormat
//    {
//        public SECFormat() { }

//        public static String getNodeData(String receiveData)
//        {
//            String subdata = "";
//            int length = 0;
//            if ((CRCUtil.checkCRC(receiveData)) &&
//              (receiveData.substring(0, 1).equals("^")))
//            {
//                if (receiveData.substring(1, 2).equals("D"))
//                {
//                    length = cn.com.voltronic.solar.util.VolUtil.parseInt(receiveData.substring(2, 5)) - 1;
//                    if (length > 2)
//                    {
//                        subdata = receiveData.substring(5);
//                        return subdata.substring(0, subdata.length() - 2);
//                    }
//                }
//                else
//                {
//                    if (receiveData.substring(1, 2).equals("1"))
//                        return "(ACK";
//                    if (receiveData.substring(1, 2).equals("0"))
//                    {
//                        return "(NAK";
//                    }
//                }
//            }
//            return subdata;
//        }

//        public static String addSetHeader(String senddata)
//        {
//            int len = senddata.length();
//            return "^S" + String.format("%03d", new Object[] { Integer.valueOf(len + 1) }) + senddata;
//        }

//        public static String addSetHeader(String senddata, int crcLength)
//        {
//            int len = senddata.length();
//            len += crcLength;
//            return "^S" + String.format("%03d", new Object[] { Integer.valueOf(len + 1) }) + senddata;
//        }

//        public static String addPollHeader(String senddata)
//        {
//            int len = senddata.length();
//            return "^P" + String.format("%03d", new Object[] { Integer.valueOf(len + 1) }) + senddata;
//        }

//        public static String addPollHeader(String senddata, int crcLength)
//        {
//            int len = senddata.length();
//            len += crcLength;
//            return "^P" + String.format("%03d", new Object[] { Integer.valueOf(len + 1) }) + senddata;
//        }
//    }
//}
