

package com.manicken;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * This is some reflect helper methods
 */
public class Reflect {

	public static boolean printDebugInfo = false;
    public static Object GetField(String name, Object src) {
		try {
			Field f = src.getClass().getDeclaredField(name);
			f.setAccessible(true);
			return f.get(src);

		} catch (Exception e) {
			System.err.println("****************************************");
			System.err.println("************cannot reflect**************");
			System.err.println("****************************************");
			e.printStackTrace();
			return null;
		}
	}

	public static Object GetStaticField(String name, Class<?> src) {
		try {
			Field f = src.getDeclaredField(name);
			f.setAccessible(true);
			return f.get(null);

		} catch (Exception e) {
			System.err.println("****************************************");
			System.err.println("************cannot reflect**************");
			System.err.println("****************************************");
			e.printStackTrace();
			return null;
		}
	}

	public static Object InvokeMethod(String name, Object src, Object... parameters)
	{
		Class<?>[] parameterTypes = new Class<?>[parameters.length];
		String debugInfo = "";
		for (int i = 0; i < parameters.length; i++)
		{
			parameterTypes[i] = parameters[i].getClass();
			debugInfo += parameterTypes[i].toString() + "\n";
		}
		try {
			Method m = src.getClass().getDeclaredMethod(name, parameterTypes);
			m.setAccessible(true);
			return m.invoke(src, parameters);
		}
		catch (Exception e) { System.err.println("cannot invoke " + src.getClass().toString() + " " + name + "\n" + debugInfo); e.printStackTrace(); return null; }
	}

	/*
	 * this is for some special cases when the above don't work
	 */
	public static Object InvokeMethod2(String name, Object src, Object[] parameters, Class<?>[] parameterTypes)
	{
		if (printDebugInfo)
		{
			String debugInfo = "";
			for (int i = 0; i < parameters.length; i++)
				debugInfo += parameterTypes[i].toString() + "\n";
			System.out.println(debugInfo);
		}
		try {
			Method m = src.getClass().getDeclaredMethod(name, parameterTypes);
			m.setAccessible(true);
			return m.invoke(src, parameters);
		}
		catch (Exception e) { System.err.println("cannot invoke " + src.getClass().toString() + " " + name); e.printStackTrace(); return null; }
	}
	public static <T> T[] asArr(T... params) { return params; } // a little helper when using above method
	
}
