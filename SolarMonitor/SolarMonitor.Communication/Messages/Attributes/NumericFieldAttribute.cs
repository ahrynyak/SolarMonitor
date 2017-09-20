using System;

namespace SolarMonitor.Communication.Messages.Attributes
{
    public class NumericFieldAttribute : Attribute
    {
        public int Position { get; set; }
        public int NumberOfDecimal { get; set; }

        public NumericFieldAttribute(int position, int numberOfDecimal) //decimal
        {
            Position = position;
            NumberOfDecimal = numberOfDecimal;
        }

        public NumericFieldAttribute(int position) : this(position, 0) //int type
        {
        }
    }
}
