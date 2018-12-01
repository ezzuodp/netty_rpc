/*
 * Copyright 2009-2016 Weibo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ezweb.engine.rpc.annotation;


import com.squareup.javapoet.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class RpcAsyncProcessor extends AbstractProcessor {
	private final static String ASYNC = "Async";
	// 直接源码目录
//	private final static String TARGET_DIR = "src/main/java/";
	private final static String TARGET_DIR = "target/generated-sources/annotations/";

	public RpcAsyncProcessor() {
	}

	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		/*String path = processingEnv.getOptions().get(GENERATE_PATH_KEY);// use javac complie options -AmotanGeneratePath=xxx
		if (path != null) {
			TARGET_DIR = path;
		} else { // use jvm option -DmotanGeneratePath=xxx
			TARGET_DIR = System.getProperty(GENERATE_PATH_KEY, "target/generated-sources/annotations/");
		}*/
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "MotanAsyncProcessor Create Path:" + TARGET_DIR);

	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		HashSet<String> types = new HashSet<>();
		types.add(RpcAsync.class.getName());
		return types;
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			return true;
		}
		for (Element elem : roundEnv.getElementsAnnotatedWith(RpcAsync.class)) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "MotanAsyncProcessor will process " + elem.toString() + ", generate class path:" + TARGET_DIR);
			try {
				writeAsyncClass(elem);
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "MotanAsyncProcessor done for " + elem.toString());
			} catch (Exception e) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
						"MotanAsyncProcessor process " + elem.toString() + " fail. exception:" + e.getMessage());
				e.printStackTrace();
			}
		}
		return true;
	}

	private void writeAsyncClass(Element elem) throws Exception {
		if (elem.getKind().isInterface()) {
			TypeElement interfaceClazz = (TypeElement) elem;

			PackageElement packageOf = processingEnv.getElementUtils().getPackageOf(interfaceClazz);

			String className = interfaceClazz.getSimpleName().toString();
			TypeSpec.Builder classBuilder =
					TypeSpec.interfaceBuilder(className + ASYNC).addModifiers(Modifier.PUBLIC)
					/*.addSuperinterface(TypeName.get(elem.asType()))*/;

			classBuilder.addJavadoc("不要编辑，系统自动生成的 !\n(" + new java.sql.Timestamp(System.currentTimeMillis()).toString() + ")\n");
			// add class generic type
			classBuilder.addTypeVariables(getTypeNames(interfaceClazz.getTypeParameters()));

			// add direct method
			addMethods(interfaceClazz, classBuilder);

			// TODO: add method form superinterface
			// addSuperInterfaceMethods(interfaceClazz.getInterfaces(), classBuilder);

			// write class
			JavaFile javaFile = JavaFile.builder(packageOf.getQualifiedName().toString(), classBuilder.build()).build();

			// typeElement ==> Symbol.ClassSymbol.class
			JavaFileObject sourcefile = getFieldFile(interfaceClazz, "sourcefile");
			JavaFileObject classfile = getFieldFile(interfaceClazz, "classfile");

			System.out.println("sourcefile = " + sourcefile);
			System.out.println("classfile  = " + classfile);

			javaFile.writeTo(new File(System.getProperty("basedir"), TARGET_DIR));

			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "MotanAsyncProcessor process : " + className + " success.");

		} else {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "MotanAsyncProcessor not process, because " + elem.toString() + " not a interface.");
		}
	}

	private JavaFileObject getFieldFile(TypeElement typeElement, String fieldName) throws NoSuchFieldException, IllegalAccessException {
		// typeElement ==> Symbol.ClassSymbol.class
		Field field = typeElement.getClass().getField(fieldName);
		return (JavaFileObject) field.get(typeElement);
	}

	private void addMethods(TypeElement interfaceClazz, TypeSpec.Builder classBuilder) {
		List<? extends Element> elements = interfaceClazz.getEnclosedElements();
		if (elements != null && !elements.isEmpty()) {
			for (Element e : elements) {
				if (ElementKind.METHOD.equals(e.getKind())) {
					ExecutableElement method = (ExecutableElement) e;

					MethodSpec.Builder methodBuilder =
							MethodSpec.methodBuilder(method.getSimpleName().toString())
									.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
									// 生成 CompletableFuture<T>
									.returns(ParameterizedTypeName.get(ClassName.get(CompletableFuture.class), TypeName.get(method.getReturnType())))
									.addTypeVariables(getTypeNames(method.getTypeParameters()));

					List<? extends VariableElement> vars = method.getParameters();
					for (VariableElement var : vars) {
						methodBuilder.addParameter(ParameterSpec.builder(TypeName.get(var.asType()), var.getSimpleName().toString()).build());
					}
					classBuilder.addMethod(methodBuilder.build());
				}
			}
		}
	}

	private List<TypeVariableName> getTypeNames(List<? extends TypeParameterElement> types) {
		List<TypeVariableName> result = new ArrayList<TypeVariableName>();
		if (types != null && !types.isEmpty()) {
			for (TypeParameterElement type : types) {
				result.add(TypeVariableName.get(type));
			}
		}
		return result;
	}

	/*private void addSuperInterfaceMethods(List<? extends TypeMirror> superInterfaces, TypeSpec.Builder classBuilder) {
		if (superInterfaces != null && !superInterfaces.isEmpty()) {
			for (TypeMirror tm : superInterfaces) {
				try {
					if (tm.getKind().equals(TypeKind.DECLARED)) {
						TypeElement de = (TypeElement) ((DeclaredType) tm).asElement();
						addMethods(de, classBuilder);
						addSuperInterfaceMethods(de.getInterfaces(), classBuilder);
					}
				} catch (Exception e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
							"MotanAsyncProcessor process superinterface " + tm.toString() + " fail. exception:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}*/

}
