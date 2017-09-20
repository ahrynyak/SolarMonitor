using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SolarMonitor.Communication.Messages.Queries
{
    public class QgsMessage : MessageBase
    {
        public override string Command => "GS";

        [NumericField(0, 1)]
        public decimal GridVoltage { get; set; }
        [NumericField(1, 1)]
        public decimal GridFrequency { get; set; }
        [NumericField(2, 1)]
        public decimal AcOutputVoltage { get; set; }
        [NumericField(3, 1)]
        public decimal AcOutputFrequency { get; set; }
        [NumericField(4)]
        public int AcOutputApperentPower { get; set; }
        [NumericField(5)]
        public int AcOutputActivePower { get; set; }
        [NumericField(6)]
        public int OutputLoadPercent { get; set; }
        [NumericField(7, 1)]
        public decimal BatteryVoltage { get; set; }
        [NumericField(8, 1)]
        public decimal BatteryVoltageFromSCC1 { get; set; }
        [NumericField(9, 1)]
        public decimal BatteryVoltageFromSCC2 { get; set; }
        [NumericField(10, 0)]
        public decimal DisChargingCurrent { get; set; }
        [NumericField(11, 0)]
        public decimal ChargingCurrent { get; set; }
        [NumericField(12)]
        public int BatteryCapacity { get; set; }
        [NumericField(13)]
        public int HeatSinkTemperature { get; set; }
        [NumericField(14)]
        public int MpptChargerTemperature1 { get; set; }
        [NumericField(15)]
        public int MpptChargerTemperature2 { get; set; }
        [NumericField(16)]
        public int PvInputPower1 { get; set; }
        [NumericField(17)]
        public int PvInputPower2 { get; set; }
        [NumericField(18, 1)]
        public decimal PvInputVoltage1 { get; set; }
        [NumericField(19, 1)]
        public decimal PvInputVoltage2 { get; set; }
        /*
        string settingValueState = gs[20];
        string pv1WorkStatus = gs[21];
        string pv2WorkStatus = gs[22];
        string loadConnection = gs[23];
        string batteryStatus = gs[24];
        string invDirection = gs[25];
        string lineDirection = gs[26];
        string localParallelID = gs[27];*/
    }
}
