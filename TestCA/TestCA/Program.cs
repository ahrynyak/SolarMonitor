using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO.Ports;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using HidLibrary;

namespace TestCA
{
    class Program
    {
        static HidDevice device;

        public static void Main()
        {

            foreach (var hidDevice in HidDevices.Enumerate())
            {
                Console.WriteLine(hidDevice.ToString());
            }

            device = HidDevices.Enumerate().ToArray()[4];
            device.OpenDevice();
            device.Inserted += Device_Inserted; 
            device.Removed += Device_Removed;   
            device.MonitorDeviceEvents = true;
            device.ReadReport(OnReport);

            var report = device.CreateReport();
            report.ReportId = 0x4;
            //http://networkupstools.org/protocols/us9003.html#_communication_examples
            //^P003ST2 - ^D011,1,500,2350
            //^P003MAN - ^D014PK ELECTRONICS
            report.Data = Encoding.ASCII.GetBytes("QPIGS");
            device.WriteReport(report);


            Console.ReadKey();
            device.CloseDevice();
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
