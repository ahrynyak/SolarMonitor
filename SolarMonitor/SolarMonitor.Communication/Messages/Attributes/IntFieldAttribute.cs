using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SolarMonitor.Communication.Messages.Attributes
{
    public class IntFieldAttribute : FieldBaseAttribute
    {
        public IntFieldAttribute(int position) : base(position)
        {
        }
    }
}
