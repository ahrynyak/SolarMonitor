using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SolarMonitor.Communication.Messages.Attributes
{
    public class StringFieldAttribute : FieldBaseAttribute
    {
        public StringFieldAttribute(int position) : base(position)
        {
        }
    }
}
