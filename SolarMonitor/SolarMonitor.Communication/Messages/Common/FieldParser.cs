using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SolarMonitor.Communication.Messages.Attributes;

namespace SolarMonitor.Communication.Messages.Common
{
    public class FieldParser : IFieldParser
    {
        public string ParseStringField(string response, StringFieldAttribute numericFieldAttribute)
        {
            return response;
        }

        public int? ParseIntField(string response, IntFieldAttribute numericFieldAttribute)
        {
            int val;
            if (int.TryParse(response, out val))
            {
                return val;
            }
            else
            {
                //todo: cannot parse value
            }
            return null;
        }

        public decimal? ParseDecimalField(string response, DecimalFieldAttribute numericFieldAttribute)
        {
            decimal val;
            if (decimal.TryParse(response, out val))
            {
                if (numericFieldAttribute.DivideByTen)
                {
                    val = val/10;
                }
                return val;
            }
            else
            {
                //todo: cannot parse value
            }
            return null;
        }

        public object ParseEnumField(string response, Type enumType)
        {
            int val;
            if (int.TryParse(response, out val))
            {
                if (((int[]) Enum.GetValues(enumType)).Contains(val))
                {
                    return Enum.Parse(enumType, response);
                }
                else
                {
                    //enum value is not supported
                }
            }
            else
            {
                //todo: cannot parse value
            }
            return null;
        }
    }
}
