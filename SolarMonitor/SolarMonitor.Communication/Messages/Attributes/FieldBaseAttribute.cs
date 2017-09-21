using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SolarMonitor.Communication.Messages.Attributes
{
    public class FieldBaseAttribute : Attribute
    {
        public FieldBaseAttribute(int position)
        {
            Position = position;
        }

        public int Position { get; set; }
    }
}
