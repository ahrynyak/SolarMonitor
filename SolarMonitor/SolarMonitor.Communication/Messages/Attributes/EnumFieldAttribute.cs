using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SolarMonitor.Communication.Messages.Attributes
{
    public class EnumFieldAttribute: NumericFieldAttribute
    {
        public EnumFieldAttribute(int position) : base(position, 0)
        {
        }
    }
}
