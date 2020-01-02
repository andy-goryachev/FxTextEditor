// Copyright © 2008-2020 Andy Goryachev <andy@goryachev.com>
package goryachev.common.util.text;
import goryachev.common.util.IntHashtable;
import goryachev.common.util.SB;


public class AccentedCharacters
{
	private static IntHashtable<String> accentedCharacters = initAccentedCharacters();
	

	private static IntHashtable<String> initAccentedCharacters()
	{
		IntHashtable<String> h = new IntHashtable();
		
		String[] accents =
		{
			// latin
			"ÀA", "ÁA", "ÂA", "ÃA", "ÄA", "ÅA",  
			"ÇC", 
			"ÈE", "ÉE", "ÊE", "ËE", 
			"ÌI", "ÍI", "ÎI", "ÏI",
			"ÐD",
			"ÑN",
			"ÒO", "ÓO", "ÔO", "ÕO", "ÖO", "ØO",
			"ÙU", "ÚU", "ÛU", "ÜU", 
			"ÝY",
			"àa", "áa", "âa", "ãa", "äa", "åa", 
			"çc",
			"èe", "ée", "êe", "ëe",
			"ìi", "íi", "îi", "ïi",
			"ðd",
			"ñn",
			"òo", "óo", "ôo", "õo", "öo", "øo",
			"ùu", "úu", "ûu", "üu",
			"ýy", "ÿy",
			
			"ĀA", "āa", "ĂA", "ăa", "ĄA", "ąa",
			"ĆC", "ćc", "ĈC", "ĉc", "ĊC", "ċc", "ČC", "čc", 
			"ĎD", "ďd", "ĐD", "đd",
			"ĒE", "ēe", "ĔE", "ĕe", "ĖE", "ėe", "ĘE", "ęe", "ĚE", "ěe",
			"ĜG", "ĝg", "ĞG", "ğg", "ĠG", "ġg", "ĢG", "ģg",
			"ĤH", "ĥh", "ĦH", "ħh",
			"ĨI", "ĩi", "ĪI", "īi", "ĬI", "ĭi", "ĮI", "įi", "İI", "ıi",
			"ĴJ", "ĵj", 
			"ĶK", "ķk",
			"ĹL", "ĺl", "ĻL", "ļl", "ĽL", "ľl", "ĿL", "ŀl", "ŁL", "łl",
			"ŃN", "ńn", "ŅN", "ņn", "ŇN", "ňn", "ŉn", "ŊN", "ŋn",
			"ŌO", "ōo", "ŎO", "ŏo", "ŐO", "őo",
			"ŔR", "ŕr", "ŖR", "ŗr", "ŘR", "řr",
			"ŚS", "śs", "ŜS", "ŝs", "ŞS", "şs", "ŠS", "šs", 
			"ŢT", "ţt", "ŤT", "ťt", "ŦT", "ŧt", 
			"ŨU", "ũu", "ŪU", "ūu", "ŬU", "ŭu", "ŮU", "ůu", "ŰU", "űu", "ŲU", "ųu",
			"ŴW", "ŵw",
			"ŶY", "ŷy", "ŸY",  
			"ŹZ", "źz", "ŻZ", "żz", "ŽZ", "žz",
			"ƀb", "ƁB", "ƂB", "ƃb",
			"ƈc",
			"ƉD", "ƊD", "ƋD", "ƌd", 
			"ƐE",
			"ƑF", "ƒf", 
			"ƓG",
			"ƗI",
			"ƘK", "ƙk",
			"ƚl",
			"ƝN", "ƞn",
			"ƟO", "ƠO", "ơo",
			"ƤP", "ƥp", 
			"ƫt", "ƬT", "ƭt", "ƮT",
			"ƯU", "ưu",
			"ƲV",
			"ƳY", "ƴy",
			"ƵZ", "ƶz",

			"ǍA", "ǎa",
			"ǏI", "ǐi",
			"ǑO", "ǒo",
			"ǓU", "ǔu", "ǕU", "ǖu", "ǗU", "ǘu", "ǙU", "ǚu", "ǛU", "ǜu",
			"ǞA", "ǟa", "ǠA", "ǡa",
			"ǤG", "ǥg", "ǦG", "ǧg", 
			"ǨK", "ǩk", 
			"ǪO", "ǫo", "ǬO", "ǭo", 
			"ǰj", 
			"ǴG", "ǵg",

			"ǺA", "ǻa",
			"ǾO", "ǿo", 
			"ȀA", "ȁa", "ȂA", "ȃa", 
			"ȄE", "ȅe", "ȆE", "ȇe", 
			"ȈI", "ȉi", "ȊI", "ȋi", 
			"ȌO", "ȍo", "ȎO", "ȏo",
			"ȐR", "ȑr", "ȒR", "ȓr", "ȔU", "ȕu", "ȖU", "ȗu",
			"ɓb",
			"ɕc",
			"ɖd", "ɗd", 
			"ɛe",
			"ɠg", 
			"ɦh",
			"ɨi",
			"ɫl", "ɬl", "ɭl", 
			"ɲn", "ɳn", 
			"ɵo",
			"ɼr", "ɽr", "ɾr",
			"ʂs",
			"ʈt", 
			"ʉu",
			"ʐz", "ʑz",

			"ḀA", "ḁa",
			"ḂB", "ḃb", "ḄB", "ḅb", "ḆB", "ḇb",
			"ḈC", "ḉc",
			"ḊD", "ḋd", "ḌD", "ḍd", "ḎD", "ḏd", "ḐD", "ḑd", "ḒD", "ḓd",
			"ḔE", "ḕe", "ḖE", "ḗe", "ḘE", "ḙe", "ḚE", "ḛe", "ḜE", "ḝe", 
			"ḞF", "ḟf", 
			"ḠG", "ḡg", 
			"ḢH", "ḣh", "ḤH", "ḥh", "ḦH", "ḧh", "ḨH", "ḩh", "ḪH", "ḫh", 
			"ḬI", "ḭi", "ḮI", "ḯi", 
			"ḰK", "ḱk", "ḲK", "ḳk", "ḴK", "ḵk", 
			"ḶL", "ḷl", "ḸL", "ḹl", "ḺL", "ḻl", "ḼL", "ḽl", 
			"ḾM", "ḿm", "ṀM", "ṁm", "ṂM", "ṃm", 
			"ṄN", "ṅn", "ṆN", "ṇn", "ṈN", "ṉn", "ṊN", "ṋn", 
			"ṌO", "ṍo", "ṎO", "ṏo", "ṐO", "ṑo", "ṒO", "ṓo", 
			"ṔP", "ṕp", "ṖP", "ṗp", 
			"ṘR", "ṙr", "ṚR", "ṛr", "ṜR", "ṝr", "ṞR", "ṟr", 
			"ṠS", "ṡs", "ṢS", "ṣs", "ṤS", "ṥs", "ṦS", "ṧs", "ṩs", 
			"ṪT", "ṫt", "ṬT", "ṭt", "ṮT", "ṯt", "ṰT", "ṱt", 
			"ṲU", "ṳu", "ṴU", "ṵu", "ṶU", "ṷu", "ṸU", "ṹu", "ṺU", "ṻu", 
			"ṼV", "ṽv", "ṾV", "ṿv", 
			"ẀW", "ẁw", "ẂW", "ẃw", "ẄW", "ẅw", "ẆW", "ẇw", "ẈW", "ẉw", 
			"ẊX", "ẋx", "ẌX", "ẍx", 
			"ẎY", "ẏy", 
			"ẐZ", "ẑz", "ẒZ", "ẓz", "ẔZ", "ẕz", 
			"ẖh", 
			"ẗt", 
			"ẘw", 
			"ẙy", 
			"ẚa", 
			"ẠA", "ạa", "ẢA", "ảa", "ẤA", "ấa", "ẦA", "ầa", "ẨA", "ẩa", "ẪA", "ẫa", "ẬA", "ậa", "ẮA", "ắa", "ẰA", "ằa", "ẲA", "ẳa", "ẴA", "ẵa", "ẶA", "ặa", 
			"ẸE", "ẹe", "ẺE", "ẻe", "ẼE", "ẽe", "ẾE", "ếe", "ỀE", "ềe", "ỂE", "ểe", "ỄE", "ễe", "ỆE", "ệe", 
			"ỈI", "ỉi", "ỊI", "ịi", 
			"ỌO", "ọo", "ỎO", "ỏo", "ỐO", "ốo", "ỒO", "ồo", "ỔO", "ổo", "ỖO", "ỗo", "ỘO", "ộo", "ỚO", "ớo", "ỜO", "ờo", "ỞO", "ởo", "ỠO", "ỡo", "ỢO", "ợo", 
			"ỤU", "ụu", "ỦU", "ủu", "ỨU", "ứu", "ỪU", "ừu", "ỬU", "ửu", "ỮU", "ữu", "ỰU", "ựu", 
			"ỲY", "ỳy", "ỴY", "ỵy", "ỶY", "ỷy", "ỸY", "ỹy", 

			// greek
			"ΆΑ",
			"ΈΕ",
			"ΉΗ",
			"ΊΙ",
			"ΌΟ",
			"ΎΥ",
			"ΏΩ",
			"ΐι",
			"ΪΙ",
			"ΫΥ",
			"άα",
			"έε",
			"ήη",
			"ίι",
			"ΰυ",
			"ϊι",
			"ύυ",
			"ώω",
			"ϋυ",
			"όο",
			"ϓΥ","ϔΥ",
			"ἀα","ἁα","ἂα","ἃα","ἄα","ἅα","ἆα","ἇα",
			"ἈΑ","ἉΑ","ἊΑ","ἋΑ","ἌΑ","ἍΑ","ἎΑ","ἏΑ",
			"ἐε","ἑε","ἒε","ἓε","ἔε","ἕε",
			"ἘΕ","ἙΕ","ἚΕ","ἛΕ","ἜΕ","ἝΕ",
			"ἠη","ἡη","ἢη","ἣη","ἤη","ἥη","ἦη","ἧη",
			"ἨΗ","ἩΗ","ἪΗ","ἫΗ","ἬΗ","ἭΗ","ἮΗ","ἯΗ",
			"ἰι","ἱι","ἲι","ἳι","ἴι","ἵι","ἶι","ἷι",
			"ἸΙ","ἹΙ","ἺΙ","ἻΙ","ἼΙ","ἽΙ","ἾΙ","ἿΙ",
			"ὀο","ὁο","ὂο","ὃο","ὄο","ὅο",
			"ὈΟ","ὉΟ","ὊΟ","ὋΟ","ὌΟ","ὍΟ",
			"ὐυ","ὑυ","ὒυ","ὓυ","ὔυ","ὕυ","ὖυ","ὗυ",
			"ὙΥ","ὛΥ","ὝΥ","ὟΥ",
			"ὠω","ὡω","ὢω","ὣω","ὤω","ὥω","ὦω","ὧω",
			"ὨΩ","ὩΩ","ὪΩ","ὫΩ","ὬΩ","ὭΩ","ὮΩ","ὯΩ",
			"ὰα","άα",
			"ὲε","έε",
			"ὴη","ήη",
			"ὶι","ίι",
			"ὸο","όο",
			"ὺυ","ύυ",
			"ὼω","ώω",
			"ᾀα","ᾁα","ᾂα","ᾃα","ᾄα","ᾅα","ᾆα","ᾇα",
			"ᾈΑ","ᾉΑ","ᾊΑ","ᾋΑ","ᾌΑ","ᾍΑ","ᾎΑ","ᾏΑ",
			"ᾐη","ᾑη","ᾒη","ᾓη","ᾔη","ᾕη","ᾖη","ᾗη",
			"ᾘΗ","ᾙΗ","ᾚΗ","ᾛΗ","ᾜΗ","ᾝΗ","ᾞΗ","ᾟΗ",
			"ᾠω","ᾡω","ᾢω","ᾣω","ᾤω","ᾥω","ᾦω","ᾧω",
			"ᾨΩ","ᾩΩ","ᾪΩ","ᾫΩ","ᾬΩ","ᾭΩ","ᾮΩ","ᾯΩ",
			"ᾰα","ᾱα","ᾲα","ᾳα","ᾴα","ᾶα","ᾷα",
			"ᾸΑ","ᾹΑ","ᾺΑ","ΆΑ","ᾼΑ",
			"ῂη","ῃη","ῄη","ῆη","ῇη",
			"ῈΕ","ΈΕ",
			"ῊΗ","ΉΗ","ῌΗ",
			"ῐι","ῑι","ῒι","ΐι","ῖι","ῗι",
			"ῘΙ","ῙΙ","ῚΙ","ΊΙ",
			"ῠυ","ῡυ","ῢυ","ΰυ",
			"ῤρ","ῥρ",
			"ῦυ","ῧυ",
			"ῨΥ","ῩΥ","ῪΥ","ΎΥ",
			"ῬΡ",
			"ῲω","ῳω","ῴω","ῶω","ῷω",
			"ῸΟ","ΌΟ",
			"ῺΩ","ΏΩ","ῼΩ",

			// cyrillic
			"ЁЕ",
			"ЇІ",
			"ЌК",
			"ЎУ",
			"йи",
			"ёе",
			"ѓг",
			"ќк",
			"ўу",
			"ЙИ"
		};
		
		for(String s: accents)
		{
			if(h.put(s.charAt(0), s) != null)
			{
				throw new Error(s);
			}
		}
		return h;
	}
	

	public static char removeAccent(char c)
	{
		String s = accentedCharacters.get(c);
		if(s != null)
		{
			return accentedCharacters.get(c).charAt(1);
		}
		return c;
	}


	public static String removeAccents(String s)
	{
		SB sb = new SB(s);
		removeAccents(sb);
		return sb.toString();
	}
	
	
	public static void removeAccents(SB sb)
	{
		for(int i=0; i<sb.length(); i++)
		{
			char c = sb.charAt(i);
			c = removeAccent(c);
			sb.setCharAt(i, c);
		}
	}
}
