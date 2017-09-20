using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using ConsoleApplication2;
using ConsoleApplication2.VP;
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

            Console.ReadKey();
        }

        static List<byte> GetCommandBytes(string command)
        {
            List<byte> result = new List<byte>();
            var header = SECFormat.addPollHeader(command, 2);
            var headerBytes = Encoding.ASCII.GetBytes(header);
            var crc = CRC16.getCRCByte(header);
            result.AddRange(headerBytes);
            result.AddRange(crc);
            result.Add(13);
            return result;
        }

        private static void Device_Removed()
        {
            Console.WriteLine("Device_Removed");
        }

        private static void Device_Inserted()
        {
            Console.WriteLine("Device_Inserted");
            var cmd = GetCommandBytes(CommandSEC.QPIRI);
            foreach (var message in Split(cmd, 8))
            {
                var report = device.CreateReport();
                report.ReportId = 0;
                report.Data = message.ToArray();
                device.WriteReport(report);
            }
            device.ReadReport(OnReport);
        }

        public static List<List<T>> Split<T>(List<T> source, int chunkSize)
        {
            return source.Select((x, i) => new {Index = i, Value = x}).
                GroupBy(x => x.Index/chunkSize).
                Select(x => x.Select(v => v.Value).ToList()).ToList();
        }

        private static void OnReport(HidReport report)
        {
            if (report != null)
            {
                Console.WriteLine("@ReportId:{0} @Exists:{1} @ReadStatus:{2} @Data:{3}",
                    report.ReportId, report.Exists,
                    report.ReadStatus, Encoding.ASCII.GetString(report.Data));
                //string r = "";
                //foreach (var item in report.Data)
                //{
                //    r += item;
                //}
                //File.AppendAllText(@"d:\2b.txt", r);
            }

            device.ReadReport(OnReport);
        }
    }
}
