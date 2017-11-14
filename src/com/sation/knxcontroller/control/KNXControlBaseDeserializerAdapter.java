package com.sation.knxcontroller.control;

import com.google.gson.*; 
import java.lang.reflect.Type;

public class KNXControlBaseDeserializerAdapter implements JsonSerializer<KNXControlBase>, JsonDeserializer<KNXControlBase> {
    
	@Override
    public JsonElement serialize(KNXControlBase src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("$type", new JsonPrimitive(src.getClass().getSimpleName()));
        
        //result.add("$type", new JsonPrimitive(src.getClass().getSimpleName()));
        //result.add("properties", context.serialize(src, src.getClass()));

        return result;
    }

    @Override
    public KNXControlBase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("$type").getAsString(); 
        type = type.replace("Structure.Control.", "").replace(", Structure", "");
        //JsonElement element = jsonObject.get("properties");  
        try {
            //return context.deserialize(element, Class.forName("com.zyyknx.android.control." + type));
        	return context.deserialize(jsonObject, Class.forName("com.sation.knxcontroller.control." + type));
        } catch (ClassNotFoundException cnfe) {
            throw new JsonParseException("Unknown element type: " + type, cnfe);
        }
    }
}
