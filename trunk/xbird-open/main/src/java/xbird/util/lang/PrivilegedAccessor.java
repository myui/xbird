/*
 * @(#)$Id: PrivilegedAccessor.java 3903 2008-06-12 05:52:41Z yui $
 *
 * Copyright 2006-2008 Makoto YUI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Makoto YUI - initial implementation
 */
package xbird.util.lang;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * The "ObjectMolester".
 * <p>
 * This class is used to access a method or field of an object no
 * matter what the access modifier of the method or field.  The syntax
 * for accessing fields and methods is out of the ordinary because this
 * class uses reflection to peel away protection.
 * <p>
 * Here is an example of using this to access a private member.
 * <code>resolveName</code> is a private method of <code>Class</code>.
 *
 * <pre>
 * Class c = Class.class;
 * System.out.println(
 *      PrivilegedAccessor.invokeMethod( c,
 *                                       "resolveName",
 *                                       "/net/iss/common/PrivilegeAccessor" ) );
 * </pre>
 *
 * @author Charlie Hubbard (chubbard@iss.net)
 * @author Prashant Dhokte (pdhokte@iss.net)
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class PrivilegedAccessor {

    public static Field getField(final Class clazz, final String fieldName) {
        try {
            return getField(clazz, fieldName, true);
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }

    public static Field getField(final Class clazz, final String fieldName, final boolean accessible)
            throws NoSuchFieldException {
        final Field field;
        try {
            field = AccessController.<Field> doPrivileged(new PrivilegedExceptionAction<Field>() {
                public Field run() throws NoSuchFieldException {
                    Field f = clazz.getDeclaredField(fieldName);
                    f.setAccessible(accessible);
                    return f;
                }
            });
        } catch (PrivilegedActionException e) {
            throw new AssertionError(e.getCause());
        }
        return field;
    }

    public static Field unsafeGetField(final Class clazz, final String fieldName) {
        try {
            return clazz.getField(fieldName);
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    public static void unsafeSetField(final Object obj, final Class clazz, final String fieldName, final Object value) {
        if(SystemUtils.IS_SUN_VM) {
            _unsafeSetField(obj, clazz, fieldName, value);
        } else {
            setField(obj, clazz, fieldName, value);
        }
    }

    public static void setField(final Object obj, final Class clazz, final String fieldName, final Object value) {
        final Field field;
        try {
            field = AccessController.<Field> doPrivileged(new PrivilegedExceptionAction<Field>() {
                public Field run() throws NoSuchFieldException {
                    Field f = clazz.getDeclaredField(fieldName);
                    f.setAccessible(true);
                    return f;
                }
            });
        } catch (PrivilegedActionException e) {
            throw new AssertionError(e.getCause());
        }
        try {
            field.set(obj, value);
        } catch (IllegalArgumentException arge) {
            throw new Error(arge);
        } catch (IllegalAccessException acce) {
            throw new Error(acce);
        }
    }

    private static void _unsafeSetField(final Object obj, final Class clazz, final String fieldName, final Object value) {
        final sun.misc.Unsafe unsafe = UnsafeUtils.getUnsafe();
        long lockOffset;
        try {
            lockOffset = unsafe.objectFieldOffset(clazz.getDeclaredField(fieldName));
        } catch (Exception ex) {
            throw new Error(ex);
        }
        unsafe.putObjectVolatile(obj, lockOffset, value);
    }

    public static void unsafeSetField(final Object obj, final Field field, final Object value) {
        if(SystemUtils.IS_SUN_VM) {
            _unsafeSetField(obj, field, value);
        } else {
            setField(obj, field, value);
        }
    }

    public static void setField(final Object obj, final Field field, final Object value) {
        try {
            field.set(obj, value);
        } catch (IllegalArgumentException arge) {
            throw new Error(arge);
        } catch (IllegalAccessException acce) {
            throw new Error(acce);
        }
    }

    private static void _unsafeSetField(final Object obj, final Field field, final Object value) {
        final sun.misc.Unsafe unsafe = UnsafeUtils.getUnsafe();
        final long lockOffset = unsafe.objectFieldOffset(field);
        unsafe.putObjectVolatile(obj, lockOffset, value);
    }

    /**
     * Return the named method with a method signature matching classTypes
     * from the given class.
     */
    public static Method getMethod(Class thisClass, String methodName, Class[] classTypes)
            throws NoSuchMethodException {
        if(thisClass == null) {
            throw new NoSuchMethodException("Class is not specified for method " + methodName + ".");
        }
        try {
            return thisClass.getDeclaredMethod(methodName, classTypes);
        } catch (NoSuchMethodException e) {
            return getMethod(thisClass.getSuperclass(), methodName, classTypes);
        }
    }

    public static Method getMethod(Object instance, String methodName, Class[] classTypes)
            throws NoSuchMethodException {
        Method accessMethod = getMethod(instance.getClass(), methodName, classTypes);
        accessMethod.setAccessible(true);
        return accessMethod;
    }

    public static Method getAssignableMethod(Class thisClass, String methodName, Class[] classTypes)
            throws NoSuchMethodException {
        if(thisClass == null) {
            throw new NoSuchMethodException("Class is not specified for method " + methodName + ".");
        }
        assert (methodName != null);
        assert (classTypes != null);
        for(Method method : thisClass.getMethods()) {
            if(method.getName().equals(methodName)) {
                Class[] paramClazz = method.getParameterTypes();
                if(classTypes.length == paramClazz.length) {
                    for(int i = 0; i < classTypes.length; i++) {
                        if(classTypes[i].isAssignableFrom(paramClazz[i])) {
                            if(i + 1 == classTypes.length) {
                                method.setAccessible(true);
                                return method;
                            }
                        } else { // force parameters convertion
                            if(classTypes[i].isPrimitive() && !paramClazz[i].isPrimitive()) {
                                Class pClassTypes = ClassResolver.getWrapperClass(classTypes[i]);
                                if(pClassTypes.isAssignableFrom(paramClazz[i])) {
                                    if(i + 1 == classTypes.length) {
                                        method.setAccessible(true);
                                        return method;
                                    }
                                }
                            } else if(!classTypes[i].isPrimitive() && paramClazz[i].isPrimitive()) {
                                Class pParamTypes = ClassResolver.getWrapperClass(paramClazz[i]);
                                if(classTypes[i].isAssignableFrom(pParamTypes)) {
                                    if(i + 1 == classTypes.length) {
                                        method.setAccessible(true);
                                        return method;
                                    }
                                }
                            }
                            assert (!(classTypes[i].isPrimitive() && paramClazz[i].isPrimitive()));
                            break;
                        }
                    }
                }
            }
        }
        final StringBuilder msg = new StringBuilder();
        msg.append("method not found: " + thisClass + '#' + methodName + '(');
        for(int i = 0; i < classTypes.length; i++) {
            if(i != 0) {
                msg.append(',');
            }
            msg.append(classTypes[i].getSimpleName());
        }
        msg.append(')');
        throw new NoSuchMethodException(msg.toString());
    }

    /**
     * Gets the value of the named field and returns it as an object.
     *
     * @param instance the object instance
     * @param fieldName the name of the field
     * @return an object representing the value of the field
     */
    public static Object getValue(Object instance, String fieldName) throws IllegalAccessException,
            NoSuchFieldException {
        Field field = _getField(instance.getClass(), fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }

    /**
     * Return the named field from the given class.
     */
    private static Field _getField(Class thisClass, String fieldName) throws NoSuchFieldException {
        try {
            return thisClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return _getField(thisClass.getSuperclass(), fieldName);
        }
    }

    /**
     * Calls a method on the given object instance with the given argument.
     *
     * @param instance the object instance
     * @param methodName the name of the method to invoke
     * @param arg the argument to pass to the method
     * @see PrivilegedAccessor#invokeMethod(Object,String,Object[])
     */
    public static Object invokeMethod(Object instance, String methodName, Object arg)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object[] args = new Object[1];
        args[0] = arg;
        return invokeMethod(instance, methodName, args);
    }

    /**
     * Calls a method on the given object instance with the given arguments.
     *
     * @param instance the object instance
     * @param methodName the name of the method to invoke
     * @param args an array of objects to pass as arguments
     * @see PrivilegedAccessor#invokeMethod(Object,String,Object)
     */
    public static Object invokeMethod(Object instance, String methodName, Object... args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class[] classTypes = null;
        if(args != null) {
            classTypes = new Class[args.length];
            for(int i = 0; i < args.length; i++) {
                if(args[i] != null)
                    classTypes[i] = args[i].getClass();
            }
        }
        return getMethod(instance, methodName, classTypes).invoke(instance, args);
    }

    /**
     * Calls a static method in the given class with the given argument.
     */
    public static Object invokeStaticMethod(String className, String methodName, Object arg)
            throws SecurityException, IllegalArgumentException, NoSuchMethodException,
            ClassNotFoundException, IllegalAccessException, InvocationTargetException {
        Object[] args = new Object[1];
        args[0] = arg;
        return invokeStaticMethod(className, methodName, args);
    }

    /**
     * Calls a static method in the given class with the given arguments.
     */
    public static Object invokeStaticMethod(String className, String methodName, Object... args)
            throws SecurityException, NoSuchMethodException, ClassNotFoundException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        Class primeClass = Class.forName(className);
        Class[] classTypes = null;
        if(args != null) {
            classTypes = new Class[args.length];
            for(int i = 0; i < args.length; i++) {
                if(args[i] != null)
                    classTypes[i] = args[i].getClass();
            }
        }
        Method method = primeClass.getDeclaredMethod(methodName, classTypes);
        method.setAccessible(true);

        return method.invoke(method, args);
    }

    /**
     * Calls a static method with the given class and the given arguments.
     * use this method when the specified arguments includes null object.
     */
    public static Object invokeStaticMethod(String className, String methodName, Class[] classTypes, Object... objects)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        Class primeClass = Class.forName(className);
        Method method = primeClass.getDeclaredMethod(methodName, classTypes);
        method.setAccessible(true);

        return method.invoke(method, objects);
    }
}