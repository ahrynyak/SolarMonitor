//using System;
//using System.Collections.Generic;
//using System.Linq;
//using System.Text;
//using System.Threading.Tasks;

//namespace ConsoleApplication2
//{
//    public class CRCUtil
//    {
//        public CRCUtil() { }

//        private static char[] crc_tb = { '\000', 'အ', '⁂', 'っ', '䂄',
//    '傥', '惆', '烧', 33032, 37161, 41290, 45419, 49548,
//    53677, 57806, 61935, 'ሱ', 'Ȑ', '㉳', '≒', '劵',
//    '䊔', '狷', '拖', 37689, 33560, 45947, 41818, 54205,
//    50076, 62463, 58334, '③', '㑃', 'Р', 'ᐁ', '擦',
//    '瓇', '䒤', '咅', 42346, 46411, 34088, 38153, 58862,
//    62927, 50604, 54669, '㙓', '♲', 'ᘑ', 'ذ', '盗',
//    '曶', '嚕', '䚴', 46939, 42874, 38681, 34616, 63455,
//    59390, 55197, 51132, '䣄', '壥', '梆', '碧', 'ࡀ',
//    'ᡡ', '⠂', '㠣', 51660, 55789, 59790, 63919, 35144,
//    39273, 43274, 47403, '嫵', '䫔', '窷', '檖', 'ᩱ',
//    '੐', '㨳', '⨒', 56317, 52188, 64447, 60318, 39801,
//    35672, 47931, 43802, '沦', '粇', '䳤', '峅', 'Ⱒ',
//    '㰃', 'ౠ', '᱁', 60846, 64911, 52716, 56781, 44330,
//    48395, 36200, 40265, '纗', '溶', '廕', '仴', '㸓',
//    '⸲', 'ṑ', '๰', 65439, 61374, 57309, 53244, 48923,
//    44858, 40793, 36728, 37256, 33193, 45514, 41451, 53516,
//    49453, 61774, 57711, 'ႀ', '¡', 'ヂ', '⃣', '倄',
//    '䀥', '灆', '恧', 33721, 37784, 41979, 46042, 49981,
//    54044, 58239, 62302, 'ʱ', 'ነ', '⋳', '㋒', '䈵',
//    '刔', '扷', '牖', 46570, 42443, 38312, 34185, 62830,
//    58703, 54572, 50445, '㓢', 'Ⓝ', 'ᒠ', 'ҁ', '瑦',
//    '摇', '吤', '䐅', 42971, 47098, 34713, 38840, 59231,
//    63358, 50973, 55100, '⛓', '㛲', 'ڑ', 'ᚰ', '晗',
//    '癶', '䘕', '嘴', 55628, 51565, 63758, 59695, 39368,
//    35305, 47498, 43435, '塄', '䡥', '砆', '栧', 'ᣀ',
//    '࣡', '㢂', '⢣', 52093, 56156, 60223, 64286, 35833,
//    39896, 43963, 48026, '䩵', '婔', '樷', '稖', '૱',
//    '᫐', '⪳', '㪒', 64814, 60687, 56684, 52557, 48554,
//    44427, 40424, 36297, '簦', '氇', '層', '䱅', '㲢',
//    'ⲃ', '᳠', 'ು', 61215, 65342, 53085, 57212, 44955,
//    49082, 36825, 40952, '渗', '縶', '乕', '年', '⺓',
//    '㺲', '໑', 'Ự' };

//        public static boolean checkCRC(String resultValue)
//        {
//            boolean result = false;
//            try
//            {
//                String firstValue = resultValue.substring(0, resultValue.length() - 2);
//                String lastValue = resultValue.substring(resultValue.length() - 2);



//                byte[] pByte = firstValue.getBytes();
//                int returnV = caluCRC(pByte);

//                String lastV = toHexString(lastValue);

//                int reV = Integer.parseInt(lastV, 16);


//                if (reV == returnV)
//                {
//                    result = true;
//                }
//                else
//                {
//                    result = false;
//                }

//            }
//            catch (Exception e)
//            {
//                result = false;
//            }

//            return result;
//        }

//        public static String toHexString(String s)
//        {
//            String str = "";
//            for (int i = 0; i < s.length(); i++)
//            {
//                short ch = (short)s.charAt(i);
//                if (ch < 0)
//                {
//                    ch = (short)(ch + 256);
//                }

//                String s4 = Integer.toHexString(ch);
//                if (s4.length() < 2)
//                {
//                    s4 = "0" + s4;
//                }
//                str = str + s4;
//            }

//            return str;
//        }

//        private static int caluCRC(byte[] pByte)
//        {
//            try
//            {
//                int len = pByte.length;




//                int i = 0;
//                int crc = 0;
//                while (len-- != 0)
//                {
//                    int da = 0xFF & (0xFF & crc >> 8) >> 4;
//                    crc <<= 4;
//                    crc ^= crc_tb[(0xFF & (da ^ pByte[i] >> 4))];

//                    da = 0xFF & (0xFF & crc >> 8) >> 4;
//                    crc <<= 4;
//                    int temp = 0xFF & (da ^ pByte[i] & 0xF);
//                    crc ^= crc_tb[temp];
//                    i++;
//                }
//                int bCRCLow = 0xFF & crc;
//                int bCRCHign = 0xFF & crc >> 8;
//                if ((bCRCLow == 40) || (bCRCLow == 13) || (bCRCLow == 10))
//                {
//                    bCRCLow++;
//                }
//                if ((bCRCHign == 40) || (bCRCHign == 13) || (bCRCHign == 10))
//                {
//                    bCRCHign++;
//                }
//                crc = (0xFF & bCRCHign) << 8;
//                return crc + bCRCLow;
//            }
//            catch (Exception ex)
//            {
//                ex.printStackTrace();
//            }
//            return 0;
//        }

//        public static byte[] getCRCByte(String command)
//        {
//            int crcint = caluCRC(command.getBytes());
//            int crclow = crcint & 0xFF;
//            int crchigh = crcint >> 8 & 0xFF;
//            return new byte[] { (byte)crchigh, (byte)crclow };
//        }

//        public static void main(String[] args)
//        {
//            String re = "(0.286";
//            System.out.println();
//            byte[] bytes = re.getBytes();
//            int ch = caluCRC(bytes);
//            System.out.println("Str:" + ch);
//            int i = ch;
//            String str = Integer.toHexString(i);
//            System.out.println("CRC十六进制字符串是:" + str);
//            System.out.println("CRC整数类型的数据是:" + i);
//        }
//    }
//}
