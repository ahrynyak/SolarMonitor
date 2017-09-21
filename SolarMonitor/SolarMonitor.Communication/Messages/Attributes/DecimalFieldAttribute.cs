using System;

namespace SolarMonitor.Communication.Messages.Attributes
{
    public class DecimalFieldAttribute : FieldBaseAttribute
    {
        public bool DivideByTen { get; set; }

        public DecimalFieldAttribute(int position, bool divideByTen) : base(position) //decimal type
        {
            Position = position;
            DivideByTen = divideByTen;
        }

        public DecimalFieldAttribute(int position) : this(position, false) //int type
        {
        }
    }
}
