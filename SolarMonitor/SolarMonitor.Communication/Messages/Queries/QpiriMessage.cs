using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SolarMonitor.Communication.Messages.Attributes;
using SolarMonitor.Communication.Messages.Common;

namespace SolarMonitor.Communication.Messages.Queries
{
    public class QpiriMessage : MessageBase
    {
        public QpiriMessage() : base(new FieldParser())
        {
        }
        public override string Command => "PIRI";

        //[DecimalField(0, 1)]
        //public decimal GridRatedVoltage { get; set; }

        //[DecimalField(1, 1)]
        //public decimal GridRatedCurrent { get; set; }

        //[DecimalField(2, 1)]
        //public decimal AcOutputRatedVoltage { get; set; }

        //[DecimalField(3, 1)]
        //public decimal AcOutputRatedFrequency { get; set; }

        //[DecimalField(4, 1)]
        //public decimal AcOutputRatedCurrent { get; set; }

        //[DecimalField(5)]
        //public decimal AcOutputRatedApparentPower { get; set; }

        //[DecimalField(6)]
        //public decimal AcOutputRatedActivePower { get; set; }
    }
}
