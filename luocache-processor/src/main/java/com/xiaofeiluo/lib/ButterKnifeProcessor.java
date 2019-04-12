package com.xiaofeiluo.lib;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xiaofeiluo.luocacheannotations.LuoCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


@AutoService(Processor.class)
public class ButterKnifeProcessor extends AbstractProcessor {

    /**
     * 元素相关的工具类
     */
    private Elements elementUtils;
    /**
     * 文件相关的工具类
     */
    private Filer filer;
    /**
     * 日志相关的工具类
     */
    private Messager messager;
    /**
     * 类型相关工具类
     */
    private Types typeUtils;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(LuoCache.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(LuoCache.class);
        for (Element annotatedElement : elementsAnnotatedWith) {
            analysisElement(annotatedElement);
        }
        return false;
    }

    private void analysisElement(Element annotatedElement) {
        PackageElement packageOf = elementUtils.getPackageOf(annotatedElement.getEnclosingElement());
        String packageName = packageOf.getQualifiedName().toString();


        TypeSpec build = getTypeSpec(annotatedElement, packageName);
        JavaFile javaFile = JavaFile.builder(packageName, build)//定义生成的包名,和类
                .build();
        try {
            javaFile.writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private TypeSpec getTypeSpec(Element annotatedElement, String packageName) {

        Name simpleName = annotatedElement.getSimpleName();
        String className = simpleName.toString() + "Manager";
        TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC);

        TypeElement classElement = (TypeElement) annotatedElement;

        List<? extends Element> members = elementUtils.getAllMembers(classElement);

        //生成操作相关的方法
        ClassName cacheClassName = ClassName.get("com.xiaofeiluo.luocache", "LuoCache");
        //单例实例
        ClassName instanceClassName = ClassName.get(packageName, className);
        FieldSpec singletonSpec = FieldSpec.builder(instanceClassName, "instance", Modifier.PRIVATE, Modifier.STATIC)
                .build();
        builder.addField(singletonSpec);

        //缓存实例
        ClassName strategyClassName = ClassName.get("com.xiaofeiluo.luocache.strategy", "IStrategy");
        FieldSpec strategySpec = FieldSpec.builder(strategyClassName, "strategy", Modifier.PRIVATE)
                .build();
        builder.addField(strategySpec);

        //生成单例方法
        MethodSpec methodSpec_singleton = MethodSpec.methodBuilder("getInstance")
                .returns(instanceClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addStatement(
                        "    if (instance == null) {" +
                                "\n" +
                                "      synchronized ($T.class) {" +
                                "\n" +
                                "        if (instance == null) {" +
                                "\n" +
                                "          instance = new $T();" +
                                "\n" +
                                "        }" +
                                "\n" +
                                "      }" +
                                "\n" +
                                "    }" +
                                "\n" +
                                "    return instance"
                        , instanceClassName
                        , instanceClassName)
                .build();
        builder.addMethod(methodSpec_singleton);

        String spName = (className + "_cache").toLowerCase();
        //私有化构造函数
        MethodSpec methodSpec_constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addStatement("strategy = $T.getStrategy($S)", cacheClassName, spName)
                .build();
        builder.addMethod(methodSpec_constructor);

        //添加设置缓存策略的方法
        MethodSpec methodSpec_strategy = MethodSpec.methodBuilder("setStrategy")
                .addModifiers(Modifier.PRIVATE)
                .addParameter(strategyClassName,"strategy")
                .addStatement("this.strategy = strategy", cacheClassName, spName)
                .build();
        builder.addMethod(methodSpec_strategy);

        ArrayList<String> fieldKeys = new ArrayList<String>();
        for (Element fieldElement : members) {

            ElementKind kind = fieldElement.getKind();

            if (kind.isField()) {
                //添加成员变量
                TypeName fieldTypeName = ClassName.get(fieldElement.asType());
                String fieldName = fieldElement.getSimpleName().toString();
                String fieldKey = (fieldName + "_key").toUpperCase();

                fieldKeys.add(fieldKey);

                FieldSpec fieldSpec = FieldSpec.builder(String.class, fieldKey, Modifier.PRIVATE)
                        .initializer("$S", fieldName)
                        .build();
                builder.addField(fieldSpec);

                //添加成员变量对应的方法
                String substring = fieldName.substring(0, 1);
                String upperCase = substring.toUpperCase();
                fieldName = upperCase + fieldName.substring(1, fieldName.length());


                MethodSpec method_get = MethodSpec.methodBuilder("get" + fieldName)
                        .returns(fieldTypeName)
                        .addStatement("return ($T)strategy.getCache($N,$T.class)", fieldTypeName, fieldKey.toUpperCase(), fieldTypeName)
                        .addModifiers(Modifier.PUBLIC, Modifier.SYNCHRONIZED)
                        .build();
                MethodSpec method_set = MethodSpec.methodBuilder("set" + fieldName)
                        .returns(TypeName.get(Boolean.class))
                        .addStatement("return strategy.setCache($N,$N)", fieldKey.toUpperCase(), fieldName)
                        .addModifiers(Modifier.PUBLIC, Modifier.SYNCHRONIZED)
                        .addParameter(fieldTypeName, fieldName)
                        .build();

                MethodSpec method_remove = MethodSpec.methodBuilder("remove" + fieldName)
                        .returns(TypeName.get(Boolean.class))
                        .addStatement("return strategy.removeCache($N)", fieldKey.toUpperCase())
                        .addModifiers(Modifier.PUBLIC, Modifier.SYNCHRONIZED)
                        .build();

                builder.addMethod(method_get);
                builder.addMethod(method_set);
                builder.addMethod(method_remove);
            }
        }

        //添加清空方法
        MethodSpec.Builder method_clear = MethodSpec.methodBuilder("clear")
                .addModifiers(Modifier.PUBLIC, Modifier.SYNCHRONIZED);
        for (int i = 0; i < fieldKeys.size(); i++) {
            String field = fieldKeys.get(i);
            method_clear.addStatement("strategy.removeCache($N)", field);
        }
        builder.addMethod(method_clear.build());
        return builder.build();

    }

}

