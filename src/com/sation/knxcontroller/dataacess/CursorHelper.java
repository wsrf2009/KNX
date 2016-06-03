package com.sation.knxcontroller.dataacess;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.database.Cursor;

public class CursorHelper {
	/**
	 * 通过Cursor转换成对应的VO。注意：Cursor里的字段名（可用别名）必须要和VO的属性名一致
	 * 
	 * @param c
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public static Object cursorToEntity(Cursor c, Class clazz) {
		if (c == null) {
			return null;
		}
		Object obj;
		int i = 1;
		try {
			c.moveToNext();
			obj = setValuesToFields(c, clazz);

			return obj;
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("ERROR @：cursor2VO");
			return null;
		} finally {
			c.close();
		}
	}

	/**
	 * 通过Cursor转换成对应的VO集合。注意：Cursor里的字段名（可用别名）必须要和VO的属性名一致
	 * 
	 * @param c
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List cursorToList(Cursor c, Class clazz) {
		if (c == null) {
			return null;
		}
		List list = new LinkedList();
		Object obj;
		try {
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				// while (c.moveToNext()) {
				obj = setValuesToFields(c, clazz);
				list.add(obj);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR @：cursorToList");
			return null;
		} finally {
			c.close();
		}
	}

	/**
	 * 把值设置进类属性里
	 * 
	 * @param columnNames
	 * @param fields
	 * @param c
	 * @param obj
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private static Object setValuesToFields(Cursor c, Class clazz)
			throws Exception {
		String[] columnNames = c.getColumnNames();// 字段数组
		Object obj = clazz.newInstance();
		Field[] fields = clazz.getFields();

		for (Field _field : fields) {
			Class<? extends Object> typeClass = _field.getType();// 属性类型
			for (int j = 0; j < columnNames.length; j++) {
				String columnName = columnNames[j];
				typeClass = getBasicClass(typeClass);
				boolean isBasicType = isBasicType(typeClass);

				if (isBasicType) {
					if (columnName.equalsIgnoreCase(_field.getName())) {// 是基本类型
						String _str = c.getString(c.getColumnIndex(columnName));
						if (_str == null) {
							break;
						}
						_str = _str == null ? "" : _str;
						Constructor<? extends Object> cons = typeClass
								.getConstructor(String.class);
						Object attribute = cons.newInstance(_str);
						_field.setAccessible(true);
						_field.set(obj, attribute);
						break;
					}
				} else {
					Object obj2 = setValuesToFields(c, typeClass);// 递归
					_field.set(obj, obj2);
					break;
				}

			}
		}
		return obj;
	}

	/**
	 * 判断是不是基本类型
	 * 
	 * @param typeClass
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static boolean isBasicType(Class typeClass) {
		if (typeClass.equals(Integer.class) || typeClass.equals(Long.class)
				|| typeClass.equals(Float.class)
				|| typeClass.equals(Double.class)
				|| typeClass.equals(Boolean.class)
				|| typeClass.equals(Byte.class)
				|| typeClass.equals(Short.class)
				|| typeClass.equals(String.class)) {

			return true;

		} else {
			return false;
		}
	}

	/**
	 * 获得包装类
	 * 
	 * @param typeClass
	 * @return
	 */
	@SuppressWarnings("all")
	public static Class<? extends Object> getBasicClass(Class typeClass) {
		Class _class = basicMap.get(typeClass);
		if (_class == null)
			_class = typeClass;
		return _class;
	}

	@SuppressWarnings("rawtypes")
	private static Map<Class, Class> basicMap = new HashMap<Class, Class>();
	static {
		basicMap.put(int.class, Integer.class);
		basicMap.put(long.class, Long.class);
		basicMap.put(float.class, Float.class);
		basicMap.put(double.class, Double.class);
		basicMap.put(boolean.class, Boolean.class);
		basicMap.put(byte.class, Byte.class);
		basicMap.put(short.class, Short.class);
	}

}
