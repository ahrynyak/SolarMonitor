using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SolarMonitor.Communication.Messages.Attributes;

namespace SolarMonitor.Communication.Messages.Queries
{
    public class QpiriMessage : MessageBase
    {
        public override string Command => "PIRI";

        [NumericField(0, 1)]
        public decimal GridRatedVoltage { get; set; }

        [NumericField(1, 1)]
        public decimal GridRatedCurrent { get; set; }

        [NumericField(2, 1)]
        public decimal AcOutputRatedVoltage { get; set; }

        [NumericField(3, 1)]
        public decimal AcOutputRatedFrequency { get; set; }

        [NumericField(4, 1)]
        public decimal AcOutputRatedCurrent { get; set; }

        [NumericField(5)]
        public decimal AcOutputRatedApparentPower { get; set; }

        [NumericField(6)]
        public decimal AcOutputRatedActivePower { get; set; }

    }
}
