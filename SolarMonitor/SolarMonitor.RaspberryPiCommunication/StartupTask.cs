using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Http;
using System.Numerics;
using System.Threading.Tasks;
using Windows.ApplicationModel.Background;
using Windows.Devices.Enumeration;
using Windows.Devices.HumanInterfaceDevice;
using Windows.Storage;
using Windows.Storage.Streams;
using Buffer = Windows.Storage.Streams.Buffer;
using Windows.Foundation.Collections;

// The Background Application template is documented at http://go.microsoft.com/fwlink/?LinkID=533884&clcid=0x409

namespace SolarMonitor.RaspberryPiCommunication
{
    public sealed class StartupTask : IBackgroundTask
    {
        private static HidDevice device;

        public async void Run(IBackgroundTaskInstance taskInstance)
        {

            var task = GetHidDevices();
            task.Wait();
            device = task.Result;
            if (device != null)
            {
                var cmd = GetCommandBytes("PIRI");
                int requestCount = (cmd.Length / 8)
                //foreach (var message in Split(cmd, 8))
                //{
                //    DataWriter dataWriter = new DataWriter();
                //    //dataWriter.WriteBytes(message.ToArray()));

                //    var report = device.CreateOutputReport();
                //    report.Data = dataWriter.DetachBuffer();

                //    await device.SendOutputReportAsync(report);
                //}

                HidInputReport inReport = await device.GetInputReportAsync();

                if (inReport != null)
                {
                    UInt16 id = inReport.Id;
                    var bytes = new byte[4];
                    DataReader dataReader = DataReader.FromBuffer(inReport.Data);
                    dataReader.ReadBytes(bytes);

                    //Encoding.ASCII.GetString(report.Data);
                }
            }
        }

        // Enumerate HID devices
        private async Task<HidDevice> GetHidDevices()
        {
            ushort vendorId = 0x045E;
            ushort productId = 0x078F;
            ushort usagePage = 0xFF00;
            ushort usageId = 0x0001;

            // Create a selector that gets a HID device using VID/PID and a 
            // VendorDefined usage
            string selector = HidDevice.GetDeviceSelector(usagePage, usageId, vendorId, productId);

            // Enumerate devices using the selector
            var devices = await DeviceInformation.FindAllAsync(selector);

            if (devices.Count > 0)
            {
                // Open the target HID device
                return await HidDevice.FromIdAsync(devices.ElementAt(0).Id,
                    FileAccessMode.ReadWrite);

                // At this point the device is available to communicate with
                // So we can send/receive HID reports from it or 
                // query it for control descriptions
            }
            return null;
        }


        static byte[] GetCommandBytes(string command)
        {
            var header = addPollHeader(command, 2);
            var headerBytes = Encoding.ASCII.GetBytes(header);
            var crc = CRC16.getCRCByte(header);

            byte[] result = new byte[headerBytes.Length + crc.Length + 1];

            headerBytes.CopyTo(result,0);
            crc.CopyTo(result, headerBytes.Length);
            result[result.Length - 1] = 13;
            return result;
        }

        public static string addPollHeader(string senddata, int crcLength)
        {
            int len = senddata.Length;
            len += crcLength;
            return "^P" + string.Format("{0:000}", len + 1) + senddata;
        }

        //public static Vector<Vector<byte>> Split(Vector<byte> source, int chunkSize)
        //{
        //    return null;/*source.Select((x, i) => new { Index = i, Value = x }).
        //        GroupBy(x => x.Index / chunkSize).
        //        Select(x => x.Select(v => v.Value).ToList()).ToList()*/
        //    ;
        //}
    }
}
