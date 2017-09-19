using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO.Ports;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ConsoleApplication2;
using HidLibrary;
using TestCA.VP;

namespace TestCA
{
    class Program
    {
        static HidDevice device;

        public static void Main()
        {
            device = HidDevices.Enumerate().ToArray()[0];
            device.OpenDevice();
            device.Inserted += Device_Inserted; 
            device.Removed += Device_Removed;   
            device.MonitorDeviceEvents = true;
            device.ReadReport(OnReport);

            //String senddata = SECFormat.addPollHeader(CommandSEC.QPI);
            String senddata = CommandSEC.QPI;
            byte[] bytes = Encoding.ASCII.GetBytes(senddata);

            device.Write(bytes);
            device.Write(new byte[1]{13});//cr

            device.Read(OnRead);
            /*var report = device.CreateReport();
            report.ReportId = 0;
            report.Data = Encoding.ASCII.GetBytes(addPollHeader(CommandSEC.QPI) + "\r");
            device.WriteReport(report);
            */

            Console.ReadKey();
            device.CloseDevice();
        }

        private static void OnRead(HidDeviceData data)
        {
            if (data != null)
            {
                Console.WriteLine("@Status:{0} @Data:{1}", data.Status, Encoding.ASCII.GetString(data.Data));
                if (data.Data.Last() != 13)
                {
                    device.Read(OnRead);
                }
            }
        }

        private static void OnReport(HidReport report)
        {
            if (report != null)
            {
                Console.WriteLine("@ReportId:{0} @Exists:{1} @ReadStatus:{2} @Data:{3}",
                    report.ReportId, report.Exists,
                    report.ReadStatus, Encoding.ASCII.GetString(report.Data));
            }

            device.ReadReport(OnReport);
        }

        private static void Device_Removed()
        {
            Console.WriteLine("Device_Removed");
        }

        private static void Device_Inserted()
        {
            Console.WriteLine("Device_Inserted");
        }
    }
}
