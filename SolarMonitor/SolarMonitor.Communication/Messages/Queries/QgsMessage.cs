using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SolarMonitor.Communication.Messages.Attributes;
using SolarMonitor.Communication.Messages.Common;

namespace SolarMonitor.Communication.Messages.Queries
{
    public class QgsMessage : MessageBase
    {
        public QgsMessage() : base(new FieldParser())
        {
        }

        public override string Command => "GS";

        [DecimalField(0, true)]
        public decimal GridVoltage { get; set; }

        [DecimalField(1, true)]
        public decimal GridFrequency { get; set; }

        [DecimalField(2, true)]
        public decimal AcOutputVoltage { get; set; }

        [DecimalField(3, true)]
        public decimal AcOutputFrequency { get; set; }

        [IntField(4)]
        public int AcOutputApperentPower { get; set; }

        [IntField(5)]
        public int AcOutputActivePower { get; set; }

        [IntField(6)]
        public int OutputLoadPercent { get; set; }

        [DecimalField(7, true)]
        public decimal BatteryVoltage { get; set; }

        [DecimalField(8, true)]
        public decimal BatteryVoltageFromScc1 { get; set; }

        [DecimalField(9, true)]
        public decimal BatteryVoltageFromScc2 { get; set; }

        [DecimalField(10)]
        public decimal DisChargingCurrent { get; set; }

        [DecimalField(11)]
        public decimal ChargingCurrent { get; set; }

        [IntField(12)]
        public int BatteryCapacity { get; set; }

        [IntField(13)]
        public int HeatSinkTemperature { get; set; }

        [IntField(14)]
        public int MpptChargerTemperature1 { get; set; }

        [IntField(15)]
        public int MpptChargerTemperature2 { get; set; }

        [IntField(16)]
        public int PvInputPower1 { get; set; }

        [IntField(17)]
        public int PvInputPower2 { get; set; }

        [DecimalField(18, true)]
        public decimal PvInputVoltage1 { get; set; }

        [DecimalField(19, true)]
        public decimal PvInputVoltage2 { get; set; }

        [StringField(20)]
        public string SettingValueState { get; set; }

        [StringField(21)]
        public string Pv1WorkStatus { get; set; }

        [StringField(22)]
        public string Pv2WorkStatus { get; set; }

        [StringField(23)]
        public string LoadConnection { get; set; }

        [StringField(24)]
        public string BatteryStatus { get; set; }

        [StringField(25)]
        public string InvDirection { get; set; }

        [StringField(26)]
        public string LineDirection { get; set; }

        [StringField(27)]
        public string LocalParallelId { get; set; }

        
    }
}
