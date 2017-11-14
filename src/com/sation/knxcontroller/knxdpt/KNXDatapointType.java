<<<<<<< HEAD
package com.sation.knxcontroller.knxdpt;

import com.sation.knxcontroller.models.KNXGroupAddress;

import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit1;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit112;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit16;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit2;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit24;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit3;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit32;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit4;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit48;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit5;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit6;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit64;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit7;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit8;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit80;

public class KNXDatapointType {
    public final static String DPT_1 = "1";
    public final static String DPT_2 = "2";
    public final static String DPT_3 = "3";
    public final static String DPT_4 = "4";
    public final static String DPT_5 = "5";
    public final static String DPT_6 = "6";
    public final static String DPT_7 = "7";
    public final static String DPT_8 = "8";
    public final static String DPT_9 = "9";
    public final static String DPT_10 = "10";
    public final static String DPT_11 = "11";
    public final static String DPT_12 = "12";
    public final static String DPT_13 = "13";
    public final static String DPT_14 = "14";
    public final static String DPT_15 = "15";
    public final static String DPT_16 = "16";
    public final static String DPT_17 = "17";
    public final static String DPT_18 = "18";
    public final static String DPT_19 = "19";
    public final static String DPT_20 = "20";
    public final static String DPT_21 = "21";
    public final static String DPT_22 = "22";
    public final static String DPT_23 = "23";
    public final static String DPT_25 = "25";
    public final static String DPT_26 = "26";
    public final static String DPT_27 = "27";
    public final static String DPT_29 = "29";
    public final static String DPT_30 = "30";
    public final static String DPT_206 = "206";
    public final static String DPT_217 = "217";
    public final static String DPT_219 = "219";
    public final static String DPT_222 = "222";
    public final static String DPT_229 = "229";
    public final static String DPT_230 = "230";
    public final static String DPT_232 = "232";
    public final static String DPT_234 = "234";
    public final static String DPT_237 = "237";
    public final static String DPT_238 = "238";
     
    public final static String DPST_ANY = "*";
    public final static String DPST_0 = "000";
    public final static String DPST_1 = "001";
    public final static String DPST_2 = "002";
    public final static String DPST_3 = "003";
    public final static String DPST_4 = "004";
    public final static String DPST_5 = "005";
    public final static String DPST_6 = "006";
    public final static String DPST_7 = "007";
    public final static String DPST_8 = "008";
    public final static String DPST_9 = "009";
    public final static String DPST_10 = "010";
    public final static String DPST_11 = "011";
    public final static String DPST_12 = "012";
    public final static String DPST_13 = "013";
    public final static String DPST_14 = "014";
    public final static String DPST_15 = "015";
    public final static String DPST_16 = "016";
    public final static String DPST_17 = "017";
    public final static String DPST_18 = "018";
    public final static String DPST_19 = "019";
    public final static String DPST_20 = "020";
    public final static String DPST_21 = "021";
    public final static String DPST_22 = "022";
    public final static String DPST_23 = "023";
    public final static String DPST_24 = "024";
    public final static String DPST_25 = "025";
    public final static String DPST_26 = "026";
    public final static String DPST_27 = "027";
    public final static String DPST_28 = "028";
    public final static String DPST_29 = "029";
    public final static String DPST_30 = "030";
    public final static String DPST_31 = "031";
    public final static String DPST_32 = "032";
    public final static String DPST_33 = "033";
    public final static String DPST_34 = "034";
    public final static String DPST_35 = "035";
    public final static String DPST_36 = "036";
    public final static String DPST_37 = "037";
    public final static String DPST_38 = "038";
    public final static String DPST_39 = "039";
    public final static String DPST_40 = "040";
    public final static String DPST_41 = "041";
    public final static String DPST_42 = "042";
    public final static String DPST_43 = "043";
    public final static String DPST_44 = "044";
    public final static String DPST_45 = "045";
    public final static String DPST_46 = "046";
    public final static String DPST_47 = "047";
    public final static String DPST_48 = "048";
    public final static String DPST_49 = "049";
    public final static String DPST_50 = "050";
    public final static String DPST_51 = "051";
    public final static String DPST_52 = "052";
    public final static String DPST_53 = "053";
    public final static String DPST_54 = "054";
    public final static String DPST_55 = "055";
    public final static String DPST_56 = "056";
    public final static String DPST_57 = "057";
    public final static String DPST_58 = "058";
    public final static String DPST_59 = "059";
    public final static String DPST_60 = "060";
    public final static String DPST_61 = "061";
    public final static String DPST_62 = "062";
    public final static String DPST_63 = "063";
    public final static String DPST_64 = "064";
    public final static String DPST_65 = "065";
    public final static String DPST_66 = "066";
    public final static String DPST_67 = "067";
    public final static String DPST_68 = "068";
    public final static String DPST_69 = "069";
    public final static String DPST_70 = "070";
    public final static String DPST_71 = "071";
    public final static String DPST_72 = "072";
    public final static String DPST_73 = "073";
    public final static String DPST_74 = "074";
    public final static String DPST_75 = "075";
    public final static String DPST_76 = "076";
    public final static String DPST_77 = "077";
    public final static String DPST_78 = "078";
    public final static String DPST_79 = "079";
    public final static String DPST_100 = "100";
    public final static String DPST_101 = "101";
    public final static String DPST_102 = "102";
    public final static String DPST_103 = "103";
    public final static String DPST_104 = "104";
    public final static String DPST_105 = "105";
    public final static String DPST_106 = "106";
    public final static String DPST_107 = "107";
    public final static String DPST_108 = "108";
    public final static String DPST_109 = "109";
    public final static String DPST_110 = "110";
    public final static String DPST_111 = "111";
    public final static String DPST_112 = "112";
    public final static String DPST_113 = "113";
    public final static String DPST_114 = "114";
    public final static String DPST_120 = "120";
    public final static String DPST_121 = "121";
    public final static String DPST_122 = "122";
    public final static String DPST_600 = "600";
    public final static String DPST_601 = "601";
    public final static String DPST_602 = "602";
    public final static String DPST_603 = "603";
    public final static String DPST_604 = "604";
    public final static String DPST_605 = "605";
    public final static String DPST_606 = "606";
    public final static String DPST_607 = "607";
    public final static String DPST_608 = "608";
    public final static String DPST_609 = "609";
    public final static String DPST_610 = "610";
    public final static String DPST_801 = "801";
    public final static String DPST_802 = "802";
    public final static String DPST_803 = "803";
    public final static String DPST_804 = "804";
    public final static String DPST_1000 = "1000";
    public final static String DPST_1001 = "1001";
    public final static String DPST_1002 = "1002";
    public final static String DPST_1003 = "1003";
    public final static String DPST_1010 = "1010";

    public static int bytes2int(byte[] array, int type) {
        KNXDataType dataType = KNXDataType.values()[type];
        int value = 0;
        switch(dataType){
            case Bit1:
            case Bit2:
            case Bit3:
            case Bit4:
            case Bit5:
            case Bit6:
            case Bit7:
            case Bit8:
                value = array[0];
                break;
            case Bit16:
                value = array[0]*100+array[1];
                break;
            case Bit24:
                value = array[0]*10000+array[1]*100+array[2];
                break;

            case Bit32:
                value = array[0]*1000000+array[1]*10000+array[2]*100+array[3];
                break;

            case Bit48:
                break;

            case Bit64:
                break;

            case Bit80:
                break;

            case Bit112:
                break;

            default:
                break;
        }

        return value;
    }
}
=======
package com.sation.knxcontroller.knxdpt;

import com.sation.knxcontroller.models.KNXGroupAddress;

import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit1;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit112;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit16;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit2;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit24;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit3;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit32;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit4;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit48;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit5;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit6;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit64;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit7;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit8;
import static com.sation.knxcontroller.knxdpt.KNXDataType.Bit80;

public class KNXDatapointType {
    public final static String DPT_1 = "1";
    public final static String DPT_2 = "2";
    public final static String DPT_3 = "3";
    public final static String DPT_4 = "4";
    public final static String DPT_5 = "5";
    public final static String DPT_6 = "6";
    public final static String DPT_7 = "7";
    public final static String DPT_8 = "8";
    public final static String DPT_9 = "9";
    public final static String DPT_10 = "10";
    public final static String DPT_11 = "11";
    public final static String DPT_12 = "12";
    public final static String DPT_13 = "13";
    public final static String DPT_14 = "14";
    public final static String DPT_15 = "15";
    public final static String DPT_16 = "16";
    public final static String DPT_17 = "17";
    public final static String DPT_18 = "18";
    public final static String DPT_19 = "19";
    public final static String DPT_20 = "20";
    public final static String DPT_21 = "21";
    public final static String DPT_22 = "22";
    public final static String DPT_23 = "23";
    public final static String DPT_25 = "25";
    public final static String DPT_26 = "26";
    public final static String DPT_27 = "27";
    public final static String DPT_29 = "29";
    public final static String DPT_30 = "30";
    public final static String DPT_206 = "206";
    public final static String DPT_217 = "217";
    public final static String DPT_219 = "219";
    public final static String DPT_222 = "222";
    public final static String DPT_229 = "229";
    public final static String DPT_230 = "230";
    public final static String DPT_232 = "232";
    public final static String DPT_234 = "234";
    public final static String DPT_237 = "237";
    public final static String DPT_238 = "238";
     
    public final static String DPST_ANY = "*";
    public final static String DPST_0 = "000";
    public final static String DPST_1 = "001";
    public final static String DPST_2 = "002";
    public final static String DPST_3 = "003";
    public final static String DPST_4 = "004";
    public final static String DPST_5 = "005";
    public final static String DPST_6 = "006";
    public final static String DPST_7 = "007";
    public final static String DPST_8 = "008";
    public final static String DPST_9 = "009";
    public final static String DPST_10 = "010";
    public final static String DPST_11 = "011";
    public final static String DPST_12 = "012";
    public final static String DPST_13 = "013";
    public final static String DPST_14 = "014";
    public final static String DPST_15 = "015";
    public final static String DPST_16 = "016";
    public final static String DPST_17 = "017";
    public final static String DPST_18 = "018";
    public final static String DPST_19 = "019";
    public final static String DPST_20 = "020";
    public final static String DPST_21 = "021";
    public final static String DPST_22 = "022";
    public final static String DPST_23 = "023";
    public final static String DPST_24 = "024";
    public final static String DPST_25 = "025";
    public final static String DPST_26 = "026";
    public final static String DPST_27 = "027";
    public final static String DPST_28 = "028";
    public final static String DPST_29 = "029";
    public final static String DPST_30 = "030";
    public final static String DPST_31 = "031";
    public final static String DPST_32 = "032";
    public final static String DPST_33 = "033";
    public final static String DPST_34 = "034";
    public final static String DPST_35 = "035";
    public final static String DPST_36 = "036";
    public final static String DPST_37 = "037";
    public final static String DPST_38 = "038";
    public final static String DPST_39 = "039";
    public final static String DPST_40 = "040";
    public final static String DPST_41 = "041";
    public final static String DPST_42 = "042";
    public final static String DPST_43 = "043";
    public final static String DPST_44 = "044";
    public final static String DPST_45 = "045";
    public final static String DPST_46 = "046";
    public final static String DPST_47 = "047";
    public final static String DPST_48 = "048";
    public final static String DPST_49 = "049";
    public final static String DPST_50 = "050";
    public final static String DPST_51 = "051";
    public final static String DPST_52 = "052";
    public final static String DPST_53 = "053";
    public final static String DPST_54 = "054";
    public final static String DPST_55 = "055";
    public final static String DPST_56 = "056";
    public final static String DPST_57 = "057";
    public final static String DPST_58 = "058";
    public final static String DPST_59 = "059";
    public final static String DPST_60 = "060";
    public final static String DPST_61 = "061";
    public final static String DPST_62 = "062";
    public final static String DPST_63 = "063";
    public final static String DPST_64 = "064";
    public final static String DPST_65 = "065";
    public final static String DPST_66 = "066";
    public final static String DPST_67 = "067";
    public final static String DPST_68 = "068";
    public final static String DPST_69 = "069";
    public final static String DPST_70 = "070";
    public final static String DPST_71 = "071";
    public final static String DPST_72 = "072";
    public final static String DPST_73 = "073";
    public final static String DPST_74 = "074";
    public final static String DPST_75 = "075";
    public final static String DPST_76 = "076";
    public final static String DPST_77 = "077";
    public final static String DPST_78 = "078";
    public final static String DPST_79 = "079";
    public final static String DPST_100 = "100";
    public final static String DPST_101 = "101";
    public final static String DPST_102 = "102";
    public final static String DPST_103 = "103";
    public final static String DPST_104 = "104";
    public final static String DPST_105 = "105";
    public final static String DPST_106 = "106";
    public final static String DPST_107 = "107";
    public final static String DPST_108 = "108";
    public final static String DPST_109 = "109";
    public final static String DPST_110 = "110";
    public final static String DPST_111 = "111";
    public final static String DPST_112 = "112";
    public final static String DPST_113 = "113";
    public final static String DPST_114 = "114";
    public final static String DPST_120 = "120";
    public final static String DPST_121 = "121";
    public final static String DPST_122 = "122";
    public final static String DPST_600 = "600";
    public final static String DPST_601 = "601";
    public final static String DPST_602 = "602";
    public final static String DPST_603 = "603";
    public final static String DPST_604 = "604";
    public final static String DPST_605 = "605";
    public final static String DPST_606 = "606";
    public final static String DPST_607 = "607";
    public final static String DPST_608 = "608";
    public final static String DPST_609 = "609";
    public final static String DPST_610 = "610";
    public final static String DPST_801 = "801";
    public final static String DPST_802 = "802";
    public final static String DPST_803 = "803";
    public final static String DPST_804 = "804";
    public final static String DPST_1000 = "1000";
    public final static String DPST_1001 = "1001";
    public final static String DPST_1002 = "1002";
    public final static String DPST_1003 = "1003";
    public final static String DPST_1010 = "1010";

    public static int bytes2int(byte[] array, int type) {
        KNXDataType dataType = KNXDataType.values()[type];
        int value = 0;
        switch(dataType){
            case Bit1:
            case Bit2:
            case Bit3:
            case Bit4:
            case Bit5:
            case Bit6:
            case Bit7:
            case Bit8:
                value = array[0];
                break;
            case Bit16:
                value = array[0]*100+array[1];
                break;
            case Bit24:
                value = array[0]*10000+array[1]*100+array[2];
                break;

            case Bit32:
                value = array[0]*1000000+array[1]*10000+array[2]*100+array[3];
                break;

            case Bit48:
                break;

            case Bit64:
                break;

            case Bit80:
                break;

            case Bit112:
                break;

            default:
                break;
        }

        return value;
    }
}
>>>>>>> SationCentralControl(10inch)
