using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SolarMonitor.Communication.Messages
{
    public abstract class MessageBase
    {
        public abstract string Command { get; }

        public void SetResponse(List<string> response)
        {
            
        }
    }
}
