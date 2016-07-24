var DATE_DEFAULT_FORMAT = "yyyy-MM-dd";
var TIME_DEFAULT_FORMAT = "HH:mm:ss";
Zxb = {}
Zxb.Init = function() {
	Array.prototype.indexOf = function(item) {
		for (var i = 0; i < this.length; i++) {
			if (item == this[i]) {
				return i;
			}
		}
		return -1;
	}
	Array.prototype.removeAt = function(index) {
		this.splice(index, 1);
	}
	Array.prototype.removeItem = function(obj) {
		var index = this.indexOf(obj);
		if (index >= 0)
			this.removeAt(index);
	}
	Array.prototype.insertAt = function(index, obj) {
		this.splice(index, 0, obj);
	}
	Array.prototype.addItem = function(obj) {
		this.splice(this.length, 0, obj);
	}
	Array.prototype.insertArr = function(index, arr) {
		for (var i = 0; i < arr.length; i++) {
			this.insertAt(index + i, arr[i]);
		}
	}
	Array.prototype.copy = function() {
		var arr = this.concat([]);
		return arr;
	}
	Array.prototype.find = function(key, value) {
		for (var i = 0; i < this.length; i++) {
			var obj = this[i];
			var tmpValue = obj[key];
			if (tmpValue == value) {
				return obj;
			}
		}
		return null;
	}
	Array.prototype.finds = function(key, value) {
		var arr = [];
		for (var i = 0; i < this.length; i++) {
			var obj = this[i];
			if (obj[key] == value) {
				arr.push(obj);
			}
		}
		return arr;
	}
}
Zxb.Init();
Zxb.Util = {
	isArray : function(v) {
		return Object.prototype.toString.apply(v) === '[object Array]';
	},
	isDate : function(v) {
		return Object.prototype.toString.apply(v) === '[object Date]';
	},
	isObject : function(v) {
		return !!v && Object.prototype.toString.call(v) === '[object Object]';
	},
	isPrimitive : function(v) {
		return Zxb.Util.isString(v) || Zxb.Util.isNumber(v)
				|| Zxb.Util.isBoolean(v);
	},
	isFunction : function(v) {
		return Object.prototype.toString.apply(v) === '[object Function]';
	},
	isNumber : function(v) {
		return typeof v === 'number' && isFinite(v);
	},
	isString : function(v) {
		return typeof v === 'string';
	},
	isBoolean : function(v) {
		return typeof v === 'boolean';
	},
	isElement : function(v) {
		return v ? !!v.tagName : false;
	},
	isDefined : function(v) {
		return typeof v !== 'undefined';
	},
	getInstanceId : function() {
		return Zxb.instanceId++;
	},
	emptyFun : function() {
	},
	isEmptyObj : function(obj) {
		for (var pro in obj)
			return false;
		return true;
	},
	applyConfig : function(src, config) {
		for (var pro in config) {
			if (!Zxb.Util.isObject(src[pro])) {
				src[pro] = config[pro];
			} else if (Zxb.Util.isObject(src[pro])) {
				this.applyConfig(src[pro], config[pro]);
			}
		}
	},
	clone : function(srcObj) {
		var destObj = {};
		for (var pro in srcObj) {
			destObj[pro] = srcObj[pro];
		}
		return destObj;
	},
	getZIndex : function(base) {
		Zxb.zIndex++
		if (base)
			return base + Zxb.zIndex;
		else
			return Zxb.zIndex;
	},
	isIE : function() {
		var userAgent = navigator.userAgent.toLowerCase();
		if (userAgent.indexOf("msie") != -1)
			return true;
		else
			return false;
	},
	toJsonObj : function(str) {
		var obj = {};
		if (str) {
			try {
				obj = eval('(' + str + ')');
			} catch (e) {
				obj = {};
			}
		}
		return obj;
	},
	obj2Str : function(obj, symbol) {
		var arr = [];
		for (var pro in obj) {
			if (!(obj[pro] instanceof Object))
				arr.push(pro + '=' + obj[pro]);
		}
		return arr.join(symbol);
	},
	getDataTypeStr : function(value) {
		for (var pro in Zxb.DataType) {
			if (Zxb.DataType[pro] == value)
				return pro;
		}
		return null;
	},
	genId : function() {
		var random = Math.round(Math.random() * 1000000)
		var timestamp = (new Date()).getTime();
		return timestamp + '' + random;
	},
	addZero : function(x) {
		return (x < 0 || x > 9 ? "" : "0") + x;
	},
	formatDate : function(date, format) {
		if (date == null) {
			return "";
		}
		if (!format)
			format = Zxb.Format.date;
		format = format + "";
		var result = "";
		var index = 0;
		var token = "";
		var c = "";
		var y = date.getYear() + "";
		var M = date.getMonth() + 1;
		var d = date.getDate();
		var E = date.getDay();
		var H = date.getHours();
		var m = date.getMinutes();
		var s = date.getSeconds();
		var yyyy, yy, MMM, MM, dd, hh, h, mm, ss, ampm, HH, H, KK, K, kk, k;
		var value = new Object();
		if (y.length < 4) {
			y = "" + (y - 0 + 1900);
		}
		value["y"] = "" + y;
		value["yyyy"] = y;
		value["yy"] = y.substring(2, 4);
		value["M"] = M;
		value["MM"] = Zxb.Util.addZero(M);
		value["MMM"] = Zxb.MONTH_NAMES[M - 1];
		value["NNN"] = Zxb.MONTH_NAMES[M + 11];
		value["d"] = d;
		value["dd"] = Zxb.Util.addZero(d);
		value["E"] = Zxb.DAY_NAMES[E + 7];
		value["EE"] = Zxb.DAY_NAMES[E];
		value["H"] = H;
		value["HH"] = Zxb.Util.addZero(H);
		if (H == 0) {
			value["h"] = 12;
		} else {
			if (H > 12) {
				value["h"] = H - 12;
			} else {
				value["h"] = H;
			}
		}
		value["hh"] = Zxb.Util.addZero(value["h"]);
		if (H > 11) {
			value["K"] = H - 12;
		} else {
			value["K"] = H;
		}
		value["k"] = H + 1;
		value["KK"] = Zxb.Util.addZero(value["K"]);
		value["kk"] = Zxb.Util.addZero(value["k"]);
		if (H > 11) {
			value["a"] = "PM";
		} else {
			value["a"] = "AM";
		}
		value["m"] = m;
		value["mm"] = Zxb.Util.addZero(m);
		value["s"] = s;
		value["ss"] = Zxb.Util.addZero(s);
		while (index < format.length) {
			token = format.charAt(index);
			c = "";
			while ((format.charAt(index) == token) && (index < format.length)) {
				c += format.charAt(index++);
			}
			if (value[c] != null) {
				result = result + value[c];
			} else {
				result = result + c;
			}
		}
		return result;
	},
	parseDate : function(value) {
		var date = null;
		if (Date.prototype.isPrototypeOf(value)) {
			date = value;
		} else {
			if (typeof(value) == "string") {
				date = new Date(Date.parse(value.replace(/-/g, "/")));
			} else {
				if (value != null && value.getTime) {
					date = new Date(value.getTime());
				}
			}
		}
		return date;
	},
	parseDate2 : function(dateString) {
		var ds = dateString.split(/[\\ \/:-]/g);
		var y = ds[0] * 1;
		var m = ds[1] * 1 - 1;
		var d = ds[2] * 1;
		if (y < 100)
			y = y + 1900;
		var leapyear = (y % 4 == 0 && y % 100 != 0 || y % 400 == 0);
		var numDays = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
		var n = numDays[m];
		if (m == 1 && leapyear)
			++n;
		if (d > n || d < 1) {
			throw new Error("时间格式错误！[" + y + "-" + (m + 1) + "-" + d + "]");
		}
		if (m > 11 || m < 0) {
			throw new Error("时间格式错误！[" + y + "-" + (m + 1) + "-" + d + "]");
		}
		return new Date(y, m, d);
	},
	getFloatFormat : function(formatStr_) {
		var initialStr_ = "", afterDecimalStr_ = "";
		var decimalLen_ = 0;
		var isInitial_ = false, hasOtherStr_ = false, hasDecimal_ = false, hasPartition_ = false;
		for (var i_ = 0; i_ < formatStr_.length; i_++) {
			var token_ = formatStr_.charAt(i_);
			if (!isInitial_) {
				if (token_ == "#" || token_ == "0") {
					isInitial_ = true;
				} else {
					initialStr_ += token_;
				}
			}
			if (isInitial_) {
				if (hasDecimal_) {
					if (token_ == "#" || token_ == "0") {
						decimalLen_++;
					} else {
						hasOtherStr_ = true;
					}
				} else {
					if (token_ == "#" || token_ == "0") {
					} else {
						if (token_ == ",") {
							hasPartition_ = true;
						} else {
							if (token_ == ".") {
								hasDecimal_ = true;
							} else {
								hasOtherStr_ = true;
							}
						}
					}
				}
			}
			if (hasOtherStr_) {
				afterDecimalStr_ += token_;
			}
		}
		var value = new Object();
		value.initialStr_ = initialStr_;
		value.afterDecimalStr_ = afterDecimalStr_;
		value.decimalLen_ = decimalLen_;
		value.hasPartition__ = hasPartition_;
		return value;
	},
	formatFloat : function(number, formatStr) {
		if (!formatStr)
			formatStr = Zxb.Format.number;
		var format_ = Zxb.Util.getFloatFormat(formatStr);
		var isDecimal_ = (number < 0);
		var initialStr_ = format_.initialStr_;
		var afterDecimalStr_ = format_.afterDecimalStr_;
		var decimalLen_ = format_.decimalLen_;
		var hasPartition__ = format_.hasPartition__;
		if (isDecimal_) {
			number *= (-1);
		}
		if (decimalLen_ > 0) {
			number = number * Math.pow(10, decimalLen_);
		}
		number = Math.round(number);
		var numberStr_ = number.toString();
		if (numberStr_.length < decimalLen_) {
			for (var i_ = decimalLen_ - numberStr_.length; i_ > 0; i_--) {
				numberStr_ = "0" + numberStr_;
			}
		}
		var floatStr_ = "";
		if (numberStr_.length > decimalLen_) {
			if (decimalLen_ > 0) {
				floatStr_ = "."
						+ numberStr_.substring(numberStr_.length - decimalLen_,
								numberStr_.length);
			}
			if (hasPartition__) {
				var digitNum_ = 0;
				for (var i_ = numberStr_.length - decimalLen_ - 1; i_ >= 0; i_--) {
					floatStr_ = numberStr_.charAt(i_) + floatStr_;
					digitNum_++;
					if (digitNum_ == 3 && i_ > 0) {
						floatStr_ = "," + floatStr_;
						digitNum_ = 0;
					}
				}
			} else {
				floatStr_ = numberStr_.substring(0, numberStr_.length
								- decimalLen_)
						+ floatStr_;
			}
		} else {
			floatStr_ = "0." + numberStr_;
		}
		if (isDecimal_) {
			floatStr_ = "-" + floatStr_;
		}
		return initialStr_ + floatStr_ + afterDecimalStr_;
	},
	getEventSrc : function(e) {
		return e.target || window.event.srcElement;
	}
}
Zxb.pub = {
	getStrLenByte : function(str) {
		var len = 0;
		if (str + "" == "undefined" || str == null) {
			len = 0;
		} else if (str.length == 0) {
			len = 0;
		}
		var iCVal;
		for (iCVal = 0; iCVal < str.length; iCVal++) {
			if (str.charCodeAt(iCVal) > 128) {
				len = len + 2;
			} else {
				len = len + 1;
			}
		}
		return len;
	},
	trim : function(str) {
		if (str + "" == "undefined" || str == null) {
			return "";
		} else if (str.length == 0) {
			return "";
		}
		var iCVal = 0;
		while (str.charAt(iCVal) == ' ') {
			iCVal++;
			if (iCVal >= str.length) {
				break;
			}
		}
		var j = str.length - 1;
		while (str.charAt(j) == ' ') {
			j--;
			if (j < 0) {
				break;
			}
		}
		if (j < iCVal)
			return "";
		else
			return str.substring(iCVal, j + 1);
	},
	preFillZero : function(str, tlength) {
		var reStr = Zxb.pub.trim(str);
		for (var iCVal = 0; iCVal < tlength - str.length; iCVal++) {
			reStr = "0" + reStr;
		}
		return reStr;
	},
	fillZeroLeft : function(str, tlength) {
		if (str == "" || str + "" == "undefined") {
			return "";
		} else {
			return Zxb.pub.preFillZero(str, tlength)
		}
	},
	isUndefined : function(paraValue) {
		if (paraValue == null || "undefined" == paraValue + "")
			return true;
		return false;
	},
	round : function(amount, dec) {
		if (Zxb.pub.isUndefined(dec))
			dec = 2;
		return Math.round(amount * Math.pow(10, dec)) / Math.pow(10, dec)
	},
	numberTrimByFormat : function(str, format, zeroRemove) {
		if (Zxb.pub._needSwitch(format)) {
			return Zxb.pub.numberTrimImpl(str, false, zeroRemove);
		} else
			return Zxb.pub.numberTrimImpl(str, true, zeroRemove);;
	},
	numberTrim : function(str, zeroRemove, needNotSwitch) {
		if (Zxb.pub._needSwitch(str) && !needNotSwitch) {
			return Zxb.pub.numberTrimImpl(str, false, zeroRemove);
		} else {
			return Zxb.pub.numberTrimImpl(str, true, zeroRemove);
		}
	},
	numberTrimImpl : function(str, needNotSwitch, zeroRemove) {
		// if (_needSwitch(str) && !needNotSwitch){
		if (!needNotSwitch) {
			str = Zxb.pub._exchangeCommaAndDot(str);
		}
		var fristvaliddigit = true;
		str = Zxb.pub.trim(str + "");
		var newStr = "";
		var count = 0;
		if (zeroRemove) {
			fristvaliddigit = false;
		}
		for (var iCVal = 0; iCVal < str.length; iCVal++) {
			ch = str.charAt(iCVal);
			if (iCVal == 0 && ch == '-') {
				// fristvaliddigit = true;
				newStr = newStr + ch;
				continue;
			}
			if (count == 0 && ch == '.') {
				count = 1;
				newStr = newStr + ch;
				continue;
			}
			if ((ch >= '0' && ch <= '9')) {
				if (!fristvaliddigit && ch != '0') {
					fristvaliddigit = true;
				} else if (!fristvaliddigit && (ch == 0)
						&& str.charAt(iCVal + 1) == '.') {
					fristvaliddigit = true;
				}
				if (fristvaliddigit)
					newStr = newStr + ch;
			}
		}
		if (zeroRemove && str.length != 0 && (newStr == '' || newStr == '-')) {
			return 0;
		}
		return newStr;
	},
	trimComma : function(str) {
		var newStr = "";
		for (var iCVal = 0; iCVal < str.length; iCVal++) {
			ch = str.charAt(iCVal);
			if (ch == ',')
				continue;
			newStr = newStr + ch;
		}
		return newStr;
	},
	formatNum : function(pSrcStr, nAfterDot, bSeparator) {
		var nSeparator = 0;
		if (bSeparator)
			nSeparator = 3;
		return Zxb.pub.formatNumberImpl(pSrcStr, nAfterDot, nSeparator, "");
	},
	formatNumberImpl : function(pSrcStr, nAfterDot, nSeparator, endNumber,
			removeAfterDot) {
		// if(pSrcStr!="")
		// pSrcStr=parseFloat(pSrcStr);
		var srcStr;
		var strAfterDot = "";
		var strBeforeDot = "";
		var tmpString = "";
		var resultStr;
		var strLen = 0;
		var tmpLen = 0;
		var flag = "";
		// when pSrcStr equalto null or empty,return empty
		if (pSrcStr == null || pSrcStr + "" == "") {
			pSrcStr = "";
			return pSrcStr;
		}
		// when the first digit is flag , clear it
		srcStr = "" + pSrcStr + "";
		if (parseFloat(pSrcStr) < 0) {
			flag = srcStr.substring(0, 1);
			srcStr = srcStr.substring(1, srcStr.length);
		}
		// deal to the decimal part by the parameter 'nAfterDot'
		strLen = srcStr.length;
		dotPos = srcStr.indexOf(".");
		if (!CheckNumber(removeAfterDot)) {
			removeAfterDot = 0;
		}
		// when the decimal is not exit in the number
		if (dotPos == -1) {
			strBeforeDot = srcStr + "";
			for (var iCVal = 0; iCVal < nAfterDot - removeAfterDot; iCVal++) {
				strAfterDot = strAfterDot + "0";
			}
		} else {
			// when the decimal is exit in the number
			strAfterDot = srcStr.substring(dotPos + 1, strLen);
			strBeforeDot = srcStr.substring(0, dotPos);
			// when the decimal part over the count of the decimal in the format
			// standard
			if ((strLen - dotPos - 1) > nAfterDot) {
				nAfter = dotPos + nAfterDot + 1;
				nTen = 1;
				for (var j = 0; j < nAfterDot; j++) {
					nTen = nTen * 10;
				}
				// round the number
				if ((parseFloat("." + strAfterDot) + "").substring(2).length <= nAfterDot
						+ 1)
					strAfterDot = Math.round(Math.round(parseFloat("."
							+ strAfterDot)
							* nTen * 10)
							/ 10)
							/ nTen;
				else
					strAfterDot = Math.round(parseFloat("." + strAfterDot)
							* nTen)
							/ nTen;
				strAfterDot = strAfterDot + "";
				if (strAfterDot.length < 2 + nAfterDot) {
					if (strAfterDot == "1") {
						strBeforeDot = (parseFloat(strBeforeDot) + 1) + "";
						strAfterDot = "0";
					}
					strAfterDot = strAfterDot.substring(2);
					var Len = strAfterDot.length;
					for (var iCVal = 0; iCVal < nAfterDot - Len; iCVal++) {
						strAfterDot = strAfterDot + "0";
					}
				} else
					strAfterDot = strAfterDot.substring(2, 2 + nAfterDot);
			} else {
				// when the decimal part less than the count of the decimal in
				// the
				// format standard
				resultStr = srcStr;
				// add zero to the end
				for (var iCVal = 0; iCVal < (nAfterDot - strLen + dotPos + 1 - removeAfterDot); iCVal++) {
					strAfterDot = strAfterDot + "0";
				}
			}
		}
		// deal to the separator by the parameter 'nSeparator'
		// when separator be exit
		if (nSeparator != 0) {
			if (strBeforeDot == null || strBeforeDot == "") {
				strBeforeDot = "0";
			} else {
				tmpLen = strBeforeDot.length;
				if (tmpLen <= nSeparator) {
					tmpString = "";
				} else {
					tmpString = strBeforeDot;
					strBeforeDot = "";
					// separate the part of before dot by ','
					while (tmpLen >= nSeparator + 1) {
						if (strBeforeDot == "") {
							strBeforeDot = tmpString.substring(tmpLen
											- nSeparator, tmpLen);
						} else {
							strBeforeDot = tmpString.substring(tmpLen
											- nSeparator, tmpLen)
									+ "," + strBeforeDot;
						}
						tmpString = tmpString.substring(0, tmpLen - nSeparator);
						tmpLen = tmpString.length;
					}
					if (tmpLen >= 1) {
						strBeforeDot = tmpString + "," + strBeforeDot;
					}
				}
			}
		}
		if (nAfterDot == 0)
			resultStr = flag + strBeforeDot;
		else if (removeAfterDot != 0 && strAfterDot == "") {
			resultStr = flag + strBeforeDot;
		} else
			resultStr = flag + strBeforeDot + "." + strAfterDot + endNumber;
		return resultStr;
	},
	formatNumber : function(str, format) {
		var srcStr = str;
		var dotIndex;
		var sepIndex = -1;
		var nAfterDot;
		var separator;
		var nSeparator;
		var newSeparator = "";
		var endNumber = "";
		// get the indexes of dot and separator
		dotIndex = format.indexOf(".");
		var arryStr = format.split(",");
		for (var j = 0; j < arryStr.length - 1; j++) {
			sepIndex = sepIndex + arryStr[j].length + 1
		}
		// get the decimal part after the dot
		var removeAfterDot = 0;
		if (dotIndex == -1) {
			nAfterDot = 0;
		} else {
			afterDot = format.substring(dotIndex + 1, format.length);
			nAfterDot = afterDot.length;
			if (afterDot.indexOf('#') != -1)
				removeAfterDot = afterDot.length - afterDot.indexOf('#');
		}
		// get the position of separator
		if (sepIndex == -1) {
			nSeparator = 0;
		} else {
			if (dotIndex != -1) {
				separator = format.substring(sepIndex + 1, dotIndex);
			} else {
				separator = format.substring(sepIndex + 1, format.length);
			}
			for (var iCVal = 0; iCVal < separator.length; iCVal++) {
				ch = separator.charAt(iCVal);
				if (ch == '#' || ch == '0')
					newSeparator = newSeparator + ch;
				if (ch > '0' && ch <= '9')
					endNumber = endNumber + ch;
			}
			nSeparator = newSeparator.length;
		}
		// call formatNumber function to format number
		return Zxb.pub.formatNumberImpl(srcStr, nAfterDot, nSeparator,
				endNumber, removeAfterDot);
	},
	formatDate : function(DateToFormat, FormatAs) {
		if (DateToFormat == "") {
			return "";
		}
		// set default format
		if (FormatAs + "" == "undefined" || FormatAs == null) {
			FormatAs = DATE_DEFAULT_FORMAT;
		}
		// declare variable
		var strReturnDate;
		var YEAR;
		var MONTH;
		var DAY;
		FormatAs = FormatAs.toLowerCase();
		DateToFormat = DateToFormat.toLowerCase();
		var arrDate
		var arrMonths = new Array("January", "February", "March", "April",
				"May", "June", "July", "August", "September", "October",
				"November", "December");
		var strMONTH;
		var Separator = "/";
		// when the old date is 8 numbers
		if (Zxb.check.isNumber(DateToFormat)) {
			if (DateToFormat.length == 8) {
				DAY = DateToFormat.substring(0, 2);
				MONTH = DateToFormat.substring(2, 4);
				YEAR = DateToFormat.substring(4, 8);
			}
			if (DateToFormat.length == 6) {
				if (FormatAs == "mm/yyyy") {
					MONTH = DateToFormat.substring(0, 2);
					YEAR = DateToFormat.substring(2);
					return MONTH + "/" + YEAR;
				} else if (FormatAs == "mm.yyyy") {
					MONTH = DateToFormat.substring(0, 2);
					YEAR = DateToFormat.substring(2);
					return MONTH + "." + YEAR;
				} else {
					DAY = DateToFormat.substring(0, 2);
					MONTH = DateToFormat.substring(2, 4);
					YEAR = SERVER_SYSTEM_DATE.substring(6, 8)
							+ DateToFormat.substring(4, 6);
				}
			}
		} else {
			while (DateToFormat.indexOf("st") > -1) {
				DateToFormat = DateToFormat.replace("st", "");
			}
			while (DateToFormat.indexOf("nd") > -1) {
				DateToFormat = DateToFormat.replace("nd", "");
			}
			while (DateToFormat.indexOf("rd") > -1) {
				DateToFormat = DateToFormat.replace("rd", "");
			}
			while (DateToFormat.indexOf("th") > -1) {
				DateToFormat = DateToFormat.replace("th", "");
			}
			// get separator of the old date
			if (DateToFormat.indexOf(".") > -1) {
				Separator = ".";
			}
			if (DateToFormat.indexOf("-") > -1) {
				Separator = "-";
			}
			if (DateToFormat.indexOf("/") > -1) {
				Separator = "/";
			}
			if (DateToFormat.indexOf(" ") > -1) {
				Separator = " ";
			}
			// get year,month and day value
			arrDate = DateToFormat.split(Separator);
			DateToFormat = "";
			for (var iSD = 0; iSD < arrDate.length; iSD++) {
				if (arrDate[iSD] != "") {
					DateToFormat += arrDate[iSD] + Separator;
				}
			}
			DateToFormat = DateToFormat.substring(0, DateToFormat.length - 1);
			arrDate = DateToFormat.split(Separator);
			if (arrDate.length < 3) {
				if (FormatAs == "mm/yyyy") {
					return DateToFormat;
				} else if (FormatAs == "mm.yyyy") {
					return DateToFormat;
				} else {
					return "";
				}
			}
			YEAR = arrDate[0];
			MONTH = arrDate[1];
			DAY = arrDate[2];
			if (parseFloat(arrDate[1]) > 12) {
				DAY = arrDate[1];
				MONTH = arrDate[2];
			}
			if (parseFloat(DAY) && DAY.toString().length == 4) {
				YEAR = arrDate[2];
				DAY = arrDate[0];
				MONTH = arrDate[1];
			}
			for (var iSD = 0; iSD < arrMonths.length; iSD++) {
				var ShortMonth = arrMonths[iSD].substring(0, 3).toLowerCase();
				var MonthPosition = DateToFormat.indexOf(ShortMonth);
				if (MonthPosition > -1) {
					MONTH = iSD + 1;
					if (MonthPosition == 0) {
						DAY = arrDate[1];
						YEAR = arrDate[2];
					}
					break;
				}
			}
		}
		var strTemp = YEAR.toString();
		var shYEAR;
		var shMONTH;
		var shDAY;
		// deal the short year ,short month and short day
		if (strTemp.length == 2) {
			shYEAR = YEAR;
			if (parseFloat(YEAR) > 40) {
				YEAR = "19" + YEAR;
			} else {
				YEAR = "20" + YEAR;
			}
		}
		if (strTemp.length == 4) {
			shYEAR = strTemp.substring(2, 4);
		}
		if (parseInt(MONTH) < 10) {
			if (MONTH.toString().length < 2) {
				shMONTH = MONTH;
				MONTH = "0" + MONTH;
			} else {
				shMONTH = parseInt(MONTH);
			}
		}
		if (parseInt(DAY) < 10) {
			if (DAY.toString().length < 2) {
				shDAY = DAY;
				DAY = "0" + DAY;
			} else {
				shDAY = parseInt(DAY);
			}
		}
		// return the formated date string by the FormatAs
		switch (FormatAs) {
			case "yyyy-mm-dd" :
				return YEAR + "-" + MONTH + "-" + DAY;
			case "yyyy-m-dd" :
				return YEAR + "-" + shMONTH + "-" + DAY;
			case "yyyy-mm-d" :
				return YEAR + "-" + MONTH + "-" + shDAY;
			case "yy-mm-dd" :
				return shYEAR + "-" + MONTH + "-" + DAY;
			case "yyyy-m-d" :
				return YEAR + "-" + shMONTH + "-" + shDAY;
			case "yy-m-d" :
				return shYEAR + "-" + shMONTH + "-" + shDAY;
			case "yyyy/mm/dd" :
				return YEAR + "/" + MONTH + "/" + DAY;
			case "yyyy.mm.dd" :
				return YEAR + "." + MONTH + "." + DAY;
			case "dd/mm/yyyy" :
				return DAY + "/" + MONTH + "/" + YEAR;
			case "dd-mm-yyyy" :
				return DAY + "-" + MONTH + "-" + YEAR;
			case "dd.mm.yyyy" :
				return DAY + "." + MONTH + "." + YEAR;
			case "mm/dd/yyyy" :
				return MONTH + "/" + DAY + "/" + YEAR;
			case "dd/mmm/yyyy" :
				return DAY + " " + arrMonths[MONTH - 1].substring(0, 3) + " "
						+ YEAR;
			case "mmm/dd/yyyy" :
				return arrMonths[MONTH - 1].substring(0, 3) + " " + DAY + " "
						+ YEAR;
			case "dd/mmmm/yyyy" :
				return DAY + " " + arrMonths[MONTH - 1] + " " + YEAR;
			case "mmmm/dd/yyyy" :
				return arrMonths[MONTH - 1] + " " + DAY + " " + YEAR;
		}
		return DAY + Separator + MONTH + Separator + YEAR;
	},
	getGenderFromCnId : function(id_value) {
		var gender;
		var id_gender;
		if (id_value.length == 15)
			id_gender = id_value.substring(14);
		else if (id_value.length == 18)
			id_gender = id_value.substring(16, 17);
		else
			return "";
		if (id_gender == "1" || id_gender == "3" || id_gender == "5"
				|| id_gender == "7" || id_gender == "9") {
			gender = "M";
		} else {
			gender = "F";
		}
		return gender;
	},
	getBirthdayFromCnId : function(id) {
		try {
			var birthday;
			if (id.length == 15)
				birthday = "19" + id.substring(6, 8) + "-"
						+ id.substring(8, 10) + "-" + id.substring(10, 12);
			else if (id.length == 18)
				birthday = id.substring(6, 10) + "-" + id.substring(10, 12)
						+ "-" + id.substring(12, 14);
			else
				birthday = "";
			return birthday
		} catch (e) {
			return "";
		}
	},
	addNumber : function(num1, num2, float_length) {
		var nTemp = 10;
		if (float_length == 0) {
			nTemp = 1;
		} else {
			nTemp = 10 * float_length;
		}
		var nSum = new Number(num1) * nTemp + new Number(num2) * nTemp;
		nSum = nSum / nTemp;
		var sSum = new String(nSum);
		var j = sSum.indexOf(".");
		if (j >= 0) {
			sSum = sSum.substring(0, j + float_length + 1);
			nSum = new Number(sSum);
		}
		return nSum;
	},
	_exchangeCommaAndDot : function(str) {
		var tmpStr = str.replace(/[.]/g, "__comma__");
		tmpStr = tmpStr.replace(/[,]/g, "__dot__");
		tmpStr = tmpStr.replace(/__comma__/g, ",");
		tmpStr = tmpStr.replace(/__dot__/g, ".");
		return tmpStr;
	},
	_needSwitch : function(str) {
		str = str + "";
		var parts = str.split('.');
		var idx = str.indexOf('.');
		if (parts.length > 2 || (idx > 0 && idx < str.indexOf(','))) {
			return true;
		}
		return false;
	},
	setNumberFormat : function() {
		var rc = true;
		var control = window.event.srcElement;
		var format = control.format;
		var value = control.value;
		var needTrans = false;
		if (Zxb.pub._needSwitch(format)) {
			format = Zxb.pub._exchangeCommaAndDot(format);
			needTrans = true;
		}
		if (needTrans || Zxb.pub._needSwitch(value)) {
			value = Zxb.pub._exchangeCommaAndDot(value);
			needTrans = true;
		}
		if (checkCurrency(value)) {
			var str = Zxb.pub.numberTrim(value, false, true);
			var reValue = Zxb.pub.formatNumber(str, format);
			control.value = reValue;
			if (needTrans) {
				control.value = Zxb.pub._exchangeCommaAndDot(reValue);
			}
			control.originalValue = control.value;
		}
		return rc;
	},
	setIntegerFormat : function() {
		var rc = true;
		var control = window.event.srcElement;
		if (checkInteger(control.value)) {
			var str = Zxb.pub.numberTrim(control.value);
			var reValue = Zxb.pub.formatNumber(str, control.format);
			control.value = reValue;
		}
		return rc;
	},
	setDateFormat : function() {
		var rc = true;
		var control = window.event.srcElement;
		if (control.format != "MMyyyy") {
			var format = control.format;
			if (control.format == null || control.format + "" == "undefined")
				format = "dd/MM/yyyy";
			if (Zxb.check.checkDate(control.value, format)) {
				var reValue = Zxb.pub.formatDate(control.value, control.format);
				control.value = reValue;
			}
		}
		return rc;
	},
	setDateTimeFormat : function() {
		var rc = true;
		var control = window.event.srcElement;
		var format = control.format;
		if (control.format == null || control.format + "" == "undefined") {
			format = "dd/MM/yyyy HH:mm";
		}
		return rc;
	},
	setCurrencyFormat : function() {
		var rc = true;
		var control = window.event.srcElement;
		var format = control.format;
		var value = control.value;
		var needTrans = false;
		if (_needSwitch(format)) {
			format = Zxb.pub._exchangeCommaAndDot(format);
			needTrans = true;
		}
		if (needTrans || Zxb.pub._needSwitch(value)) {
			value = Zxb.pub._exchangeCommaAndDot(value);
			needTrans = true;
		}
		if (Zxb.check.checkCurrency(value)) {
			var str = Zxb.pub.numberTrim(value, true, true);
			var reValue = Zxb.pub.formatNumber(str, format);
			control.value = reValue;
			if (needTrans) {
				control.value = Zxb.pub._exchangeCommaAndDot(reValue);
			}
		}
		return rc;
	},
	getPureCode : function(code) {
		var pureCode;
		if (code.indexOf("-") >= 0) {
			pureCode = code.substring(0, code.indexOf("-"))
		} else {
			pureCode = code;
		}
		return pureCode;
	},
	createXMLHTTP : function() {
		var arrSignatures = ['MSXML2.XMLHTTP.5.0', 'MSXML2.XMLHTTP.4.0',
				'MSXML2.XMLHTTP.3.0', 'MSXML2.XMLHTTP', 'Microsoft.XMLHTTP'];
		for (var i = 0; i < arrSignatures.length; i++) {
			try {
				var oRequest = new ActiveXObject(arrSignatures[i]);
				return oRequest;
			} catch (oError) {
			}
		}
		alert("MSXML is not installed on your system");
		throw new Error("MSXML is not installed on your system");
	},
	LoadXml : function(url, showError) {
		try {
			var oXML = new createXMLHTTP()
			oXML.open("GET", url, false, "", "");
			try {
				oXML.send(null);
			} catch (e) {
				if (showError && showError == true) {
					alert('Some error occured,please contact administrator');
				}
				return null;
			}
			var sXML = oXML.responseText;
			var docElem = LoadXmlStr(sXML, showError);
			var denyUrl = docElem.selectSingleNode("DenyUrl");
			if (denyUrl != null) {
				alert('Access Deny : \n' + denyUrl.text);
				return null;
			}
			return docElem;
		} catch (e) {
			alert(e.name + " - " + e.message);
			return null;
		}
	},
	LoadJSON : function(url, showError) {
		var oXML = new createXMLHTTP()
		oXML.open("GET", encodeURI(url), false, "", "");
		try {
			oXML.send(null);
		} catch (e) {
			if (showError && showError == true) {
				alert('Some error occured,please contact administrator');
			}
			return null;
		}
		return oXML.responseText;
	},
	LoadXmlStr : function(xml, showError) {
		try {
			var oXML = new ActiveXObject("Microsoft.XMLDOM");
			oXML.async = false;
			oXML.loadXML(xml);
			if (oXML.documentElement == null) {
				if (showError && showError == true) {
					alert('Load invalid xml string!');
				}
				return null;
			}
			var node = oXML.documentElement;
			return node;
		} catch (e) {
			alert(e.name + " - " + e.message);
			return null;
		}
	},
	checkAll : function(controlName, elementName) {
		for (var iCVal = 0; iCVal < document.all.length; iCVal++) {
			if (document.all.item(iCVal).id.indexOf(elementName) != -1) {
				var obj = document.all.item(iCVal);
				for (var j = 0; j < obj.getElementsByTagName('input').length; j++) {
					if (obj.getElementsByTagName('input')[j].type == 'checkbox'
							&& obj.getElementsByTagName('input')[j].disabled != true) {
						if (controlName.checked)
							obj.getElementsByTagName('input')[j].checked = true;
						else
							obj.getElementsByTagName('input')[j].checked = false;
					}
				}
			}
		}
	},
	addAmount : function(num1, num2) {
		return this.addNumber(num1, num2, 2);
	},
	addNumber : function(num1, num2, float_length) {
		var nSum = new Number(num1) * 100 + new Number(num2) * 100;
		// nSum = Math.round(nSum);
		nSum = nSum / 100.00;
		var sSum = new String(nSum);
		var j = sSum.indexOf(".");
		if (j >= 0) {
			sSum = sSum.substring(0, j + float_length + 1);
			nSum = new Number(sSum);
		}
		return nSum;
	},
	processPercent : function() {
		var srcObj = window.event.srcElement;
		var hiddenObj;
		// alert(srcObj.previousSibling.type);
		if (srcObj.previousSibling.type == "hidden") {
			hiddenObj = srcObj.previousSibling;
		} else {
			hiddenObj = srcObj.previousSibling.previousSibling;
		}
		if (srcObj.value == "") {
			hiddenObj.value = "";
		} else {
			var exponent = 0;
			if (srcObj.format) {
				var dotIndex;
				if (_needSwitch(srcObj.format)) {
					dotIndex = srcObj.format.indexOf(",");
				} else {
					dotIndex = srcObj.format.indexOf(".");
				}
				if (dotIndex != -1) {
					exponent = srcObj.format.length - dotIndex - 1;
				}
			}
			// hiddenObj.value = Math.round(parseFloat(srcObj.value) *
			// Math.pow(10,
			// exponent)) / Math.pow(10, exponent +2);
			hiddenObj.value = Math.round(parseFloat(srcObj.originalValue)
					* Math.pow(10, exponent))
					/ Math.pow(10, exponent + 2);
		}
	},
	processPolicy : function() {
		var srcObj = window.event.srcElement;
		var hiddenObj;
		if (srcObj.previousSibling.type == "hidden") {
			hiddenObj = srcObj.previousSibling;
		} else {
			hiddenObj = srcObj.previousSibling.previousSibling;
		}
		if (srcObj.value == "") {
			hiddenObj.value = "";
		} else {
			var jsPrjRelated = getJsPrjRelated();
			if (jsPrjRelated != null && jsPrjRelated != '') {
				var hiddenFormat = 'formatPolicyHidden' + jsPrjRelated;
				var textFormat = 'formatPolicy' + jsPrjRelated;
				if (funFactory.hasFunction(hiddenFormat)) {
					hiddenObj.value = funFactory[hiddenFormat](srcObj);
				} else {
					hiddenObj.value = Zxb.pubformatPolicyHidden(srcObj);
				}
				if (funFactory.hasFunction(textFormat)) {
					srcObj.value = funFactory[textFormat](srcObj.value);
				} else {
					srcObj.value = Zxb.pubformatPolicy(srcObj.value);
				}
			} else {
				hiddenObj.value = Zxb.pubformatPolicyHidden(srcObj);
				srcObj.value = Zxb.pub.formatPolicy(srcObj.value);
			}
		}
	},
	formatPolicyHidden : function(srcObj) {
		return Zxb.pub.preFillZero(strTrim(srcObj.value, "-"), 10);
	},
	processApply : function() {
		var srcObj = window.event.srcElement;
		var hiddenObj;
		if (srcObj.previousSibling.type == "hidden") {
			hiddenObj = srcObj.previousSibling;
		} else {
			hiddenObj = srcObj.previousSibling.previousSibling;
		}
		if (srcObj.value == "") {
			hiddenObj.value = "";
		} else {
			hiddenObj.value = Zxb.pubstrTrim(formatApply(srcObj.value), "/");
			srcObj.value = Zxb.pubformatApply(srcObj.value);
		}
	},
	processHeight : function() {
		var srcObj = window.event.srcElement;
		var hiddenObj;
		if (srcObj.previousSibling.type == "hidden") {
			hiddenObj = srcObj.previousSibling;
		} else {
			hiddenObj = srcObj.previousSibling.previousSibling;
		}
		if (srcObj.value == "") {
			hiddenObj.value = "";
		} else {
			var exponent = 0;
			if (srcObj.format) {
				var dotIndex;
				if (_needSwitch(srcObj.format)) {
					dotIndex = srcObj.format.indexOf(",");
				} else {
					dotIndex = srcObj.format.indexOf(".");
				}
				if (dotIndex != -1) {
					exponent = srcObj.format.length - dotIndex - 1;
				}
			}
			hiddenObj.value = Math.round(parseFloat(srcObj.originalValue)
					* Math.pow(10, 2 + exponent))
					/ Math.pow(10, exponent);
		}
	},
	processPostCode : function() {
		var srcObj = window.event.srcElement;
		var hiddenObj;
		if (srcObj.previousSibling.type == "hidden") {
			hiddenObj = srcObj.previousSibling;
		} else {
			hiddenObj = srcObj.previousSibling.previousSibling;
		}
		if (srcObj.value == "") {
			hiddenObj.value = "";
		} else {
			var value = srcObj.value;
			hiddenObj.value = value;
			if (typeof srcObj.format != "undefined") {
				var format = srcObj.format;
				srcObj.value = transCode(value, format);
			} else {
				srcObj.value = value;
			}
		}
	},
	transCode : function(value, format) {
		if (typeof value == "undefined" || value == "") {
			return value;
		}
		var result = "";
		var formatRegs = format.split('%');
		var valueIndex = 0;
		var reg;
		var num;
		var rightParen;
		if (formatRegs.length > 0)
			result += formatRegs[0];
		for (var i = 1; i < formatRegs.length; i++) {
			reg = formatRegs[i];
			if (typeof reg == "undefined" || reg == "")
				continue;
			if (reg.charAt(0) == 'd') {
				rightParen = reg.indexOf("}");
				num = reg.substring(reg.indexOf("{") + 1, rightParen);
				result = result + value.substring(valueIndex, valueIndex + num);
				if (rightParen + 1 < reg.length)
					result = result + reg.substring(rightParen + 1);
				valueIndex += num;
			} else {
				result = result + reg;
			}
		}
		return result;
	},
	formatPolicy : function(value) {
		var valueTrim = strTrim(value, "-");
		valueTrim = Zxb.pub.preFillZero(valueTrim, "10");
		return valueTrim.substring(0, 9) + "-" + valueTrim.substring(9);
	},
	formatApply : function(value) {
		/* to call project related formatApply() */
		var jsPrjRelated = getJsPrjRelated();
		if (jsPrjRelated != null && jsPrjRelated != '') {
			var prjRelatedCall = 'formatApply' + jsPrjRelated;
			if (funFactory.hasFunction(prjRelatedCall)) {
				try {
					return funFactory[prjRelatedCall](value);
				} catch (e) {
					alert(prjRelatedCall + '()  error;' + '\n Caused by: \n  '
							+ e.message);
					return value;
				}
			}
		}
		/* end */
		var valueTrim = strTrim(value, "/");
		var preStr = valueTrim.substring(0, valueTrim.length - 7);
		if (preStr.length < 3) {
			var len = preStr.length;
			for (var iCVal = 0; iCVal < 3 - len; iCVal++) {
				preStr = preStr + " ";
			}
		}
		return preStr
				+ "/"
				+ valueTrim.substring(valueTrim.length - 7, valueTrim.length
								- 2) + "/"
				+ valueTrim.substring(valueTrim.length - 2);
	},
	strTrim : function(str, sepStr) {
		var arryStr = str.split(sepStr);
		var reStr = "";
		for (var iCVal = 0; iCVal < arryStr.length; iCVal++) {
			reStr = reStr + arryStr[iCVal];
		}
		return reStr;
	},
	getCurrentDate : function() {
		var arryDate = SERVER_SYSTEM_DATE.split("/");
		var date = new Date(arryDate[2], arryDate[1] - 1, arryDate[0]);
		return date;
	},
	setPolicyCode : function(policyTagName, value) {
		var hiddenobj = document.getElementById(policyTagName);
		var obj = document.getElementById(policyTagName + "_text");
		hiddenobj.value = preFillZero(value, '10');
		obj.value = Zxb.pub.formatPolicy(value);
	},
	setApplyCode : function(applyTagName, value) {
		var hiddenobj = document.getElementById(applyTagName);
		var obj = document.getElementById(applyTagName + "_text");
		hiddenobj.value = strTrim(formatApply(value), "/");
		obj.value = Zxb.pub.formatApply(value);
	},
	textCounter : function() {
		var obj = window.event.srcElement;
		if (obj.value.length > obj.maxLength)
			obj.value = obj.value.substring(0, obj.maxLength);
	},
	daysBetweenStr : function(date1, date2) {
		var arryDate1 = date1.split("/");
		var d1 = new Date(arryDate1[2], arryDate1[1] - 1, arryDate1[0]);
		var arryDate2 = date2.split("/");
		var d2 = new Date(arryDate2[2], arryDate2[1] - 1, arryDate2[0]);
		return daysBetween(d1, d2);
	},
	daysBetween : function(date1, date2) {
		var DSTAdjust = 0;
		// constants used for our calculations below
		oneMinute = 1000 * 60;
		var oneDay = oneMinute * 60 * 24;
		// equalize times in case date objects have them
		date1.setHours(0);
		date1.setMinutes(0);
		date1.setSeconds(0);
		date2.setHours(0);
		date2.setMinutes(0);
		date2.setSeconds(0);
		// take care of spans across Daylight Saving Time changes
		if (date2 > date1) {
			DSTAdjust = (date2.getTimezoneOffset() - date1.getTimezoneOffset())
					* oneMinute;
		} else {
			DSTAdjust = (date1.getTimezoneOffset() - date2.getTimezoneOffset())
					* oneMinute;
		}
		var diff = Math.abs(date2.getTime() - date1.getTime()) - DSTAdjust;
		return Math.ceil(diff / oneDay);
	},
	setButtonsDisabled : function(sDisbld) {
		var objs = document.getElementsByTagName('Input');
		for (var j = 0; j < objs.length; j++) {
			if (objs[j].type == "button" || objs[j].type == "submit") {
				objs[j].disabled = sDisbld;
			}
		}
	},
	returnHome : function(contextPath) {
		var scontextPath = contextPath;
		if (returnHome.arguments.length == 0 || contextPath == 'undefined') {
			scontextPath = _getContextPath();
		}
		window.open(scontextPath + "/mainMenu.do", "_top");
	},
	goHome : function() {
		this.returnHome();
	},
	goHomeWithConfirm : function(contextPath) {
		var sContextPath = _getContextPath(contextPath);
		if (EXIT_BUTTON_WITH_CONFIRM == 'true') {
			if (_isConfirm()) {
				returnHome(sContextPath);
			}
		} else {
			returnHome(sContextPath);
		}
	},
	convertToDate : function(sDate, sFormat, sLocale) {
		contextPath = _getContextPath();
		if (sFormat == undefined) {
			sFormat = '';
		}
		if (sLocale == undefined) {
			sLocale = '';
		}
		if (sDate == undefined) {
			return null;
		}
		var rootEle = LoadXml(contextPath + '/getDateInfo.do?sDate=' + sDate
				+ '&sFormat=' + sFormat + '&sLocale=' + sLocale
				+ '&entryflag=js');
		if (rootEle != null) {
			var yearNode = rootEle.selectSingleNode('Year');
			var sYear = yearNode.text;
			var monthNode = rootEle.selectSingleNode('Month');
			var sMonth = monthNode.text;
			var dateNode = rootEle.selectSingleNode('Date');
			var sDate = dateNode.text;
			var oDate = new Date();
			oDate.setYear(sYear);
			oDate.setMonth(sMonth);
			oDate.setDate(sDate);
			return oDate;
		} else {
			return null;
		}
	},
	/* ================functions for currency tag with hidden style============= */
	processCurrency : function() {
		var srcObj = window.event.srcElement;
		var hiddenObj;
		if (srcObj.previousSibling.type == "hidden") {
			hiddenObj = srcObj.previousSibling;
		} else {
			hiddenObj = srcObj.previousSibling.previousSibling;
		}
		if (srcObj.value == "") {
			hiddenObj.value = "";
		} else {
			var format = srcObj.format;
			var value = srcObj.value;
			var needNotTrans = false;
			if (_needSwitch(format)) {
				value = Zxb.pub._exchangeCommaAndDot(value);
				needNotTrans = true;
			}
			hiddenObj.value = Zxb.pub.numberTrim(value, false, needNotTrans);
		}
	},
	setCurrencyValue : function(tagName, value) {
		var valueObj = document.getElementById(tagName);
		var textObj = document.getElementById(tagName + "_text");
		if (valueObj.type == "hidden") {
			if (Zxb.check.checkCurrency(value)) {
				valueObj.value = value;
				textObj.value = Zxb.pub.formatNumber(valueObj.value,
						textObj.format);
			}
		}
	},
	setCurrencyValueUseControl : function(controlObj, value) {
		var valueObj = controlObj;
		var textObj = valueObj.nextSibling;
		if (valueObj.type == "hidden" && textObj.type == "text"
				&& valueObj.name + "_text" == textObj.name) {
			if (Zxb.check.checkCurrency(value)) {
				valueObj.value = value;
				textObj.value = Zxb.pub.formatNumber(valueObj.value,
						textObj.format);
			}
		}
	},
	setCurrencyDisable : function(tagName, flag) {
		var valueObj = document.getElementById(tagName);
		var textObj = document.getElementById(tagName + "_text");
		if (valueObj.type == "hidden") {
			if (flag == true) {
				valueObj.disabled = true;
				textObj.disabled = true;
			} else if (flag == false) {
				valueObj.disabled = false;
				textObj.disabled = false;
			}
		}
	},
	setCurrencyDisableUseControl : function(controlObj, flag) {
		var valueObj = controlObj;
		var textObj = valueObj.nextSibling;
		if (valueObj.type == "hidden" && textObj.type == "text"
				&& valueObj.name + "_text" == textObj.name) {
			if (flag == true) {
				valueObj.disabled = true;
				textObj.disabled = true;
			} else if (flag == false) {
				valueObj.disabled = false;
				textObj.disabled = false;
			}
		}
	},
	setCurrencyReadOnly : function(controlObj, flag) {
		var valueObj = controlObj;
		var textObj = valueObj.nextSibling;
		if (valueObj.type == "hidden" && textObj.type == "text"
				&& valueObj.name + "_text" == textObj.name) {
			if (flag == true) {
				// valueObj.readOnly = true;
				textObj.readOnly = true;
			} else if (flag == false) {
				// valueObj.readOnly = false;
				textObj.readOnly = false;
			}
		}
	},
	setCurrencyFocus : function(controlObj) {
		var valueObj = controlObj;
		var textObj = valueObj.nextSibling;
		if (valueObj.type == "hidden" && textObj.type == "text"
				&& valueObj.name + "_text" == textObj.name) {
			// valueObj.focus = true;
			textObj.focus();
		}
	},
	_getContextPath : function(contextPath) {
		if (contextPath != undefined && contextPath != 'undefined') {
			return contextPath;
		} else {
			var contextEle = document.getElementById("contextPathValue");
			if (contextEle == null) {
				return GLOBAL_CONTEXT_PATH;
			}
			return contextEle.value;
		}
	},
	_changeParentClass : function(sClassName) {
		var oele = window.event.srcElement;
		while (oele.parentNode.tagName != 'TR') {
			oele = oele.parentNode;
		}
		oele.parentNode.className = sClassName;
	},
	/**
	 * @param: str is unicode string
	 * @return: length of utf8 characters
	 */
	utf8_strlen : function(str) {
		var count = 0;
		for (var i = 0; i < str.length; i++) {
			var c = str.charCodeAt(i);
			if ((c >= 0x0001) && (c <= 0x007F)) {
				count++;
			} else if (c > 0x07FF) {
				count += 3;
			} else {
				count += 2;
			}
		}
		return count;
	},
	/**
	 * Description:
	 * 
	 * @param tagName
	 *            the name of jsp tag
	 * @param index
	 *            the index of the objects with the same tagName
	 * @return if the tag create a html element, it returns HtmlUIObject if the
	 *         tag create a combobox element, it returns the object of ComboBox
	 *         if the tag create a jcombo element, it returns the object of
	 *         jcombo
	 */
	getTagObject : function(tagName, index) {
		if (!tagName) {
			return null;
		}
		var objs = document.getElementsByName(tagName);
		if (!objs || objs.length == 0) {
			return null;
		}
		index = index ? index : 0;
		if (document.forms["warning_form"]) {
			index++;
		}
		var obj = objs[index];
		if (!obj) {
			return null;
		}
		// obj of combobox
		if (obj.className == 'combo-input-value') {
			obj = obj.model;
		}
		// obj of jcombo
		else if (obj.className == 'jcombo-input-value') {
			obj = getJcomboObjByName(obj.modelName);
		}
		// obj of html element
		else {
			obj = new HtmlUIObject(obj);
		}
		return obj;
	},
	getTagObjects : function(tagName) {
		var result = [];
		var size = document.getElementsByName(tagName).length;
		for (var i = 0; i < size; i++) {
			var obj = getTagDisplayObject(tagName, i);
			if (obj != null) {
				result.push(obj);
			}
		}
		return result;
	},
	/**
	 * Description: get value of the object with tagName
	 * 
	 * @param tagName
	 *            the name of jsp tag
	 */
	getTagValue : function(tagName) {
		var obj = getTagObject(tagName);
		if (obj == null) {
			return null;
		}
		return obj.getValue();
	},
	/**
	 * Description: get value of the object with tagName by index
	 * 
	 * @param tagName
	 *            the name of jsp tag
	 * @param index
	 *            the index of the object
	 */
	getIndexTagValue : function(tagName, index) {
		var obj = getTagObject(tagName, index);
		if (obj == null) {
			return null;
		}
		return obj.getValue();
	},
	/**
	 * Description: set value to the object with tagName
	 * 
	 * @param tagName
	 *            the name of jsp tag
	 * @param value
	 *            value to set
	 */
	setTagValue : function(tagName, value) {
		var obj = getTagObject(tagName);
		if (obj == null) {
			return;
		}
		if (obj.htmlElem && obj.htmlElem.type == "radio") {
			var radios = getTagObjects("radio");
			for (var i = 0; i < radios.length; i++) {
				if (radios[i].getValue() == value) {
					if (obj.setAttribute) {
						radios[i].htmlElem.checked = true;
					}
				} else {
					if (obj.setAttribute) {
						radios[i].htmlElem.checked = false;
					}
				}
			}
		} else if (obj.htmlElem && obj.htmlElem.type == "checkbox") {
			if (obj.htmlElem.value == value)
				obj.htmlElem.checked = true;
			else
				obj.htmlElem.checked = false;
		} else {
			obj.setValue(value);
			obj.flush();
		}
	},
	/**
	 * Description: get value of the objects with the same tagName
	 * 
	 * @param tagName
	 *            the name of jsp tag
	 * @param value
	 *            value to set
	 * @param index
	 *            the index of the object
	 */
	setIndexTagValue : function(tagName, value, index) {
		var obj = getTagObject(tagName, index);
		if (obj == null) {
			return;
		}
		obj.setValue(value);
		obj.flush();
	},
	/**
	 * Description:
	 * 
	 * @param tagName
	 *            the name of jsp tag
	 * @param index
	 *            the index of the objects with the same tagName
	 * @return if the tag create a html element, it returns HtmlUIObject if the
	 *         tag create a combobox element, it returns the object of ComboBox
	 *         if the tag create a jcombo element, it returns the object of
	 *         jcombo
	 */
	getTagDisplayObject : function(tagName, index) {
		if (!tagName) {
			return null;
		}
		var objs = document.getElementsByName(tagName);
		if (!objs || objs.length == 0) {
			return null;
		}
		index = index ? index : 0;
		if (document.forms["warning_form"]) {
			index++;
		}
		var obj = objs[index];
		if (!obj) {
			return null;
		}
		// obj of combobox
		if (obj.className == 'combo-input-value') {
			obj = obj.model;
		}
		// obj of jcombo
		else if (obj.className == 'jcombo-input-value') {
			obj = getJcomboObjByName(obj.modelName);
		} else if (obj.type && obj.type.toLowerCase() == 'hidden') {
			obj = obj.nextSibling;
			obj = new HtmlUIObject(obj);
		}
		// obj of html element
		else {
			obj = new HtmlUIObject(obj);
		}
		return obj;
	},
	setTagReadOnly : function(name, bReadOnly, index) {
		var obj = getTagObject(name, index);
		if (obj) {
			if (obj instanceof wincony.ria.control.ComboBox) {
				obj.setReadOnly(bReadOnly);
				var lb = getMatchedLabel(name);
				if (lb) {
					var fonts = lb.getElementsByTagName('font');
					for (var i = 0; i < fonts.length; i++) {
						if (fonts[i].className == 'font_star') {
							if (bReadOnly) {
								fonts[i].style.display = 'none';
							} else {
								fonts[i].style.display = '';
							}
						}
					}
				}
			} else {
				if (obj.setAttribute) {
					obj.setAttribute("readOnly", bReadOnly);
				}
				if (obj.getAttribute("notNull")) {
					var id = obj.getAttribute("id");
					var lb = getMatchedLabel(id);
					if (lb) {
						var fonts = lb.getElementsByTagName('font');
						for (var i = 0; i < fonts.length; i++) {
							if (fonts[i].className == 'font_star') {
								if (bReadOnly) {
									fonts[i].style.display = 'none';
								} else {
									fonts[i].style.display = '';
								}
							}
						}
					}
				}
			}
		}
	},
	setTagDisabled : function(name, bDisabled, index) {
		var obj = getTagObject(name, index);
		if (obj) {
			if (obj instanceof wincony.ria.control.ComboBox) {
				obj.setDisabled(bDisabled);
				var lb = getMatchedLabel(name);
				if (lb) {
					var fonts = lb.getElementsByTagName('font');
					for (var i = 0; i < fonts.length; i++) {
						if (fonts[i].className == 'font_star') {
							if (bDisabled) {
								fonts[i].style.display = 'none';
							} else {
								fonts[i].style.display = '';
							}
						}
					}
				}
			} else {
				if (obj.htmlElem && obj.htmlElem.type == "radio") {
					var radios = getTagObjects("radio");
					for (var i = 0; i < radios.length; i++) {
						if (obj.setAttribute) {
							radios[i].setAttribute("disabled", bDisabled);
						}
						if (radios[i].getAttribute("notNull")) {
							var id = radios[i].getAttribute("id");
							var lb = getMatchedLabel(id);
							if (lb) {
								var fonts = lb.getElementsByTagName('font');
								for (var i = 0; i < fonts.length; i++) {
									if (fonts[i].className == 'font_star') {
										if (bDisabled) {
											fonts[i].style.display = 'none';
										} else {
											fonts[i].style.display = '';
										}
									}
								}
							}
						}
					}
				} else {
					if (obj.setAttribute) {
						obj.setAttribute("disabled", bDisabled);
					}
					if (obj.getAttribute("notNull")) {
						var id = obj.getAttribute("id");
						var lb = getMatchedLabel(id);
						if (lb) {
							var fonts = lb.getElementsByTagName('font');
							for (var i = 0; i < fonts.length; i++) {
								if (fonts[i].className == 'font_star') {
									if (bDisabled) {
										fonts[i].style.display = 'none';
									} else {
										fonts[i].style.display = '';
									}
								}
							}
						}
					}
				}
			}
		}
	},
	getMatchedLabel : function(id) {
		if (!id)
			return null;
		var labels = document.getElementsByTagName('label');
		for (var i = 0; i < labels.length; i++) {
			if (labels[i]['htmlFor'] == id) {
				return labels[i];
			}
		}
		return null;
	},
	remove : function(id) {
		var idObject = document.getElementById(id);
		if (idObject != null)
			idObject.parentNode.removeChild(idObject);
	},
	appendError : function(id, str) {
		this.remove(id + idExt);// 如果span元素存在，则先删除此元素
		var spanNew = document.createElement("span");// 创建span
		spanNew.id = id + idExt;// 生成spanid
		spanNew.style.color = "red";
		spanNew.appendChild(document.createTextNode(str));// 给span添加内容
		var inputId = document.getElementById(id);
		inputId.parentNode.insertBefore(spanNew, inputId.nextSibling);// 给需要添加元素后面添加span
	}
}
Zxb.check = {
	/*
	 * JavaScript 公用数据校验模块，返回true|false; 依赖于Zxb.pub
	 */
	/*
	 * 函数清单??
	 * 
	 * 检查输入参数是否为整数 isInteger
	 * 
	 * 检查输入参数是否为合法的指定整数部分长度和小数部分长度的浮点数 checkDoubleLength
	 * 
	 * 检查输入参数是否为合法的日??validte date type return boolean value by year,month,day)
	 * checkDateByFields
	 * 
	 * 检查输入参数是否为合法的日??validte date type return boolean value by date string)
	 * checkDate
	 * 
	 * 检查输入参数是否为合法的身份证号码 (中国)(validate China ID code return boolean value)
	 * checkCnId
	 * 
	 * 检查输入参数是否为合法的身份证号码（新加坡），传入的参数必须先全部是大写，并且保证页面参数全部大写 checkSgId
	 * 
	 * 新加坡身份证首位与生日关系校?? checkSgIdBirth
	 * 
	 * 检查身份证号上的生日与输入生日(yyyy-MM-dd)是否相符 checkCnIdBirth
	 * 
	 * 检查身份证上的性别与输入的性别 (M:male;F:female)是否相符 checkCnIdGender
	 * 
	 * 根据国家的不同，检查输入的身份证号码是否合?? checkId
	 * 
	 * 检查电话号码是否正??中国) checkCnPhone
	 * 
	 * 根据国家的不同，检查电话号码是否正?? checkPhone
	 * 
	 * 检查字符串是否只有字母或数字组?? isNumberOrLetter
	 * 
	 * 检查BP机号?? checkBp
	 * 
	 * 检查是否全为字?? isLetter
	 * 
	 * 检查年?? checkAge
	 * 
	 * 检查是否全为空?? isSpace
	 * 
	 * 检查日期在给定日期之后 isLargerThanNow
	 * 
	 * 检查日期在当前日期之后 dateIsLargerThanNow
	 * 
	 * 检查是否为当日以前的合法日?? isPastDate
	 * 
	 * 检测输入的保单号是否合法，并进行补?? checkPolicyCode
	 * 
	 * 检测输入的投保单号是否合法 checkApplyCode
	 * 
	 * 判断一个字符串是否以指定字符串开?? startWith
	 * 
	 * 判断一个字符串是否以指定字符串结尾 endWith
	 * 
	 * 检查是否是团单保单号码，去除顺序号后以088结尾即认为是团单保单号码，否则不?? isGroupPolicyCode
	 * 
	 * 检查是否是团单投保单号码，去除顺序号后??50结尾即认为是团单投保单号码，否则不是 isGroupApplyCode
	 * 
	 * 检查是否是符合规则的个单投保单号码，去除顺序号后以001结尾即认为是个单投保单号码，否则不是 isValidIndvApplyCode
	 * 
	 * 检查是否是符合规则的团单投保单号码，去除顺序号后以050结尾即认为是团单投保单号码，否则不是 isValidGroupApplyCode
	 * 
	 * 检查是否是符合规则的投保单号码，去除顺序号后以001结尾即认为是个单投保单号码，否则不是；去除顺序号后以050结尾即认为是团单投保单号码，否则不是
	 * isValidApplyCode
	 * 
	 * 检查输入的E-mail是否正确(validate email return boolean value) checkEmail
	 * 
	 * 检查输入的移动电话号码是否正确(validate mobile phone return boolean value) checkMobile
	 * 
	 * 检查输入的字符是否为Double类型数据(validate float number return boolean value) isDouble
	 * 
	 * 检查输入的字符是否为货币类型数??validate currency number return boolean value)
	 * checkCurrency
	 * 
	 * 按照给定数据类型，检查输入值是否大于给定的最小值，数据类型分为，value,date,number,currency四种(validate if
	 * value < minValue and return boolean value) checkMinValue
	 * 
	 * 按照给定数据类型，检查输入值是否小于给定的最大值，数据类型分为，value,date,number,currency四种(validate if
	 * value > maxValue and return boolean value) checkMaxValue
	 * 
	 * 检查输入的字符类型是否为number??validte number type return boolean value) isNumber
	 * 
	 * 按照文档的编码类型，长度及校验位算法，检查输入的编码是否合法(validate the document code return boolean
	 * value) checkDocCode
	 * 
	 * 检查输入编码的长度是否符合指定长度(validate the code length return boolean value)
	 * checkCodeLength
	 * 
	 * 检查输入的银行帐号是否正确(validate the bank account return boolean value)
	 * checkBankAccount
	 * 
	 */
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * <p>Description: 检查输入参数是否为整数</p> @param str 待检查字符串 @return boolean值，true
	 * or false
	 * 
	 * @version 1.0
	 */
	isInteger : function(str) {
		var rc = true;
		if (str + "" == "undefined" || str == null) {
			rc = false;
		} else {
			for (var iCVal = 0; iCVal < str.length; iCVal++) {
				if (iCVal == 0 && str.charAt(iCVal) == '-') {
					continue;
				}
				if (str.charAt(iCVal) < '0' || str.charAt(iCVal) > '9') {
					rc = false;
					break;
				}
			}
		}
		return rc;
	},
	/*
	 * <p>Description: 检查输入参数是否为合法的指定整数部分长度和小数部分长度的浮点数</p> @param str 待检查字符串
	 * @param intleng 整数部分长度 @param fltleng 小数部分长度 @return boolean值，true or
	 * false <p>Create Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkDoubleLength : function(str, intleng, fltleng) {
		if (checkFloat(str) == false) {
			return false;
		}
		if (parseFloat(str) < 0) {
			if (str.length > intleng + fltleng + 2) {
				return false;
			}
		} else {
			if (str.length > intleng + fltleng + 1) {
				return false;
			}
		}
		var strArray = str.split(".");
		if (strArray.length > 2) {
			return false;
		}
		if (parseFloat(str) < 0) {
			if (strArray[0].length > intleng + 1) {
				return false;
			}
		} else {
			if (strArray[0].length > intleng) {
				return false;
			}
		}
		if (strArray.length == 2) {
			if (strArray[1].length > fltleng) {
				return false;
			}
		}
		return true;
	},
	// 3.检查输入参数是否为浮点??
	checkFloat : function(str) {
		var rc = true;
		oneDecimal = false;
		if (str + "" == "undefined" || str == null) {
			rc = false;
		} else {
			for (i = 0; i < str.length; i++) {
				ch = str.charAt(i);
				if (i == 0 && ch == '-') {
					continue;
				}
				if (ch == "." && !oneDecimal) {
					oneDecimal = true;
					continue;
				}
				if ((ch < "0") || (ch > '9')) {
					rc = false;
					break;
				}
			}
		}
		return rc;
	},
	/*
	 * <p>Description: 检查输入参数是否为合法的日??validte date type return boolean value by
	 * year,month,day)</p> @param year 日期的年?? @param month 日期的月?? @param day 日期
	 * @return boolean值，true or false <p>Create Time: 2004-07-01</p> @author:
	 * zhangmin
	 * 
	 * @version 1.0
	 */
	checkDateByFields : function(year, month, day) {
		if (!Zxb.check.isNumber(year)) {
			return false;
		}
		if (!Zxb.check.isNumber(month)) {
			return false;
		}
		if (!Zxb.check.isNumber(day)) {
			return false;
		}
		var date = new Date(year, month - 1, day);
		if (date.getMonth() == (month - 1)) {
			return true;
		} else {
			return false;
		}
	},
	/*
	 * <p>Description: 检查输入参数是否为合法的日??validte date type return boolean value by
	 * date string)</p> @param str1 待检查字符串 @return boolean值，true or false <p>Create
	 * Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkDate : function(str1, asFormat) {
		if (str1 + "" == "undefined" || str1 == null) {
			return false;
		}
		if (asFormat == null || asFormat + "" == "undefined")
			asFormat = DATE_DEFAULT_FORMAT;
		var y, m, d;
		var Separator;
		if (asFormat == "MMyyyy") {
			if (str1.length != 6)
				return false;
			else {
				if (Zxb.check.isNumber(str1)) {
					d = "01";
					m = str1.substring(0, 2);
					y = str1.substring(2);
					return Zxb.check.checkDateByFields(y, m, d);
				} else {
					return false;
				}
			}
		}
		if (asFormat == "MM/yyyy") {
			if (str1.length != 6 && str1.length != 7)
				return false;
			if (str1.length == 6) {
				if (!Zxb.check.isNumber(str1)) {
					return false;
				} else {
					d = "01";
					m = str1.substring(0, 2);
					y = str1.substring(2);
				}
			}
			if (str1.length == 7) {
				if (str1.substring(2, 3) != "/") {
					return false;
				} else {
					d = "01";
					m = str1.substring(0, 2);
					y = str1.substring(3);
				}
			}
			return Zxb.check.checkDateByFields(y, m, d);
		}
		if (asFormat == "MM.yyyy") {
			if (str1.length != 6 && str1.length != 7)
				return false;
			if (str1.length == 6) {
				if (!isNumber(str1)) {
					return false;
				} else {
					d = "01";
					m = str1.substring(0, 2);
					y = str1.substring(2);
				}
			}
			if (str1.length == 7) {
				if (str1.substring(2, 3) != ".") {
					return false;
				} else {
					d = "01";
					m = str1.substring(0, 2);
					y = str1.substring(3);
				}
			}
			return Zxb.check.checkDateByFields(y, m, d);
		}
		if (Zxb.check.isNumber(str1)) {
			// 输入日期合法格式：ddMMyyyy,ddMMyy
			if (str1.length != 8 && str1.length != 6) {
				return false;
			} else {
				if (str1.length == 8) {
					if (asFormat == "dd-MM-yy" || asFormat == "ddMMyy"
							|| asFormat == "dd/MM/yy")
						return false;
					d = str1.substring(0, 2);
					m = str1.substring(2, 4);
					y = str1.substring(4, 8);
				}
				if (str1.length == 6) {
					if (asFormat == DATE_DEFAULT_FORMAT
							|| asFormat == "ddMMyyyy")
						return false;
					d = str1.substring(0, 2);
					m = str1.substring(2, 4);
					y = SERVER_SYSTEM_DATE.substring(6, 8)
							+ str1.substring(4, 6);
				}
			}
		} else {
			// 输入日期合法格式：yyyy.MM.dd
			if (asFormat == "yyyy.MM.dd" || asFormat == "dd.MM.yyyy") {
				if (str1.indexOf(".") > -1) {
					Separator = ".";
				}
			}
			if (asFormat == "yyyy-MM-dd" || asFormat == "dd-MM-yyyy") {
				// 输入日期合法格式：yyyy-MM-dd
				if (str1.indexOf("-") > -1) {
					Separator = "-";
				}
			}
			// 输入日期合法格式：yyyy/MM/dd or dd/MM/yyyy
			if (asFormat == "yyyy/MM/dd" || asFormat == "dd/MM/yyyy") {
				if (str1.indexOf("/") > -1) {
					Separator = "/";
				}
			}
			if (asFormat == "yyyy MM dd" || asFormat == "dd MM yyyy") {
				// 输入日期合法格式：yyyy MM dd
				if (str1.indexOf(" ") > -1) {
					Separator = " ";
				}
			}
			var arrDate = new Array();
			arrDate = str1.split(Separator);
			var arrFormat = new Array();
			if (asFormat != null && asFormat + "" != "undefined")
				arrFormat = asFormat.split(Separator);
			if (arrDate.length != 3)
				return false;
			for (var iCVal = 0; iCVal < arrDate.length; iCVal++) {
				if (arrDate[iCVal] == "") {
					return false;
				}
				if (asFormat != null && asFormat + "" != "undefined") {
					if (arrDate[iCVal].length != arrFormat[iCVal].length) {
						return false;
					}
				}
			}
			d = arrDate[0];
			m = arrDate[1];
			y = arrDate[2];
			if (d.toString().length == 4) {
				y = arrDate[0];
				d = arrDate[2];
				m = arrDate[1];
			}
		}
		if (y < 1900)
			return false;
		return Zxb.check.checkDateByFields(y, m, d);
	},
	/*
	 * <p>Description: 检查输入参数是否为合法的身份证号码 (中国)(validate China ID code return
	 * boolean value)</p> @param sID 身份证号?? @return boolean值，true or false <p>Create
	 * Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkCnId : function(sID) {
		var W = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2,
				1);
		var rc = false;
		if (Zxb.pub.isUndefined(sID)) {
			return rc;
		} else if (sID.length == 15) {
			if (!checkDate(getBirthdayFromCnId(sID), "yyyy-MM-dd")) {
				return rc;
			} else {
				rc = true;
			}
		} else if (sID.length == 18) {
			var nCount = 0;
			var nIdNum = 0;
			for (var iCVal = 0; iCVal < 18; iCVal++) {
				var c = sID.charAt(iCVal);
				if ((c == 'X') || (c == 'x')) {
					nIdNum = 10;
				} else if ((c <= '9') || (c >= '0')) {
					nIdNum = c - '0';
				} else {
					return rc;
				}
				nCount += nIdNum * W[iCVal];
			}
			if (!checkDate(getBirthdayFromCnId(sID), "yyyy-MM-dd")) {
				return rc;
			}
			if ((nCount % 11) == 1) {
				rc = true;
			}
		}
		return rc;
	},
	/*
	 * <p>Description: 检查身份证号上的生日与输入生日(yyyy-MM-dd)是否相符</p> @param id 身份证号??
	 * @param birth 生日 @return boolean值，true or false <p>Create Time:
	 * 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkCnIdBirth : function(id, birth) {
		if (id.length == 15 || id.length == 18) {
			var id_birthday = formatDate(getBirthdayFromCnId(id),
					DATE_DEFAULT_FORMAT);
			if (birth != id_birthday)
				return false;
		} else {
			return false;
		}
		return true;
	},
	/*
	 * <p>Description: 检查身份证上的性别与输入的性别 (M:male;F:female)是否相符</p> @param id
	 * 身份证号?? @param gender 性别 @return boolean值，true or false <p>Create Time:
	 * 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkCnIdGender : function(id, gender) {
		if (id.length == 15 || id.length == 18) {
			var id_gender = getGenderFromCnId(id);
			if (gender.toLowerCase() != id_gender.toLowerCase()) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	},
	/*
	 * <p>Description: 通过四种类型检查输入参数是否为合法的身份证号码（新加坡），传入的参数必须先全部是大写，并且保证页面参数全部大写</p>
	 * @param sID 身份证号?? @param stype 身份证类??S,T,G,F) @return boolean值，true or
	 * false <p>Create Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkSgIdByType : function(sID, stype) {
		if (sID.charAt(0) == stype)
			return checkSgId(sID);
		else
			return false;
	},
	/**
	 * 
	 * @param {Object}
	 *            sID
	 */
	checkHkId : function(sHKID) {
		var rc = true;
		if (sHKID + "" == "undefined" || sHKID == null || sHKID == '') {
			return true;
		} else {
			if (sHKID.length != 10 && sHKID.length != 11) {
				rc = false;
			} else {
				if (sHKID.length == 10) {
					if (sHKID.charAt(7) != '(' || sHKID.charAt(9) != ')') {
						rc = false;
					} else {
						var sID = sHKID.substring(0, 7) + sHKID.substring(8, 9);
						if (sID.charAt(0) < 'A' || sID.charAt(0) > 'Z')
							rc = false;
						else {
							if (!isNumber(sID.substring(1, 6)))
								rc = false;
							else {
								if (!isNumber(sID.charAt(7))
										&& sID.charAt(7) != 'A')
									rc = false;
								else {
									if (sID.charAt(7) == 'A')
										var checkNo = 10;
									else
										var checkNo = sID.charAt(7) * 1;
									var result = (letterValue(sID.charAt(0))
											* 8 + sID.charAt(1) * 7
											+ sID.charAt(2) * 6 + sID.charAt(3)
											* 5 + sID.charAt(4) * 4
											+ sID.charAt(5) * 3 + sID.charAt(6)
											* 2 + checkNo * 1)
											% 11;
									if (result != 0)
										rc = false;
								}
							}
						}
					}
				}
				if (sHKID.length == 11) {
					if (sHKID.charAt(8) != '(' || sHKID.charAt(10) != ')')
						rc = false;
					else {
						var sID = sHKID.substring(0, 8)
								+ sHKID.substring(9, 10);
						if (!(sID.charAt(0) > 'A' && sID.charAt(0) < 'Z')
								|| !(sID.charAt(1) > 'A' && sID.charAt(1) < 'Z'))
							rc = false;
						else {
							if (!isNumber(sID.substring(2, 6)))
								rc = false;
							else {
								if (!isNumber(sID.charAt(8))
										&& sID.charAt(8) != 'A')
									rc = false;
								else {
									if (sID.charAt(8) == 'A')
										var checkNo = 10;
									else
										var checkNo = sID.charAt(8) * 1;
									var result = ((letterValue(sID.charAt(0)) + 17)
											* 9
											+ letterValue(sID.charAt(1))
											* 8
											+ sID.charAt(2)
											* 7
											+ sID.charAt(3)
											* 6
											+ sID.charAt(4)
											* 5
											+ sID.charAt(5)
											* 4
											+ sID.charAt(6)
											* 3
											+ sID.charAt(7)
											* 2 + checkNo * 1)
											% 11;
									if (result != 0)
										rc = false;
								}
							}
						}
					}
				}
			}
		}
		return rc;
	},
	/*
	 * 
	 */
	letterValue : function(sLetter) {
		var rc = null;
		if (sLetter == "A")
			rc = 1;
		if (sLetter == "B")
			rc = 2;
		if (sLetter == "C")
			rc = 3;
		if (sLetter == "D")
			rc = 4;
		if (sLetter == "E")
			rc = 5;
		if (sLetter == "F")
			rc = 6;
		if (sLetter == "G")
			rc = 7;
		if (sLetter == "H")
			rc = 8;
		if (sLetter == "I")
			rc = 9;
		if (sLetter == "J")
			rc = 10;
		if (sLetter == "K")
			rc = 11;
		if (sLetter == "L")
			rc = 12;
		if (sLetter == "M")
			rc = 13;
		if (sLetter == "N")
			rc = 14;
		if (sLetter == "O")
			rc = 15;
		if (sLetter == "P")
			rc = 16;
		if (sLetter == "Q")
			rc = 17;
		if (sLetter == "R")
			rc = 18;
		if (sLetter == "S")
			rc = 19;
		if (sLetter == "T")
			rc = 20;
		if (sLetter == "U")
			rc = 21;
		if (sLetter == "V")
			rc = 22;
		if (sLetter == "W")
			rc = 23;
		if (sLetter == "X")
			rc = 24;
		if (sLetter == "Y")
			rc = 25;
		if (sLetter == "Z")
			rc = 26;
		return rc;
	},
	/*
	 * <p>Description: 根据国家的不同，检查输入的身份证号码是否合??/p> @param id 身份证号?? @param
	 * country 身份证所在国家，CN：中国；SG：新加坡 @return boolean值，true or false <p>Create
	 * Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkId : function(id, country) {
		if (Zxb.pub.isUndefined(country) || country == "CN") {
			return checkCnId(id);
		} else if (country == "SG") {
			return checkSgId(id);
		} else if (country == "HK") {
			return checkHkId(id);
		}
	},
	/*
	 * <p>Description: 检查电话号码是否正??中国)</p> @param str 待检查的电话号码 @return
	 * boolean值，true or false <p>Create Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkCnPhone : function(str) {
		var rc = true;
		if (str + "" == "undefined" || str == null) {
			rc = false;
		} else {
			for (var iCVal = 0; iCVal < str.length; iCVal++) {
				if (!(str.charAt(iCVal) >= '0' && str.charAt(iCVal) <= '9')
						&& str.charAt(iCVal) != '*' && str.charAt(iCVal) != '('
						&& str.charAt(iCVal) != ')' && str.charAt(iCVal) != '-') {
					rc = false;
					break;
				}
			}
		}
		return rc;
	},
	/*
	 * <p>Description: 根据国家的不同，检查电话号码是否正??/p> @param str 待检查的电话号码 @param
	 * country 电话号码所在国家，CN：中?? @return boolean值，true or false <p>Create Time:
	 * 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkPhone : function(str, country) {
		if (Zxb.pub.isUndefined(country) || country == "CN") {
			return checkCnPhone(str);
		}
	},
	/*
	 * <p>Description: 检查字符串是否只有字母或数字组??/p> @param str 待检查的字符?? @return
	 * boolean值，true or false <p>Create Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	isNumberOrLetter : function(str) {
		var rc = true;
		if (Zxb.pub.isUndefined(str)) {
			rc = false;
		} else {
			for (var iCVal = 0; iCVal < str.length; iCVal++) {
				if (str.charAt(iCVal) < '0'
						|| (str.charAt(iCVal) > '9' && str.charAt(iCVal) < 'A')
						|| (str.charAt(iCVal) > 'Z' && str.charAt(iCVal) < 'a')
						|| str.charAt(iCVal) > 'z') {
					rc = false;
					break;
				}
			}
		}
		return rc;
	},
	/*
	 * <p>Description: 检查是否全为字??/p> @param str 待检查的字符?? @return boolean值，true
	 * or false <p>Create Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	isLetter : function(str) {
		var rc = true;
		if (Zxb.pub.isUndefined(str)) {
			rc = false;
		} else {
			for (var iCVal = 0; iCVal < str.length; iCVal++) {
				if ((str.charAt(iCVal) > 'Z' && str.charAt(iCVal) < 'a')
						|| str.charAt(iCVal) < 'A' || str.charAt(iCVal) > 'z') {
					rc = false;
					break;
				}
			}
		}
		return rc;
	},
	/*
	 * <p>Description: 检查年??/p> @param str 待检查的年龄 @return boolean值，true or
	 * false <p>Create Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkAge : function(age) {
		var rc = true;
		if (isNumber(age) == false) {
			rc = false;
		} else if (age >= 200) {
			rc = false;
		} else if (age < 0) {
			rc = false;
		}
		return rc;
	},
	/*
	 * <p>Description: 检查是否全为空??/p> @param str 待检查的字符?? @return boolean值，true
	 * or false <p>Create Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	isSpace : function(str) {
		for (var iCVal = 0; iCVal < str.length - 1; iCVal++) {
			if (str.charAt(iCVal) != ' ') {
				if (str.charAt(iCVal) != 0xa1 || str.chatAt(iCVal + 1) != 0xa1)
					return false;
				else
					iCVal++;
			}
		}
		if (str.charAt(iCVal) != '	')
			return false;
		return true;
	},
	/*
	 * <p>Description: 检查日期在给定日期之后</p> @param year 待检查日期的年份 @param month
	 * 待检查日期的月份 @param day 待检查日 @param d0 给定的日?? @return boolean值，true or false
	 * <p>Create Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	dateIsLargerThanDate : function(year, month, day, d0) {
		return isLargerThanNow(year, month, day, d0);
	},
	isLargerThanNow : function(year, month, day, d0) {
		var rc = false;
		if (!checkDateByFields(year, month, day)) {
			rc = false;
		} else {
			var d1 = new Date(year, month - 1, day);
			if (d1.getTime() > d0.getTime()) {
				rc = true;
			}
		}
		return rc;
	},
	/*
	 * <p>Description: 检查日期在当前日期之后</p> @param year 待检查日期的年份 @param month
	 * 待检查日期的月份 @param day 待检查日 @return boolean值，true or false <p>Create Time:
	 * 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	dateIsLargerThanNow : function(year, month, day) {
		var Y = SERVER_SYSTEM_DATE.substring(6);
		var M = SERVER_SYSTEM_DATE.substring(3, 5);
		var D = SERVER_SYSTEM_DATE.substring(0, 2);
		var nowdate = new Date(Y, M - 1, D);
		return isLargerThanNow(year, month, day, nowdate);
	},
	/*
	 * <p>Description: 比较两个日期是否相差一个月，如果超出一个月返回false,否则返回true</p> @param date1
	 * 待检查的第一个日?? @param date2 待检查的第二个日?? @return boolean值，true or false <p>Create
	 * Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	isDatesBetweenOneMonth : function(date1, date2) {
		var rc = true;
		var y1 = date1.substring(6);
		var m1 = date1.substring(3, 5);
		var d1 = date1.substring(0, 2);
		var y2 = date2.substring(6);
		var m2 = date2.substring(3, 5);
		var d2 = date2.substring(0, 2);
		if (y1 != y2) {
			if (y1 > y2) {
				if (parseFloat(y2) + 1 != y1)
					rc = false;
				else if (parseFloat(m2) != 12 || parseFloat(m1) != 1)
					rc = false;
				else if (d2 <= d1)
					rc = false;
			}
			if (y1 < y2) {
				if (parseFloat(y1) + 1 != y2)
					rc = false;
				else if (parseFloat(m1) != 12 || parseFloat(m2) != 1)
					rc = false;
				else if (d1 <= d2)
					rc = false;
			}
		} else {
			if (m1 > m2) {
				if (parseFloat(m2) + 1 != m1)
					rc = false;
				else if (d2 <= d1)
					rc = false;
			}
			if (m1 < m2) {
				if (parseFloat(m1) + 1 != m2)
					rc = false;
				else if (d1 <= d2)
					rc = false;
			}
		}
		return rc;
	},
	/*
	 * <p>Description: 检查是否为当日以前的合法日??/p> @param str1 待检查的日期 yyyy-MM-dd @return
	 * boolean值，true or false <p>Create Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	isPastDate : function(str1) {
		if (str1 + "" == "undefined" || str1 == null) {
			return false;
		}
		var y, m, d;
		var iCVal;
		iCVal = str1.indexOf("-");
		if (iCVal == -1 || iCVal == str1.length) {
			return false;
		}
		y = str1.substring(0, iCVal);
		str1 = str1.substring(iCVal + 1);
		iCVal = str1.indexOf("-");
		if (iCVal == -1 || iCVal == str1.length) {
			return false;
		}
		m = str1.substring(0, iCVal);
		d = str1.substring(iCVal + 1);
		return !dateIsLargerThanNow(y, m, d);
	},
	/*
	 * <p>Description: 根据公司名检测输入的保单号是否合??/p> @param str 输入保单?? @return
	 * boolean值，true or false <p>Create Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkPolicyCode : function(str) {
		var rc = true;
		var companyName = getCustomerCompanyName();
		var jsPrjRelated = getJsPrjRelated();
		if (jsPrjRelated != null && jsPrjRelated != '') {
			var prjRelatedCall = 'isPolicyCode' + jsPrjRelated;
			if (funFactory.hasFunction(prjRelatedCall)) {
				try {
					return funFactory[prjRelatedCall](str);
				} catch (e) {
					alert(prjRelatedCall + '()  error;' + '\n Caused by: \n  '
							+ e.message);
					return false
				}
			}
		}
		if (companyName == 'SL') {
			rc = isPolicyCodeSL(str);
		}
		// 公司名为GEL
		else {
			rc = isPolicyCodeGEL(str);
		}
		return rc;
	},
	/*
	 * <p>Description: 检测输入的保单号是否合??for GEL</p> @param str 输入保单?? @return
	 * boolean值，true or false <p>Create Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	isPolicyCodeGEL : function(str) {
		var rc = true;
		var len = 10;
		if (!Zxb.pub.isUndefined(str) && str != "") {
			var aryStr = str.split("-");
			if (aryStr.length == 1) {
				if (str.length > 10)
					rc = false;
				else {
					if (!isNumber(str))
						rc = false;
					else {
						str = preFillZero(str, len);
						var checkNum = getCheckDigit(str);
						if (checkNum != str.charAt(9))
							rc = false;
					}
				}
			} else {
				if (aryStr.length == 2) {
					if (aryStr[0].length > 9 || aryStr[1].length != 1)
						rc = false;
					else {
						if (!isNumber(aryStr[0]) || !isNumber(aryStr[1]))
							rc = false;
						else {
							var preStr = preFillZero(aryStr[0], len - 1);
							var checkNum = getCheckDigit(preStr);
							if (checkNum != aryStr[1])
								rc = false;
						}
					}
				} else {
					rc = false;
				}
			}
		}
		return rc;
	},
	/*
	 * <p>Description: 检测输入的保单号是否合??for SL </p> @param str 输入保单?? @return
	 * boolean值，true or false <p>Create Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	isPolicyCodeSL : function(str) {
		// XXXXX
		var rc = true;
		var len = 10;
		if (!Zxb.pub.isUndefined(str) && str != "") {
			if (str.length > 10)
				rc = false;
			else {
				if (!isNumber(str))
					rc = false;
				else {
					str = preFillZero(str, len);
					if (!endWith(str, "001"))
						rc = false;
				}
			}
		}
		return rc;
	},
	/*
	 * <p>Description: 检测输入的投保单号是否合法</p> @param str 输入投保单号 @return
	 * boolean值，true or false <p>Create Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkApplyCode : function(str) {
		var jsPrjRelated = getJsPrjRelated();
		if (jsPrjRelated != null && jsPrjRelated != '') {
			var prjRelatedCall = 'isApplyCode' + jsPrjRelated;
			if (funFactory.hasFunction(prjRelatedCall)) {
				try {
					return funFactory[prjRelatedCall](str);
				} catch (e) {
					alert(prjRelatedCall + '()  error;' + '\n Caused by: \n  '
							+ e.message);
					return false
				}
			}
		}
		return isApplyCode(str);
	},
	isApplyCode : function(str) {
		// XX/XXXXX/XX
		var rc = true;
		if (!Zxb.pub.isUndefined(str) && str != "") {
			var aryStr = str.split("/");
			if (aryStr.length == 1) {
				if (str.length > 10 || str.length < 8)
					rc = false;
				else if (!isNumber(str.substring(str.length - 7)))
					rc = false;
			} else {
				if (aryStr.length == 3) {
					if (aryStr[0].length > 3 || aryStr[0].length < 1
							|| aryStr[1].length != 5 || aryStr[2].length != 2)
						rc = false;
					else if (!isNumber(aryStr[1]) || !isNumber(aryStr[2]))
						rc = false;
				} else {
					rc = false;
				}
			}
		}
		return rc;
	},
	/*
	 * <p>Description: 判断一个字符串是否以指定字符串开??/p> @param str 待检查字符串 @param start
	 * 指定开头的字符?? @return boolean值，true or false <p>Create Time: 2004-07-01</p>
	 * @author: zhangmin
	 * 
	 * @version 1.0
	 */
	startWith : function(str, start) {
		try {
			return (str.substring(0, start.length) == start);
		} catch (ex) {
			return false;
		}
	},
	/*
	 * <p>Description: 判断一个字符串是否以指定字符串结尾</p> @param str 待检查字符串 @param end
	 * 指定结尾的字符串 @return boolean值，true or false <p>Create Time: 2004-07-01</p>
	 * @author: zhangmin
	 * 
	 * @version 1.0
	 */
	endWith : function(str, end) {
		try {
			return (str.substring(str.length - end.length) == end);
		} catch (ex) {
			return false;
		}
	},
	/*
	 * <p>Description: 检查是否是团单保单号码，去除顺序号后以088结尾即认为是团单保单号码，否则不??/p> @param code
	 * 待检查的保单号码 @return boolean值，true or false <p>Create Time: 2004-07-01</p>
	 * @author: zhangmin
	 * 
	 * @version 1.0
	 */
	isGroupPolicyCode : function(code) {
		return endWith(getPureCode(code), "088");
	},
	/*
	 * <p>Description: 检查是否是团单投保单号码，去除顺序号后??50结尾即认为是团单投保单号码，否则不是</p> @param
	 * code 待检查的投保单号?? @return boolean值，true or false <p>Create Time:
	 * 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	isGroupApplyCode : function(code) {
		return endWith(getPureCode(code), "050");
	},
	/*
	 * <p>Description: 检查是否是符合规则的个单投保单号码，去除顺序号后以001结尾即认为是个单投保单号码，否则不是</p>
	 * @param code 待检查的投保单号?? @return boolean值，true or false <p>Create Time:
	 * 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	isValidIndvApplyCode : function(code) {
		return isValidApplyCode(code, "001");
	},
	/*
	 * <p>Description: 检查是否是符合规则的团单投保单号码，去除顺序号后以050结尾即认为是团单投保单号码，否则不是</p>
	 * @param code 待检查的投保单号?? @return boolean值，true or false <p>Create Time:
	 * 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	isValidGroupApplyCode : function(code) {
		return isValidApplyCode(code, "050");
	},
	/*
	 * <p>Description: 检查是否是符合规则的投保单号码，去除顺序号后以001结尾即认为是个单投保单号码，否则不是??
	 * 去除顺序号后??50结尾即认为是团单投保单号码，否则不是</p> @param code 投保单号?? @param suffix
	 * 投保单号码后缀（个??001 团单:050?? @return boolean值，true or false <p>Create Time:
	 * 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	isValidApplyCode : function(code, suffix) {
		var result = false;
		// 截取续保的投保单号码前面的部分???????????????????????
		var pureCode = getPureCode(code);
		// 是否??0??
		result = pureCode.length == 15;
		if (result == false)
			return false;
		// 检查是否全部是数字????????????????
		for (var iCVal = 0; iCVal < pureCode.length - 1; iCVal++) {
			if (pureCode.charAt(iCVal) < '0' || pureCode.charAt(iCVal) > '9') {
				result = false;
				break;
			}
		}
		if (result == false)
			return false;
		// 如果是老保单，则返回?????????
		if (startWith(pureCode, "00"))
			return true;
		// 是否??个单??01 团单??50)结尾
		result = endWith(pureCode, suffix);
		if (result == false)
			return false;
		// 检查校验码
		// 检查重新组合后的数字除??7余数是否??
		var crc;
		if (startWith(pureCode, "0")) {
			crc = pureCode.substring(1, 10) + pureCode.substring(12, 15)
					+ pureCode.substring(10, 12);
		} else {
			crc = pureCode.substring(0, 10) + pureCode.substring(12, 15)
					+ pureCode.substring(10, 12);
		}
		result = (parseInt(crc) % 97) == 1;
		return result;
	},
	/*
	 * <p>Description: 检查输入的E-mail是否正确(validate email return boolean value)</p>
	 * @param str 待检查的e-mail @return boolean值，true or false <p>Create Time:
	 * 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkEmail : function(str) {
		var CHAR_LETTER_NUMERIC = 1;
		var CHAR_UNDERLINE = 2;
		var CHAR_DOT = 3;
		var CHAR_AT = 4;
		var CHAR_DIVIDE = 5;
		var CHAR_END = 6;
		var CHAR_OTHER = 7;
		var DIV_CHAR = ',';
		var rc = true;
		if (Zxb.pub.isUndefined(str)) {
			rc = false;
			return rc;
		} else if (str.length == 0) {
			rc = true;
			return rc;
		}
		var exit_flag = false;
		var total_char = str.length;
		var pos = 0;
		var cur_char;
		var cur_status = 0;
		while ((pos <= total_char) && (!exit_flag)) {
			if (pos == total_char) {
				cur_char = CHAR_END;
			} else if (str.charAt(pos) == '.') {
				cur_char = CHAR_DOT;
			} else if (str.charAt(pos) == DIV_CHAR) {
				cur_char = CHAR_DIVIDE;
			} else if (str.charAt(pos) == '_') {
				cur_char = CHAR_UNDERLINE;
			} else if (str.charAt(pos) == '@') {
				cur_char = CHAR_AT;
			} else if (((str.charAt(pos) >= 'a') && (str.charAt(pos) <= 'z'))
					|| ((str.charAt(pos) >= 'A') && (str.charAt(pos) <= 'Z'))
					|| ((str.charAt(pos) >= '0') && (str.charAt(pos) <= '9'))
					|| (str.charAt(pos) == '-')) {
				cur_char = CHAR_LETTER_NUMERIC;
			} else {
				cur_char = CHAR_OTHER;
			}
			switch (cur_status) {
				case -1 :// error
					rc = false;
					exit_flag = true;
					break;
				case 0 :// begin status
					if ((cur_char == CHAR_LETTER_NUMERIC)
							|| (cur_char == CHAR_UNDERLINE)
							|| (cur_char == CHAR_DOT)) {
						cur_status = 1;
					} else {
						rc = false;
						cur_status = -1;
					}
					break;
				case 1 :// user name
					if ((cur_char == CHAR_LETTER_NUMERIC)
							|| (cur_char == CHAR_UNDERLINE)
							|| (cur_char == CHAR_DOT)) {
						cur_status = 1;
					} else if (cur_char == CHAR_AT) {
						cur_status = 2;
					} else {
						rc = false;
						cur_status = -1;
					}
					break;
				case 2 :// @
					if (cur_char == CHAR_LETTER_NUMERIC) {
						cur_status = 6;
					} else {
						rc = false;
						cur_status = -1;
					}
					break;
				case 6 :// "."
					if (cur_char == CHAR_LETTER_NUMERIC) {
						cur_status = 6;
					} else if (cur_char == CHAR_DOT) {
						cur_status = 3;
					} else {
						rc = false;
						cur_status = -1;
					}
					break;
				case 3 :// fisrt domain name
					if (cur_char == CHAR_LETTER_NUMERIC) {
						cur_status = 4;
					} else {
						rc = false;
						cur_status = -1;
					}
					break;
				case 4 :// not first domain name
					if (cur_char == CHAR_LETTER_NUMERIC) {
						cur_status = 4;
					} else if (cur_char == CHAR_DOT) {
						cur_status = 3;
					} else if (cur_char == CHAR_DIVIDE) {
						cur_status = 0;
					} else if (cur_char == CHAR_END) {
						cur_status = 5;
					} else {
						rc = false;
						cur_status = -1;
					}
					break;
				case 5 :// ok
					rc = true;
					exit_flag = true;
					break;
				default :
					rc = false;
					exit_flag = true;
					break;
			}
			pos++;
		}
		return rc;
	},
	/*
	 * <p>Description: 检查输入的移动电话号码是否正确(validate mobile phone return boolean
	 * value)</p> @param str 待检查的移动电话号码 @return boolean值，true or false <p>Create
	 * Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkMobile : function(str, country) {
		return checkPhone(str, country);
	},
	/*
	 * <p>Description: 检查输入的字符是否为double类型数据(validate double number return
	 * boolean value)</p> @param str 待检查的字符?? @return boolean值，true or false
	 * <p>Create Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	isDouble : function(str) {
		var rc = true;
		oneDecimal = false;
		if (Zxb.pub.isUndefined(str)) {
			rc = false;
		} else {
			for (var iCVal = 0; iCVal < str.length; iCVal++) {
				ch = str.charAt(iCVal);
				if (iCVal == 0 && ch == '-') {
					continue;
				}
				if (ch == "." && !oneDecimal) {
					oneDecimal = true;
					continue;
				}
				if ((ch < "0") || (ch > '9')) {
					rc = false;
					break;
				}
			}
		}
		return rc;
	},
	/*
	 * <p>Description: 检查输入的字符是否为货币类型数??validate currency number return boolean
	 * value)</p> @param str 待检查的字符?? @return boolean值，true or false <p>Create
	 * Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkCurrency : function(str) {
		var rc = true;
		oneDecimal = false;
		if (Zxb.pub.isUndefined(str)) {
			rc = false;
		} else {
			for (var iCVal = 0; iCVal < str.length; iCVal++) {
				ch = str.charAt(iCVal);
				if (iCVal == 0 && ch == '-' && str.length != 1) {
					continue;
				}
				if (ch == "." && !oneDecimal) {
					oneDecimal = true;
					continue;
				}
				if (!((ch >= '0' && ch <= '9') || ch == ',')) {
					rc = false;
					break;
				}
			}
		}
		return rc;
	},
	checkInteger : function(str) {
		if (Zxb.pub.isUndefined(str)) {
			return false;
		}
		if (str == '') {
			return true;
		}
		var reg = new RegExp("^(-)?[0-9]([0-9]|,)*$");
		return reg.test(str);
	},
	/*
	 * <p>Description:
	 * 按照给定数据类型，检查输入值是否大于给定的最小值，数据类型分为，value,date,number,currency四种 (validate if
	 * value < minValue and return boolean value)</p> @param str 输入字符 @param
	 * minstr 给定的最小?? @param formatType 数据类型 @return boolean值，true or false <p>Create
	 * Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkMinValue : function(str, minstr, formatType, format) {
		var rc = true;
		if (Zxb.pub.isUndefined(str)) {
			rc = false;
		} else {
			if (formatType == "number") {
				if (str != "" && checkNumberByFormat(str, format)) {
					if (parseFloat(Zxb.pub.numberTrimByFormat(str, format)) < parseFloat(Zxb.pub
							.numberTrimByFormat(minstr, format)))
						rc = false;
				}
			}
			if (formatType == "currency") {
				if (str != "" && checkCurrency(str)) {
					if (parseFloat(Zxb.pub.numberTrim(str)) < parseFloat(Zxb.pub
							.numberTrim(minstr)))
						rc = false;
				}
			}
			if (formatType == "date") {
				if (str != "" && Zxb.check.checkDate(str)) {
					var formatStr = Zxb.pub.formatDate(str, "yyyy-MM-dd");
					var formatMinStr = Zxb.pub.formatDate(minstr, "yyyy-MM-dd");
					if (formatStr < formatMinStr)
						rc = false;
				}
			}
			if (formatType == null || formatType == "value") {
				if (str != "") {
					if (parseFloat(Zxb.pub.numberTrim(str)) < parseFloat(Zxb.pub
							.numberTrim(minstr)))
						rc = false;
				}
			}
		}
		return rc;
	},
	checkNumberByFormat : function(str, format) {
		var separator = ',';
		var decimal = '.'
		if (_needSwitch(format)) {
			separator = '.';
			decimal = ',';
		}
		var rc = true;
		oneDecimal = false;
		if (Zxb.pub.isUndefined(str)) {
			rc = false;
		} else {
			for (var iCVal = 0; iCVal < str.length; iCVal++) {
				ch = str.charAt(iCVal);
				if (iCVal == 0 && ch == '-' && str.length != 1) {
					continue;
				}
				if (ch == decimal && !oneDecimal) {
					oneDecimal = true;
					continue;
				}
				if (!((ch >= '0' && ch <= '9') || (ch == separator && !oneDecimal))) {
					rc = false;
					break;
				}
			}
		}
		return rc;
	},
	/*
	 * <p>Description:
	 * 按照给定数据类型，检查输入值是否小于给定的最大值，数据类型分为，value,date,number,currency四种 (validate if
	 * value > maxValue and return boolean value)</p> @param str 输入字符 @param
	 * maxstr 给定的最大?? @param formatType 数据类型 @return boolean值，true or false <p>Create
	 * Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkMaxValue : function(str, maxstr, formatType, format) {
		var rc = true;
		if (Zxb.pub.isUndefined(str)) {
			rc = false;
		} else {
			if (formatType == "number") {
				if (str != "" && checkNumberByFormat(str, format)) {
					if (parseFloat(numberTrimByFormat(str, format)) > parseFloat(numberTrimByFormat(
							maxstr, format)))
						rc = false;
				}
			}
			if (formatType == "currency") {
				if (str != "" && checkCurrency(str)) {
					if (parseFloat(numberTrim(str)) > parseFloat(numberTrim(maxstr)))
						rc = false;
				}
			}
			// add new method checkMaxDateValue
			// if (formatType=="date"){
			// if(str!="" && checkDate(str)) {
			// var formatStr = formatDate(str,"yyyy-MM-dd");
			// var formatMaxStr = formatDate(maxstr,"yyyy-MM-dd");
			// if (formatStr > formatMaxStr) rc = false;
			// }
			// }
			if (formatType == "value") {
				if (str != "") {
					if (parseFloat(Zxb.pub.numberTrim(str)) > parseFloat(Zxb.pub
							.numberTrim(maxstr)))
						rc = false;
				}
			} else {
				if (str != "") {
					if (parseFloat(Zxb.pub.numberTrim(str)) > parseFloat(Zxb.pub
							.numberTrim(maxstr)))
						rc = false;
				}
			}
		}
		return rc;
	},
	checkMaxDateValue : function(str, maxstr, format, formatType) {
		var rc = true;
		if (Zxb.pub.isUndefined(str)) {
			rc = false;
		} else {
			if (Zxb.pub.isUndefined(format)) {
				format = DATE_DEFAULT_FORMAT;
				if (formatType == "dateTime")
					format = format + " HH:mm";
			}
			if (formatType == "date") {
				if (str != "" && Zxb.check.checkDate(str, format)) {
					var formatStr = Zxb.pub.formatDate(str, "yyyy-MM-dd");
					var formatMaxStr = Zxb.pub.formatDate(maxstr, "yyyy-MM-dd");
					if (formatStr > formatMaxStr)
						rc = false;
				}
			}
			if (formatType == "dateTime") {
				if (str != null && str != "" && Zxb.check.checkDateTime(str, format)) {
					var formatStr = Zxb.pub.formatDateTime(str, format);
					var formatMaxStr = Zxb.pub.formatDateTime(maxstr, format);
					if (formatStr > formatMaxStr)
						rc = false;
				}
			}
		}
		return rc;
	},
	checkMinDateValue : function(str, maxstr, format, formatType) {
		var rc = true;
		if (Zxb.pub.isUndefined(str)) {
			rc = false;
		} else {
			if (Zxb.pub.isUndefined(format)) {
				format = DATE_DEFAULT_FORMAT;
				if (formatType == "dateTime")
					format = format + " HH:mm";
			}
			if (formatType == "date") {
				if (str != "" && Zxb.check.checkDate(str, format)) {
					var formatStr = Zxb.pub.formatDate(str, "yyyy-MM-dd");
					var formatMaxStr = Zxb.pub.formatDate(maxstr, "yyyy-MM-dd");
					if (formatStr < formatMaxStr)
						rc = false;
				}
			}
			if (formatType == "dateTime") {
				if (str != null && str != "" && Zxb.check.checkDateTime(str, format)) {
					var formatStr = Zxb.pub.formatDateTime(str, format);
					var formatMaxStr = Zxb.pub.formatDateTime(maxstr, format);
					if (formatStr < formatMaxStr)
						rc = false;
				}
			}
		}
		return rc;
	},
	/*
	 * <p>Description: 检查输入的字符类型是否为number??validte number type return boolean
	 * value)</p> @param str 待检查的字符 @return boolean值，true or false <p>Create
	 * Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	isNumber : function(str) {
		var rc = true;
		if (Zxb.pub.isUndefined(str)) {
			rc = false;
		} else if (str.length == 0) {
			rc = false;
		} else {
			for (var iCVal = 0; iCVal < str.length; iCVal++) {
				if (str.charAt(iCVal) < '0' || str.charAt(iCVal) > '9') {
					rc = false;
					break;
				}
			}
		}
		return rc;
	},
	/*
	 * <p>Description: 按照文档的编码类型，长度及校验位算法，检查输入的编码是否合法(validate the document
	 * code return boolean value)</p> @param str 待检查的编码 @param typeCode 编码类型
	 * @param nLength 编码指定长度 @return boolean值，true or false <p>Create Time:
	 * 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkDocCode : function(str, typeCode, nLength) {
		var rc = true;
		var strLength = str.length;
		if (Zxb.pub.isUndefined(str)) {
			rc = false;
		} else {
			// check code length
			if (strLength != nLength) {
				rc = false;
			} else {
				// check code type
				var checkNum = new Array(9, 7, 3, 2, 8, 7, 4, 3, 2);
				if (typeCode == 1) {
					// check code arithmetic
					var check = 10
							- (str.charAt(0) * checkNum[0] + str.charAt(1)
									* checkNum[1] + str.charAt(2) * checkNum[2]
									+ str.charAt(3) * checkNum[3]
									+ str.charAt(4) * checkNum[4]
									+ str.charAt(5) * checkNum[5]
									+ str.charAt(6) * checkNum[6]
									+ str.charAt(7) * checkNum[7] + str
									.charAt(8)
									* checkNum[8]) % 10;
					if (check != str.charAt(9))
						rc = false;
				}
			}
		}
		return rc;
	},
	/*
	 * <p>Description: 检查输入编码的长度是否符合指定长度(validate the code length return
	 * boolean value)</p> @param str 待检查的编码 @param codeLength 指定长度 @return
	 * boolean值，true or false <p>Create Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkCodeLength : function(str, codeLength) {
		var rc = true;
		if (Zxb.pub.isUndefined(str)) {
			rc = false;
		} else {
			if (str.length > codeLength)
				rc = false;
		}
		return rc;
	},
	/*
	 * <p>Description: 检查输入的银行帐号是否正确(validate the bank account return boolean
	 * value)</p> @param str 待检查的银行帐号 @return boolean值，true or false <p>Create
	 * Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkBankAccount : function(str) {
		var rc = true;
		return rc;
	},
	/*
	 * <p>Description: 比较两个日期的大??/p> @param strDate1 给定的第一个日?? @param strDate2
	 * 给定的第二个日期 @return 0：两个日期相等； 1：strDate1大于strDate2?? -1：strDate1小于strDate2??
	 * 2：输入日期非法； 3：出现异?? <p>Create Time: 2004-07-01</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	dateCompare : function(strDate1, strDate2) {
		var rc = 3;
		if (Zxb.pub.isUndefined(strDate1) || strDate1 == ""
				|| !checkDate(strDate1) || Zxb.pub.isUndefined(strDate2)
				|| strDate2 == "" || !checkDate(strDate2)) {
			rc = 2;
		} else {
			try {
				var formatStr1 = formatDate(strDate1, "yyyy-MM-dd");
				var formatStr2 = formatDate(strDate2, "yyyy-MM-dd");
				if (formatStr1 < formatStr2)
					rc = -1;
				if (formatStr1 > formatStr2)
					rc = 1;
				if (formatStr1 == formatStr2)
					rc = 0;
			} catch (ex) {
				rc = 3;
				return rc;
			}
		}
		return rc;
	},
	/*
	 * <p>Description: 检查proposalNo的正确??/p> @param proposalNo 给定的proposalNo
	 * @return 0：正确数?? -1：输入NO.长度错误?? -2：输入最后两位不符合当前时间?? -3：输入的序列号不正确. <p>Create
	 * Time: 2004-10-15</p> @author: zhangmin
	 * 
	 * @version 1.0
	 */
	checkProposalNo : function(proposalNo) {
		var rc = 0;
		var dateLength = SERVER_SYSTEM_DATE.length;
		var lastPart = SERVER_SYSTEM_DATE.substring(dateLength - 2);
		if (!Zxb.pub.isUndefined(proposalNo) && proposalNo != "") {
			if (proposalNo.length > 10 || proposalNo.length < 8)
				return -1;
			if (proposalNo.substring(proposalNo.length - 2) != lastPart)
				if (parseFloat(proposalNo.substring(proposalNo.length - 2)) + 1 != lastPart
						|| SERVER_SYSTEM_DATE.substring(3, 5) != "01")
					return -2;
			if (!isNumber(proposalNo.substring(proposalNo.length - 7,
					proposalNo.length - 2)))
				return -3;
		}
		return rc;
	},
	/**
	 * 检查是否全为空??AllSpace
	 */
	AllSpace : function(str) {
		for (var iCVal = 0; iCVal < str.length - 1; iCVal++) {
			if (str.charAt(iCVal) != ' ') {
				if (str.charAt(iCVal) != 0xa1 || str.chatAt(iCVal + 1) != 0xa1)
					return false;
				else
					iCVal++;
			}
		}
		if (str.charAt(iCVal) != ' ')
			return false;
		return true;
	},
	/**
	 * 检查是否全为空??AllSpace
	 */
	allSpace : function(str) {
		return this.AllSpace(str);
	},
	/**
	 * // 1.检查输入参数是否全为数??
	 */
	CheckNumber : function(str) {
		var rc = true;
		if (str + "" == "undefined" || str == null) {
			rc = false;
		} else if (str.length == 0) {
			rc = false;
		} else {
			for (var iCVal = 0; iCVal < str.length; iCVal++) {
				if (str.charAt(iCVal) < '0' || str.charAt(iCVal) > '9') {
					rc = false;
					break;
				}
			}
		}
		return rc;
	},
	checkNumber : function(str) {
		return this.CheckNumber(str);
	},
	getCheckDigit : function(str) {
		var checkNum = new Array(9, 7, 3, 2, 8, 7, 4, 3, 2);
		// check code arithmetic
		var check = 10
				- (str.charAt(0) * checkNum[0] + str.charAt(1) * checkNum[1]
						+ str.charAt(2) * checkNum[2] + str.charAt(3)
						* checkNum[3] + str.charAt(4) * checkNum[4]
						+ str.charAt(5) * checkNum[5] + str.charAt(6)
						* checkNum[6] + str.charAt(7) * checkNum[7] + str
						.charAt(8)
						* checkNum[8]) % 10;
		if (check == 10)
			check = 0;
		return check;
	},
	isSeasonStartDay : function(str) {
		var d = str.substring(0, 2);
		var m = str.substring(3, 5);
		if (parseFloat(d) != 1)
			return false;
		if (parseFloat(m) != 1 && parseFloat(m) != 4 && parseFloat(m) != 7
				&& parseFloat(m) != 10)
			return false;
		return true;
	},
	getMaxDayOfMonth : function(year, month) {
		var theMonth = new Date(year, month, 1);
		theMonth.setHours(theMonth.getHours() - 3);
		// alert(theMonth.getDate());
		return theMonth.getDate();
	},
	isMaxDayOfMonth : function(year, month, day) {
		if (getMaxDayOfMonth(year, month) == day) {
			return true;
		} else {
			return false;
		}
	},
	checkHoursAndMinute : function(str) {
		var rc = true;
		if (str + "" != "undefined" && str != "" && str != null) {
			if (str.indexOf(":") < 0)
				rc = false;
			else {
				var tm = str.split(":");
				if (tm.length != 2)
					rc = false;
				else {
					if (tm[0].length > 2 || tm[0].length == 0
							|| tm[1].length != 2)
						rc = false;
					else {
						if (!isNumber(tm[0]) || !isNumber(tm[1]))
							rc = false;
						else {
							if (parseInt(tm[0]) < 0 || parseInt(tm[0]) > 23
									|| parseInt(tm[1]) < 0
									|| parseInt(tm[1]) > 59)
								rc = false;
						}
					}
				}
			}
		}
		return rc;
	},
	checkWorkDay : function(str, format) {
		var count;
		var dateArray = new Array();
		dateArray[0] = "";
		dateArray[1] = "";
		var formatAs = format;
		if (isNumber(str)) {
			formatAs = "";
			for (var j = 0; j < format.length; j++) {
				if (isLetter(format.charAt(j))) {
					formatAs = formatAs + format.charAt(j);
				}
			}
		}
		var contextPathValue = document.getElementById('contextPathValue');
		var contextPath = '/ls';
		if (contextPathValue != null && contextPathValue != undefined) {
			contextPath = contextPathValue.value
		}
		var url = contextPath + "/checkWorkDay.do?dateValue=" + str
				+ "&format=" + formatAs + "&entryflag=js";
		var rootEle = LoadXml(url);
		if (rootEle == null) {
			alert('Some error occured,please contact administrator');
			return null;
		}
		var currNode = rootEle.selectSingleNode("Count");
		if (currNode == null) {
			window.alert("Data format error!");
			window.alert(rootEle);
			return null;
		} else {
			count = currNode.text;
		}
		if (count == 0)
			return false;
		currResult = rootEle.selectSingleNode("Result").text;
		currLastDay = rootEle.selectSingleNode("lastDay").text;
		dateArray[0] = currResult;
		dateArray[1] = currLastDay;
		return dateArray;
	},
	checkDateTime : function(str1, asFormat) {
		if (formatDateTime(str1, asFormat) == false)
			return false;
		else
			return true;
	},
	formatDateTime : function(str, asFormat) {
		if (str == null || str + "" == "undefined") {
			return false;
		}
		if (asFormat == null || asFormat + "" == "undefined")
			asFormat = DATE_DEFAULT_FORMAT + " HH:mm:ss";
		var year, month, day, hour, minute, second;
		var yearIndex, monthIndex, dayIndex, hourIndex, minuteIndex, secondIndex;
		yearIndex = asFormat.indexOf("yyyy");
		if (yearIndex != -1)
			year = str.substr(yearIndex, 4);
		else {
			yearIndex = asFormat.indexOf("yy");
			if (yearIndex != -1)
				year = str.substr(yearIndex, 2);
			else
				return false;
		}
		monthIndex = asFormat.indexOf("MM");
		if (monthIndex != -1)
			month = str.substr(monthIndex, 2);
		else
			return false;
		dayIndex = asFormat.indexOf("dd");
		if (dayIndex != -1)
			day = str.substr(dayIndex, 2);
		else
			return false;
		hourIndex = asFormat.indexOf("HH");
		if (hourIndex == -1) {
			hourIndex = asFormat.indexOf("kk");
			if (hourIndex == -1) {
				hourIndex = asFormat.indexOf("KK");
				if (hourIndex == -1) {
					hourIndex = asFormat.indexOf("hh");
					if (hourIndex == -1)
						return false;
				}
			}
		}
		hour = str.substr(hourIndex, 2);
		minuteIndex = asFormat.indexOf("mm");
		if (minuteIndex != -1)
			minute = str.substr(minuteIndex, 2);
		else
			return false;
		secondIndex = asFormat.indexOf("ss");
		if (secondIndex != -1)
			second = str.substr(secondIndex, 2);
		else
			second = null;
		if (!(/^([0-9][0-9])$|^(19[0-9][0-9]|20[0-9][0-9])$/.test(year))) {// alert("年出错,请输入1900-2050之间的年数字");
			return false;
		}
		if (!(/^((0[1-9])|[1-9]|10|11|12)$/.test(month))) {// alert("月份出错");
			return false;
		}
		if (!(/^([0-9]|[0-1][0-9]|(2[0-4]))$/.test(hour))) {// alert("hour出错");
			return false;
		}
		if (!(/^([0-9]|[0-5][0-9])$/.test(minute))) {// alert("minute出错");
			return false;
		}
		if (second != null && second != ""
				&& !(/^([0-9]|[0-5][0-9])$/.test(second))) {// alert("second出错");
			return false;
		}
		year = parseInt(year, 10);
		month = parseInt(month, 10);
		if (isNaN(parseInt(day, 10)))
			return false;
		else
			day = parseInt(day, 10);
		var max;
		if (month == 2) {
			max = 28;
			if (((year % 10 == 0) && (year % 40 == 0))
					|| ((year % 10 != 0) && (year % 4 == 0))) // 判断是否闰年
				max = 29;
		} else if ((month == 4) || (month == 6) || (max == 9) || (month == 11))
			max = 30;
		else
			max = 31;
		if (day < 1 || day > max) { // alert("日期出错,请输入范围为1-"+max+"内的整数");
			return false;
		}
		// alert(year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second);
		if (year.length == 2)
			year = "19" + year;
		if (year < 1900)
			return false;
		var result = year + "-" + month + "-" + day + " " + hour + ":" + minute;
		if (second != null && "" != second)
			result = result + ":" + second;
		return result;
	}
}
var idExt = "_zxb_error_span";
Zxb.validate = {
	// /////////////////////////////////////////////////////////////////////////////
	/**
	 * Check the value of text tag and alert a prompt message
	 * 
	 * 1.validate email, if it's invalid then alert a message and return false;
	 * 2.validate phone, if it's invalid then alert a message and return false;
	 * 3.validate moblie phone, if it's invalid then alert a message and return
	 * false; 4.validate float number, if it's invalid then alert a message and
	 * return false; 5.validate currency number, if it's invalid then alert a
	 * message and return false; 6.validate if value < minValue , then alert a
	 * message and return false; 7.validate if value > maxValue , then alert a
	 * message and return false; 8.validte date type, if it's invalid then alert
	 * a message and return false; 9.validate the document code, if it's invalid
	 * then alert a message and return false; 10.validate the code length, if
	 * it's invalid then alert a message and return false; 11.validate the bank
	 * account, if it's invalid then alert a message and return false;
	 * 12.validate ID code, if it's invalid then alert a message and return
	 * false; 13.check if text value is empty when notNull=true, then alert a
	 * message and return false;
	 */
	// //////////////////////////////////////////////////////////////////////////////
	validateEmail : function(e) {
		var rc = true;
		var msg = "Email格式不正确";
		var srcElement = Zxb.Util.getEventSrc(e);
		if (!Zxb.check.checkEmail(srcElement.value)) {
			srcElement.focus();
			Zxb.pub.appendError(srcElement.id, msg)
			rc = false;
		} else {
			Zxb.pub.remove(srcElement.id + idExt);
			return true;
		}
		return rc;
	},
	validateEmail2 : function(strEmail) {
		var rc = true;
		if (strEmail) {
			strEmail = strEmail.replace(/,/g, ';');
			var mailaddress = strEmail.split(';');
			for (var i = 0; i < mailaddress.length; i++) {
				rc = rc && checkEmail(mailaddress[i]);
			}
		}
		return rc;
	},
	validateReg : function(regExp, value) {
		if (value && value != "") {
			var reg = new RegExp(regExp);
			return reg.test(value);
		}
		return true;
	},
	validatePhone : function(e) {
		var rc = true;
		var msg = "电话号码 格式不正确";
		var target = Zxb.Util.getEventSrc(e);;
		var valid = false;
		if (target.regExp) {
			valid = this.validateReg(target.regExp, target.value);
		} else {
			valid = Zxb.check.checkPhone(target.value);
		}
		if (!valid) {
			Zxb.Util.getEventSrc(e).focus();
			Zxb.pub.appendError(target.id, msg)
			rc = false;
		} else {
			Zxb.pub.remove(target.id + idExt);
			return true;
		}
		return rc;
	},
	validateFax : function(e) {
		var rc = true;
		var msg = "传真号码 格式不正确";
		var target = Zxb.Util.getEventSrc(e);
		var valid = false;
		if (target.regExp) {
			valid = this.validateReg(target.regExp, target.value);
		} else {
			valid = Zxb.check.checkPhone(target.value);
		}
		if (!valid) {
			target.focus();
			Zxb.pub.appendError(target.id, msg)
			rc = false;
		} else {
			Zxb.pub.remove(target.id + idExt);
			return true;
		}
		return rc;
	},
	validateMobile : function(e) {
		var rc = true;
		var msg = "手机号码格式不对";
		var target = Zxb.Util.getEventSrc(e);
		var valid = false;
		if (target.regExp) {
			valid = this.validateReg(target.regExp, target.value);
		} else {
			valid = Zxb.check.checkMobile(target.value);
		}
		if (!valid) {
			target.focus();
			Zxb.pub.appendError(target.id, msg)
			rc = false;
		} else {
			Zxb.pub.remove(target.id + idExt);
			return true;
		}
		return rc;
	},
	validateFloat : function() {
		var rc = true;
		var value = window.event.srcElement.value;
		var format = window.event.srcElement.format;
		if (_needSwitch(value) && _needSwitch(format)) {
			value = _exchangeCommaAndDot(value);
		}
		if (!checkCurrency(value)) {
			window.event.srcElement.focus();
			alert(window.event.srcElement.errorMsg);
			rc = false;
		}
		return rc;
	},
	validateFloatByFormat : function() {
		var rc = true;
		var value = window.event.srcElement.value;
		var format = window.event.srcElement.format;
		if (!checkNumberByFormat(value, format)) {
			window.event.srcElement.focus();
			alert(window.event.srcElement.errorMsg);
			rc = false;
		}
		window.event.srcElement.originalValue = value;
		return rc;
	},
	validateInteger : function() {
		var rc = true;
		var msg = "不是整数型";
		var srcElement = Zxb.Util.getEventSrc(e);
		if (!checkInteger(srcElement.value)) {
			srcElement.focus();
			Zxb.pub.appendError(target.id, msg)
			rc = false;
		} else {
			Zxb.pub.remove(target.id + idExt);
			return true;
		}
		return rc;
	},
	validateCurrency : function(e) {
		var rc = true;
		var msg = "不是货币类型";
		var srcElement = Zxb.Util.getEventSrc(e);
		var value = srcElement.value;
		var format = srcElement.format;
		if (Zxb.pub._needSwitch(value) && Zxb.pub._needSwitch(format)) {
			value = Zxb.pub._exchangeCommaAndDot(value);
		}
		if (!Zxb.check.checkCurrency(value)) {
			srcElement.focus();
			Zxb.pub.appendError(target.id, msg)
			rc = false;
		} else {
			Zxb.pub.remove(target.id + idExt);
			return true;
		}
		return rc;
	},
	validateMinValue : function(e, formatType) {
		var rc = true;
		var msg = "小于最小值";
		var srcElement = Zxb.Util.getEventSrc(e);
		if (formatType != null
				&& (formatType == "date" || formatType == "dateTime")) {
			if (!Zxb.check.checkMinDateValue(srcElement.value, $(srcElement)
							.attr('minvalue'), srcElement.format, formatType)) {
				srcElement.focus();
				Zxb.pub.appendError(srcElement.id, msg)
				rc = false;
			} else {
				Zxb.pub.remove(srcElement.id + idExt);
				return true;
			}
		} else if (formatType != null && formatType == "number") {
			if (!Zxb.check.checkMinValue(srcElement.value, $(srcElement)
							.attr('minvalue'), formatType, srcElement.format)) {
				srcElement.focus();
				Zxb.pub.appendError(srcElement.id, msg)
				rc = false;
			} else {
				Zxb.pub.remove(srcElement.id + idExt);
				return true;
			}
		} else {
			if (!Zxb.check.checkMinValue(srcElement.value, $(srcElement)
							.attr('minvalue'), formatType)) {
				srcElement.focus();
				Zxb.pub.appendError(srcElement.id, msg)
				rc = false;
			} else {
				Zxb.pub.remove(srcElement.id + idExt);
				return true;
			}
		}
		return rc;
	},
	validateMaxValue : function(e, formatType) {
		var rc = true;
		var srcElement = Zxb.Util.getEventSrc(e);
		var msg = "大于最大值";
		if (formatType != null
				&& (formatType == "date" || formatType == "dateTime")) {
			if (!Zxb.check.checkMaxDateValue(srcElement.value, $(srcElement)
							.attr('maxvalue'), srcElement.format, formatType)) {
				srcElement.focus();
				Zxb.pub.appendError(srcElement.id, msg)
				rc = false;
			} else {
				Zxb.pub.remove(srcElement.id + idExt);
				return true;
			}
		} else if (formatType != null && formatType == "number") {
			if (!Zxb.check.checkMaxValue(srcElement.value, $(srcElement)
							.attr('maxvalue'), formatType, srcElement.format)) {
				srcElement.focus();
				Zxb.pub.appendError(srcElement.id, msg)
				rc = false;
			} else {
				Zxb.pub.remove(srcElement.id + idExt);
				return true;
			}
		} else {
			if (!Zxb.check.checkMaxValue(srcElement.value, $(srcElement)
							.attr('maxvalue'), formatType)) {
				srcElement.focus();
				Zxb.pub.appendError(srcElement.id, msg)
				rc = false;
			} else {
				Zxb.pub.remove(srcElement.id + idExt);
				return true;
			}
		}
		return rc;
	},
	validateDateForTag : function(e) {
		var rc = true;
		if (Zxb.Util.getEventSrc(e).value != "") {
			var format = Zxb.Util.getEventSrc(e).format;
			
			if (format == null || format + "" == "undefined")
				format = DATE_DEFAULT_FORMAT;
			if (!Zxb.check.checkDate(Zxb.Util.getEventSrc(e).value, format)) {
				Zxb.Util.getEventSrc(e).focus();
			var msg="时间格式不正确" + " " + format.toUpperCase() + "!";
				Zxb.pub.appendError(Zxb.Util.getEventSrc(e).id, msg)
				rc = false;
			} else {
				Zxb.pub.remove(Zxb.Util.getEventSrc(e).id + idExt);
				return true;
			}
		}
		return rc;
	},
	validatePostCode : function() {
		var rc = true;
		var srcElement = Zxb.Util.getEventSrc(e);
		var str = srcElement.value;
		var nLength = window.event.srcElement.codeLength;
		if (typeof str != "undefined" && str != "") {
			if (str.length != nLength) {
				window.event.srcElement.focus();
				alert(window.event.srcElement.errorLenMsg);
				rc = false;
			}
		}
	},
	validateDocCode : function() {
		var rc = true;
		var str = window.event.srcElement.value;
		var nLength = window.event.srcElement.codeLength;
		var typeCode = window.event.srcElement.typeCode;
		if (typeCode == 22) {
			if (!checkApplyCode(str)) {
				window.event.srcElement.focus();
				alert(window.event.srcElement.errorMsg);
				rc = false;
			}
		} else {
			if (typeCode == 11) {
				if (!checkPolicyCode(str)) {
					window.event.srcElement.focus();
					alert(window.event.srcElement.errorMsg);
					rc = false;
				}
			} else {
				if (isNumber(str) && checkCodeLength(str, nLength)) {
					str = preFillZero(str, nLength);
					if (!checkDocCode(str, typeCode, nLength)) {
						window.event.srcElement.focus();
						alert(window.event.srcElement.errorMsg);
						rc = false;
					}
				}
			}
		}
		return rc;
	},
	validateCodeLength : function() {
		var rc = true;
		var str = window.event.srcElement.value;
		var nLength = window.event.srcElement.codeLength;
		if (window.event.srcElement.typeCode == 22) {
		} else {
			if (window.event.srcElement.typeCode == 11) {
			} else {
				if (isNumber(str)) {
					if (!checkCodeLength(str, nLength)) {
						window.event.srcElement.focus();
						alert(window.event.srcElement.errorLenMsg);
						rc = false;
					} else {
						if (str.length != 0) {
							window.event.srcElement.value = preFillZero(str,
									nLength);
						}
					}
				} else {
					if (str.length != 0) {
						window.event.srcElement.focus();
						alert(window.event.srcElement.errorMsg);
						rc = false;
					}
				}
			}
		}
		return rc;
	},
	validateBankAccount : function(e) {
		var rc = true;
		var msg="不是合法的银行帐号";
		var target = Zxb.Util.getEventSrc(e);
		if (!Zxb.check.checkBankAccount(target.value)) {
			target.focus();
			Zxb.pub.appendError(target.id, msg)
			rc = false;
		} else {
			Zxb.pub.remove(target.id + idExt);
			return true;
		}
		return rc;
	},
	validateCertiCode : function() {
		var rc = true;
		var control = Zxb.Util.getEventSrc(e);
		if (control.value != "" && control.typeField == "ID") {
			if (!Zxb.check.checkId(control.value)) {
				control.focus();
				alert("不是合法的CertiCode");
				rc = false;
			}
		}
		return rc;
	},
	checkNotNull : function() {
		var forms = document.forms;
		for (var i = 0; i < forms.length; i++) {
			hideAllBackground(forms[i]);
		}
		var objs = document.getElementsByTagName("Input");
		var areaObjs = document.getElementsByTagName("textarea");
		var selectObjs = document.getElementsByTagName("select");
		for (var iCVal = 0; iCVal < objs.length; iCVal++) {
			if ((objs[iCVal].type == "text" || objs[iCVal].type == "hidden")
					&& objs[iCVal].notNull == "true"
					&& objs[iCVal].disabled != true
					&& trim(objs[iCVal].value) == "") {
				if (objs[iCVal].className == 'combo-input-value') {
					alert(objs[iCVal].nullMsg);
					showBackground(objs[iCVal].model, "red");
					return false;
				} else if (objs[iCVal].className == 'jcombo-input-value') {
					alert(objs[iCVal].nullMsg);
					showBackground(getJcomboObjByName(objs[iCVal].modelName),
							"red");
					return false;
				} else {
					alert(objs[iCVal].nullMsg);
					if (objs[iCVal].type != "hidden") {
						showBackground(objs[iCVal], "red");
						objs[iCVal].focus();
					}
					return false;
				}
			}
		}
		for (var iCVal = 0; iCVal < areaObjs.length; iCVal++) {
			if (areaObjs[iCVal].notNull == "true"
					&& areaObjs[iCVal].disabled != true
					&& trim(areaObjs[iCVal].value) == "") {
				alert(areaObjs[iCVal].nullMsg);
				areaObjs[iCVal].focus();
				return false;
			}
		}
		for (var iCVal = 0; iCVal < selectObjs.length; iCVal++) {
			if (selectObjs[iCVal].notNull == "true"
					&& selectObjs[iCVal].disabled != true
					&& trim(selectObjs[iCVal].value) == "") {
				alert(selectObjs[iCVal].nullMsg);
				selectObjs[iCVal].focus();
				return false;
			}
		}
		return true;
	},
	checkCharset : function(charset) {
		var objs = document.getElementsByTagName("Input");
		var areaObjs = document.getElementsByTagName("textarea");
		var selectObjs = document.getElementsByTagName("select");
		var reg = new RegExp("^([\\u0000-\\u007f])*$");
		var checkValues = {};
		for (var iCVal = 0; iCVal < objs.length; iCVal++) {
			if ((objs[iCVal].type == "text" || objs[iCVal].type == "hidden")
					&& objs[iCVal].disabled != true
					&& !reg.test(objs[iCVal].value) && objs[iCVal].name) {
				if (!checkValues[objs[iCVal].name]) {
					checkValues[objs[iCVal].name] = [];
				}
				checkValues[objs[iCVal].name].push(objs[iCVal].value);
			}
		}
		for (var iCVal = 0; iCVal < areaObjs.length; iCVal++) {
			if (areaObjs[iCVal].disabled != true
					&& !reg.test(areaObjs[iCVal].value) && areaObjs[iCVal].name) {
				if (!checkValues[areaObjs[iCVal].name]) {
					checkValues[areaObjs[iCVal].name] = [];
				}
				checkValues[areaObjs[iCVal].name].push(areaObjs[iCVal].value);
			}
		}
		var result = doServerSideCharsetCheck(checkValues, charset);
		if (result.name) {
			var object = getTagDisplayObject(result.name, result.index);
			alert(result.message);
			showBackground(object.htmlElem, "red");
			object.focus();
			return false;
		}
		return true;
	},
	/**
	 * Description : check data not null in current form scope
	 */
	checkNotNullFORMSCOPE : function(form) {
		hideAllBackground(form);
		if (form != null) {
			var objs = form.elements;
			for (var iCVal = 0; iCVal < objs.length; iCVal++) {
				if (objs[iCVal].notNull == "true"
						&& objs[iCVal].disabled != true) {
					if (objs[iCVal].className == 'combo-input-value') {
						if ("" == objs[iCVal].value) {
							alert(objs[iCVal].nullMsg);
							showBackground(objs[iCVal].model, "red");
							return false;
						}
					} else if (objs[iCVal].className == 'jcombo-input-value') {
						if ("" == objs[iCVal].value) {
							alert(objs[iCVal].nullMsg);
							showBackground(
									getJcomboObjByName(objs[iCVal].modelName),
									"red");
							return false;
						}
					} else if (objs[iCVal].unitedTableStyle == 'unitedTable-two-textbox') {
						if ("" == objs[iCVal].value) {
							alert(objs[iCVal].nullMsg);
							var codeText = document
									.getElementsByName(objs[iCVal].name
											+ "code")[0];
							var descText = document
									.getElementsByName(objs[iCVal].name
											+ "desc")[0]
							showBackground(codeText, "red");
							showBackground(descText, "red");
							codeText.focus();
							return false;
						}
					} else if (trim(objs[iCVal].value) == "") {
						alert(objs[iCVal].nullMsg);
						showBackground(objs[iCVal], "red");
						objs[iCVal].focus();
						return false;
					}
				}
			}
		}
		return true;
	},
	checkCharsetFORMSCOPE : function(form, charset) {
		var checkValues = {};
		if (form != null) {
			var reg = new RegExp("^([\\u0000-\\u007f])*$");
			var objs = form.elements;
			for (var iCVal = 0; iCVal < objs.length; iCVal++) {
				if (objs[iCVal].type.toLowerCase() != 'button'
						&& objs[iCVal].disabled != true
						&& !reg.test(objs[iCVal].value) && objs[iCVal].name) {
					if (!checkValues[objs[iCVal].name]) {
						checkValues[objs[iCVal].name] = [];
					}
					checkValues[objs[iCVal].name].push(objs[iCVal].value);
				}
			}
			var result = doServerSideCharsetCheck(checkValues, charset);
			if (result.name) {
				var object = getTagDisplayObject(result.name, result.index);
				alert(result.message);
				showBackground(object.htmlElem, "red");
				object.focus();
				return false;
			}
		}
		return true;
	},
	__objectType : function(obj) {
		if ((typeof(obj) == 'object') && (obj instanceof Array)) {
			return 'Array';
		}
		if (typeof(obj) == 'string') {
			return 'String';
		}
		if (typeof(obj) == 'number') {
			return 'Number';
		}
		if (typeof(obj) == 'boolean') {
			return 'Boolean';
		}
		return 'Object';
	},
	__serializeObject : function(obj) {
		var type = __objectType(obj);
		switch (type) {
			case 'Array' : {
				var strArray = '[';
				for (var i = 0; i < obj.length; ++i) {
					var value = '';
					if (obj[i]) {
						value = __serializeObject(obj[i]);
					}
					strArray += value + ',';
				}
				if (strArray.charAt(strArray.length - 1) == ',') {
					strArray = strArray.substr(0, strArray.length - 1);
				}
				strArray += ']';
				return strArray;
			}
			case 'Date' : {
				return 'new Date(' + obj.getTime() + ')';
			}
			case 'Boolean' :
			case 'Function' :
			case 'Number' :
				return obj;
			case 'String' :
				return "'" + obj + "'";
			default :
				var serialize = '{';
				for (var key in obj) {
					if (key == 'Serialize')
						continue;
					var subserialize = 'null';
					if (obj[key] != undefined) {
						subserialize = __serializeObject(obj[key]);
					}
					serialize += key + ':' + subserialize + ',';
				}
				if (serialize.charAt(serialize.length - 1) == ',') {
					serialize = serialize.substr(0, serialize.length - 1);
				}
				serialize += '}';
				return serialize;
		}
	},
	doServerSideCharsetCheck : function(checkValues, charset) {
		var json = __serializeObject(checkValues);
		var uri = _getContextPath() + '/checkCharsetAction.do?values=' + json
				+ '&charset=' + charset + '&entryflag=js';
		var result = LoadJSON(uri);
		if (result) {
			return eval(result);
		}
		return {};
	},
	transSubmitValue : function() {
		var objs = document.getElementsByTagName("Input");
		for (var iCVal = 0; iCVal < objs.length; iCVal++) {
			if (objs[iCVal].type == "text" && objs[iCVal].value != "") {
				if (objs[iCVal].trans == "number") {
					// objs[iCVal].trans = "transted";
					var format = objs[iCVal].format;
					var srcValue = objs[iCVal].value;
					if (srcValue != objs[iCVal].originalValue) {
						objs[iCVal].value = numberTrim(srcValue, false, true);
						continue;
					}
					var value = srcValue;
					if (format && format.indexOf('#') > -1
							&& (_needSwitch(format) || _needSwitch(value))) {
						value = _exchangeCommaAndDot(value);
					}
					objs[iCVal].value = numberTrim(value, false, true);
					// alert(objs[iCVal].name+', ori:'+objs[iCVal].originalValue
					// +',:'+objs[iCVal].value)
					continue;
				}
			}
		}
	},
	setPercentValue : function(percentTagName, value) {
		var hiddenObj = document.getElementById(percentTagName);
		var textObj = document.getElementById(percentTagName + "_text");
		hiddenObj.value = value;
		textObj.value = parseFloat(value) * 100;
	},
	getPercentValue : function(percentTagName) {
		var textObj = document.getElementById(percentTagName + "_text");
		return textObj.value;
	},
	setPercentFocus : function(percentTagName) {
		var textObj = document.getElementById(percentTagName + "_text");
		textObj.focus();
	},
	validateNumber : function(e) {
		var rc = true;
		var srcElement = Zxb.Util.getEventSrc(e);
		var msg="不是数字类型";
		var value = srcElement.value;
		var format = srcElement.format;
		if (Zxb.pub._needSwitch(value) && Zxb.pub._needSwitch(format)) {
			value = Zxb.pub._exchangeCommaAndDot(value);
		}
		if (value != null && value != "" && !Zxb.check.isNumber(value)) {
			srcElement.focus();
			Zxb.pub.appendError(srcElement.id, msg)
			rc = false;
		} else {
			Zxb.pub.remove(srcElement.id + idExt);
			return true;
		}
		return rc;
	},
	
	validateExpiryMonth : function() {
		var rc = true;
		var val = window.event.srcElement.value;
		if (val != '' && !isNumber(val)) {
			window.event.srcElement.focus();
			alert(window.event.srcElement.errorMonthMsg);
			rc = false;
		} else {
			if (val != '' && (val < 1 || val > 12)) {
				window.event.srcElement.focus();
				alert(window.event.srcElement.errorMonthMsg);
				rc = false;
			} else {
				if (val.length > 0 && val.length < 2)
					window.event.srcElement.value = preFillZero(val, '2');
			}
		}
		return rc;
	},
	validateExpiryYear : function() {
		var rc = true;
		var val = window.event.srcElement.value;
		if (val != '' && !isNumber(val)) {
			window.event.srcElement.focus();
			alert(window.event.srcElement.errorYearMsg);
			rc = false;
		} else {
			if (val.length > 0 && val.length < 2)
				window.event.srcElement.value = preFillZero(val, '2');
		}
		return rc;
	},
	validateMaxLength : function(errMsg) {
		var obj = window.event.srcElement;
		var length = obj.value ? utf8_strlen(obj.value) : 0;
		if (length > obj.maxLength) {
			alert(obj.MaxLengthMsg + obj.maxLength + "!");
			obj.focus();
			return false;
		} else {
			return true;
		}
	},
	ValidateWorkDay : function(errMsg) {
		var rc = true;
		if (window.event.srcElement.value != "") {
			var format = window.event.srcElement.format;
			if (format == null || format + "" == "undefined")
				format = DATE_DEFAULT_FORMAT;
			var ary = checkWorkDay(window.event.srcElement.value, format);
			if (ary[0] == "false") {
				window.event.srcElement.focus();
				alert(errMsg + " " + ary[1] + "!");
				rc = false;
			}
		}
		return rc;
	},
	validateDateFromToOnBlur : function(target) {
		if (!target._position)
			return;
		var sibling = null;
		var dateFromToName = null;
		if (target._position == "part1") {
			sibling = target.nextSibling.nextSibling.nextSibling.nextSibling;
			minVal = target.value;
			maxVal = sibling.value;
			dateFromToName = target.name + "_" + sibling.name;
		} else {
			sibling = target.previousSibling.previousSibling.previousSibling.previousSibling;
			minVal = sibling.value;
			maxVal = target.value;
			dateFromToName = sibling.name + "_" + target.name;
		}
		datefromtonofocus = false;
		if (validateDateFromTo(minVal, maxVal)) {
			dateFromToFocus = null;
			return true;
		} else {
			alert(target.rangeErrMsg);
			if (!datefromtonofocus) {
				target.focus();
			}
			return false;
		}
	},
	validateDateFromTo : function(minVal, maxVal) {
		if (minVal && maxVal && !checkMinValue(maxVal, minVal, 'date')) {
			// target.focus();
			// alert(target.rangeErrMsg);
			return false;
		}
		return true;
	},
	validateTimeTag : function() {
		var control = window.event.srcElement;
		var hour = control.value;
		if (hour == null || hour == "" || hour + "" == "undefined") {
			return true;
		} else {
			var HH;
			var MM;
			var SS;
			if (isNumber(hour)) {
				if (hour.length == 6) {
					HH = hour.substring(0, 2);
					MM = hour.substring(2, 4);
					SS = hour.substring(4, 6);
				} else {
					return false;
				}
			} else {
				var arrTimes;
				if (hour.indexOf(":") > -1) {
					arrTimes = hour.split(":");
					if (arrTimes.length != 3) {
						return false;
					} else {
						HH = arrTimes[0].length == 1
								? "0" + arrTimes[0]
								: arrTimes[0];
						MM = arrTimes[1].length == 1
								? "0" + arrTimes[1]
								: arrTimes[1];
						SS = arrTimes[2].length == 1
								? "0" + arrTimes[2]
								: arrTimes[2];
					}
				}
			}
			var result = formatHour(HH, MM, SS);
			if (result == "") {
				return false;
			} else {
				control.value = result;
				return true;
			}
		}
	},
	validateDateTimeForTag : function() {
		var rc = true;
		if (window.event.srcElement.value != "") {
			var format = window.event.srcElement.format;
			if (format == null || format + "" == "undefined")
				format = DATE_DEFAULT_FORMAT + " HH:mm";
			if (!checkDateTime(window.event.srcElement.value, format)) {
				window.event.srcElement.focus();
				alert(window.event.srcElement.errorMsg + " " + format + "!");
				rc = false;
			}
		}
		return rc;
	},
	checkInputNumber : function(evt) {
		var evt = evt || window.event;
		var reg = /^(-)?[0-9]+((\x2e[0-9]+)|\x2e)?$/;
		var srcElem = evt.srcElement || evt.target;
		var oSel = document.selection.createRange()
		var srcRange = srcElem.createTextRange()
		oSel.setEndPoint("StartToStart", srcRange)
		if (event.keyCode != 8 && event.keyCode != 46) {
			var num = oSel.text + String.fromCharCode(event.keyCode)
					+ srcRange.text.substr(oSel.text.length);
			evt.returnValue = reg.test(num);
		}
	},
	validateNotNull : function(e) {
		var rc = true;
		var target = Zxb.Util.getEventSrc(e);
		if (target.value == null || target.value == '') {
			target.focus();
			Zxb.pub.appendError(target.id, "不能为空")
			rc = false;
		} else {
			Zxb.pub.remove(target.id + idExt);
			return true;
		}
		return rc;
	},
	validateLength : function(e) {
		var rc = true;
		var target = Zxb.Util.getEventSrc(e);
		var len = $(target).attr("length");
		if (target.value.length != len) {
			target.focus();
			Zxb.pub.appendError(target.id, "长度必须是" + len)
			rc = false;
		} else {
			Zxb.pub.remove(target.id + idExt);
			return true;
		}
		return rc;
	}
}
Zxb.code = {}
Zxb.Ajax = {
	sync : function(url, paraObj, callbackFun, otherPara, scope) {
		var xmlHttp;
		if (Zxb.Util.isIE()) {
			xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
			xmlHttp = new XMLHttpRequest();
		}
		xmlHttp.open("POST", url, false);
		xmlHttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded");
		xmlHttp.setRequestHeader("Pragma", "no-cache");
		var requestStr = Zxb.Util.obj2Str(paraObj, "&");
		xmlHttp.send(requestStr);
		var valueObj = Zxb.Util.toJsonObj(xmlHttp.responseText);
		if (callbackFun)
			callbackFun.call(scope, valueObj, otherPara);
	},
	async : function(url, paraObj, callbackFun, otherObj, scope) {
		var xmlHttp;
		if (Zxb.Util.isIE()) {
			xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
			xmlHttp = new XMLHttpRequest();
		}
		xmlHttp.onreadystatechange = function() {
			if (xmlHttp.readyState == 4) {
				var valueObj = Zxb.Util.toJsonObj(xmlHttp.responseText);
				callbackFun.call(scope, otherObj, valueObj);
			}
		}
		xmlHttp.open("POST", url, true);
		xmlHttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded");
		xmlHttp.setRequestHeader("Pragma", "no-cache");
		var requestStr = Zxb.Util.obj2Str(paraObj, "&");
		xmlHttp.send(requestStr);
	}
}
function disableSubmit(finalResult, submitButtonId) {
	if (finalResult) {
		document.getElementById(submitButtonId).disabled = true;
		return finalResult;
	} else {
		return finalResult;
	}
}
function batchDelete(action, checkboxName) {
	if (!hasOneChecked(checkboxName)) {
		alert('请选择要操作的对象!');
		return;
	}
	if (confirm('确定执行[删除]操作?')) {
		var ids = getCheckedIds(checkboxName);
		if (Zxb.Util.isArray(ids)) {
			var idss = ids.join();
			window.location.href = action + '&id=' + idss;
		} else {
			window.location.href = action + '&id=' + ids;
		}
	}
}
function getCheckedIds(name) {
	var items = document.getElementsByName(name);
	if (items.length > 0) {
		var ids = new Array();
		for (var i = 0; i < items.length; i++) {
			if (items[i].checked == true) {
				ids.push(items[i].id);
			}
		}
	} else {
		if (items.checked == true) {
			return items.id;
		}
	}
	return ids;
}
function hasOneChecked(name) {
	var items = document.getElementsByName(name);
	if (items.length > 0) {
		for (var i = 0; i < items.length; i++) {
			if (items[i].checked == true) {
				return true;
			}
		}
	} else {
		if (items.checked == true) {
			return true;
		}
	}
	return false;
}
function setAllCheckboxState(name, state) {
	var elms = document.getElementsByName(name);
	for (var i = 0; i < elms.length; i++) {
		elms[i].checked = state;
	}
}
function getReferenceForm(elm) {
	while (elm && elm.tagName != 'BODY') {
		if (elm.tagName == 'FORM')
			return elm;
		elm = elm.parentNode;
	}
	return null;
}
