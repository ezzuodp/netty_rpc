package com.ezweb.demo.assist;

/**
 * <一句话说明功能>
 * <功能详细描述>
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/11/29
 */

import javassist.*;

import java.io.File;

public class JavassistTransformer {

	private final ClassPool pool = ClassPool.getDefault();

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			throw new IllegalArgumentException("Expected 1 argument: directory with classes to transform");
		}
		File classesDir = new File(args[0]);
		new JavassistTransformer().instrumentClassesIn(classesDir);
	}

	private void instrumentClassesIn(File classesDir) throws Exception {
		pool.appendClassPath(classesDir.getPath());

		final CtClass compiledClass = instrument(GetValueFix.class);
		compiledClass.writeFile(classesDir.getPath());
		System.out.println(">>>" + JavassistTransformer.class.getSimpleName() + ": Trasnformation class for ---> " + classesDir.getAbsolutePath());
	}

	private CtClass instrument(Class<?> targetClass) throws NotFoundException, CannotCompileException {
		final CtClass nodeClass = pool.get(targetClass.getName());
		CtMethod toString = CtNewMethod.make(
				"public String say() {return \"HACKED BY JSFELCHECK \" + super.toString();}", nodeClass);
		nodeClass.addMethod(toString);
		return nodeClass;
	}
}
