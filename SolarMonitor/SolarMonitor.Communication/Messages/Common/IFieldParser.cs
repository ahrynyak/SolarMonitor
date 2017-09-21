using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SolarMonitor.Communication.Messages.Attributes;

namespace SolarMonitor.Communication.Messages.Common
{
    public interface IFieldParser
    {
        string ParseStringField(string response, StringFieldAttribute numericFieldAttribute);
        int? ParseIntField(string response, IntFieldAttribute numericFieldAttribute);
        decimal? ParseDecimalField(string response, DecimalFieldAttribute numericFieldAttribute);
        object ParseEnumField(string response, Type enumType);
    }
}
