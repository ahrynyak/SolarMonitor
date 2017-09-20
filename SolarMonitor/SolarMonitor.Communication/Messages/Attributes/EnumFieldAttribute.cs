using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SolarMonitor.Communication.Messages.Attributes
{
    public class EnumFieldAttribute: FieldBaseAttribute
    {
        public EnumFieldAttribute(int position) : base(position)
        {
        }
    }
}
