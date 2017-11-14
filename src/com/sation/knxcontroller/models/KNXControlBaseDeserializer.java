package com.sation.knxcontroller.models;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sation.knxcontroller.control.KNXControlBase;

public class KNXControlBaseDeserializer implements JsonDeserializer<KNXControlBase> {
	String controlBaseElementName;
	Gson gson;
	Map<String, Class<? extends KNXControlBase>> controlBaseRegistry;

	public KNXControlBaseDeserializer(String controlBaseElementName) {
		this.controlBaseElementName = controlBaseElementName;
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
		gson = gsonBuilder.create();
		controlBaseRegistry = new HashMap<String, Class<? extends KNXControlBase>>();
	}

	public void registerControlBase(String controlBaseType, Class<? extends KNXControlBase> commandInstanceClass) {
		controlBaseRegistry.put(controlBaseType, commandInstanceClass);
	}

	@Override
	public KNXControlBase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		try {
			JsonObject controlBaseObject = json.getAsJsonObject();
			JsonElement controlBaseTypeElement = controlBaseObject.get(controlBaseElementName);
			Class<? extends KNXControlBase> commandInstanceClass = controlBaseRegistry.get(controlBaseTypeElement.getAsString());
			KNXControlBase mKNXControlBase = gson.fromJson(json, commandInstanceClass);
			return mKNXControlBase;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
