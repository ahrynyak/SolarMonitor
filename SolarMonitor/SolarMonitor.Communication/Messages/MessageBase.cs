using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using SolarMonitor.Communication.Messages.Attributes;

namespace SolarMonitor.Communication.Messages
{
    public abstract class MessageBase
    {
        protected MessageBase()
        {
            InitFields();
        }

        public abstract string Command { get; }

        private Dictionary<PropertyInfo, FieldBaseAttribute> Fields { get; set; }


        public void SetResponse(List<string> response)
        {
            if (response.Count < Fields.Max(x => x.Value.Position - 1))
            {
                //todo: validation response is too short
            }
            foreach (var field in Fields)
            {
                var responseValue = response[field.Value.Position];
                if (!string.IsNullOrWhiteSpace(responseValue))
                {
                    object propertyValue = null;

                    var numericFieldAttribute = field.Value as NumericFieldAttribute;
                    if (numericFieldAttribute != null)
                    {
                        propertyValue = ParseNumericField(responseValue, numericFieldAttribute);
                    }

                    var enumFieldAttribute = field.Value as EnumFieldAttribute;
                    if (propertyValue == null && enumFieldAttribute != null)
                    {
                        propertyValue = ParseEnumField(responseValue, field.Key.PropertyType);
                    }

                    field.Key.SetValue(this, propertyValue);
                }
            }
        }

        private decimal? ParseNumericField(string response, NumericFieldAttribute numericFieldAttribute)
        {
            decimal val;
            if (decimal.TryParse(response, out val))
            {
                if (numericFieldAttribute.NumberOfDecimal > 0)
                {
                    val = val/(numericFieldAttribute.NumberOfDecimal*10);
                }
                return val;
            }
            else
            {
                //todo: cannot parse value
            }
            return null;
        }

        private object ParseEnumField(string response, Type enumType)
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

        private void InitFields()
        {
            Fields = new Dictionary<PropertyInfo, FieldBaseAttribute>();
            var properties = GetType().GetProperties();
            foreach (var propertyInfo in properties)
            {
                var attribute = propertyInfo.GetCustomAttributes(typeof (FieldBaseAttribute), true).
                    FirstOrDefault() as FieldBaseAttribute;

                if (attribute != null)
                {
                    Fields.Add(propertyInfo, attribute);
                }
            }
        }
    }
}
