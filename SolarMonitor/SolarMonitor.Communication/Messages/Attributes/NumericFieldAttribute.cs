using System;

namespace SolarMonitor.Communication.Messages.Attributes
{
    public class NumericFieldAttribute : FieldBaseAttribute
    {
        public int NumberOfDecimal { get; set; }

        public NumericFieldAttribute(int position, int numberOfDecimal) : base(position) //decimal type
        {
            Position = position;
            NumberOfDecimal = numberOfDecimal;
        }

        public NumericFieldAttribute(int position) : this(position, 0) //int type
        {
        }
    }
}
