package com.sation.knxcontroller.util;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonHelper {

	public static Gson createWCFGson() {
		GsonBuilder gsonb = new GsonBuilder();
		// gsonb.registerTypeAdapter(Date.class, new WCFDateDeserializer());
		gsonb.registerTypeAdapter(Date.class, new WCFCalendarDeserializer());
		Gson gson = gsonb.create();
		return gson;
	}

	public static Gson createWCFGson(String dataFormat) {
		GsonBuilder gsonb = new GsonBuilder();
		gsonb.setDateFormat(dataFormat);
		gsonb.registerTypeAdapter(Date.class, new WCFDateDeserializer());
		// gsonb.registerTypeAdapter(Date.class, new WCFCalendarDeserializer());
		Gson gson = gsonb.create();
		return gson;
	}

	public static class WCFCalendarDeserializer implements
			JsonDeserializer<Calendar>, JsonSerializer<Calendar> {

		public Calendar deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {

			String JSONDateToMilliseconds = "\\/(Date\\((.*?)(\\+.*)?\\))\\/";
			Pattern pattern = Pattern.compile(JSONDateToMilliseconds);
			Matcher matcher = pattern.matcher(json.getAsJsonPrimitive()
					.getAsString());
			matcher.matches();
			String tzone = matcher.group(3);
			String result = matcher.replaceAll("$2");

			Calendar calendar = new GregorianCalendar();
			calendar.setTimeZone(TimeZone.getTimeZone("GMT" + tzone));
			calendar.setTimeInMillis(Long.valueOf(result));
			return calendar;
		}

		@Override
		public JsonElement serialize(Calendar calendar, Type arg1,
				JsonSerializationContext arg2) {
			return new JsonPrimitive("/Date(" + calendar.getTimeInMillis()
					+ ")/");
		}
	}

	public static class WCFDateDeserializer implements JsonDeserializer<Date>,
			JsonSerializer<Date> {

		public Date deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			String JSONDateToMilliseconds = "\\/(Date\\((.*?)(\\+.*)?\\))\\/";
			Pattern pattern = Pattern.compile(JSONDateToMilliseconds);
			Matcher matcher = pattern.matcher(json.getAsJsonPrimitive()
					.getAsString());
			String result = matcher.replaceAll("$2");
			return new Date(Long.valueOf(result));
			// return new Date(new Long(result));
		}

		@Override
		public JsonElement serialize(Date date, Type arg1,
				JsonSerializationContext arg2) {
			return new JsonPrimitive("/Date(" + date.getTime() + ")/");
		}
	}
}
