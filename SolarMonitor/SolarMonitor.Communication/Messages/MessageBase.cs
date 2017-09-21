using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using SolarMonitor.Communication.Messages.Attributes;
using SolarMonitor.Communication.Messages.Common;

namespace SolarMonitor.Communication.Messages
{
    public abstract class MessageBase
    {
        private readonly IFieldParser fieldParser;

        protected MessageBase(IFieldParser fieldParser)
        {
            this.fieldParser = fieldParser;
            InitFields();
        }

        public abstract string Command { get; }

        private Dictionary<PropertyInfo, FieldBaseAttribute> Fields { get; set; }

        public void SetResponse(List<string> response)
        {
            var expectedFieldCount = Fields.Max(x => x.Value.Position + 1);
            if (response.Count != expectedFieldCount)
            {
                throw new ArgumentOutOfRangeException(nameof(response),
                    string.Format("Expected count {0}, actual count {1}", expectedFieldCount, response.Count));
            }
            foreach (var field in Fields)
            {
                var responseValue = response[field.Value.Position];
                if (!string.IsNullOrWhiteSpace(responseValue))
                {
                    object propertyValue = null;

                    var stringFieldAttribute = field.Value as StringFieldAttribute;
                    if (stringFieldAttribute != null)
                    {
                        propertyValue = fieldParser.ParseStringField(responseValue, stringFieldAttribute);
                    }
                    var intFieldAttribute = field.Value as IntFieldAttribute;
                    if (propertyValue == null && intFieldAttribute != null)
                    {
                        propertyValue = fieldParser.ParseIntField(responseValue, intFieldAttribute);
                    }
                    var numericFieldAttribute = field.Value as DecimalFieldAttribute;
                    if (propertyValue == null && numericFieldAttribute != null)
                    {
                        propertyValue = fieldParser.ParseDecimalField(responseValue, numericFieldAttribute);
                    }

                    var enumFieldAttribute = field.Value as EnumFieldAttribute;
                    if (propertyValue == null && enumFieldAttribute != null)
                    {
                        propertyValue = fieldParser.ParseEnumField(responseValue, field.Key.PropertyType);
                    }

                    field.Key.SetValue(this, propertyValue);
                }
            }
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
