package me.yifeiyuan.flap.plugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by 程序亦非猿 on 2020/9/8.
 */
class FlapTransform extends Transform {


    final String PROXY_PACKAGE_NAME = "me/yifeiyuan/flap/proxies";

    final String FLAP_CLASS_FILE_NAME = "me/yifeiyuan/flap/Flap.class";

    final String FLAP_INJECT_METHOD_NAME = "injectFactories";

    //me.yifeiyuan.flap.Flap 那个文件
    static File flapFile;

    private Project project;

    public FlapTransform(Project project) {
        this.project = project;
    }

    @Override
    public String getName() {
        return "me.yifeiyuan.flap.FlapTransform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);

        Log.i("===================== flap transform start ===================== ");
        Log.println("===================== flap transform start ===================== ");

        if (transformInvocation.isIncremental()) {
            Log.println("增量编译");
        } else {
            Log.println("全量编译");
        }

        if (!transformInvocation.isIncremental() && transformInvocation.getOutputProvider() != null) {
            transformInvocation.getOutputProvider().deleteAll();
        }

        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();

        List<TransformInput> inputs = (List<TransformInput>) transformInvocation.getInputs();

        for (TransformInput input : inputs) {
            //jar 包中的 class 文件
            handleJarInputs(outputProvider, input.getJarInputs());

            // 处理文件夹目录中的 class 文件
            handleDirectoryInputs(input.getDirectoryInputs());
        }

        Log.println("===================== flap transform end ===================== ");

    }

    // ImmutableDirectoryInput{name=111413e8795dca60dfeda4518181815a7b03851e, file=/Users/xxx/workspace/Flap/app/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes, contentTypes=CLASSES, scopes=PROJECT, changedFiles={}}
    private void handleDirectoryInputs(Collection<DirectoryInput> directoryInputs) {
        Log.println("handleDirectoryInputs");
        for (DirectoryInput directoryInput : directoryInputs) {
            Log.println(directoryInput);
            Log.println(directoryInput.getChangedFiles());
            Log.println(directoryInput.getFile());
            Log.println(directoryInput.getFile().isDirectory());

            File[] files = directoryInput.getFile().listFiles();

            for (File file : files) {
                Log.println(file);
            }
        }
    }

    private void handleJarInputs(TransformOutputProvider outputProvider, Collection<JarInput> jarInputs) throws IOException {
        Log.println("handleJarInputs start");
        for (JarInput jarInput : jarInputs) {

            String inputName = jarInput.getName();

            if (inputName.endsWith(".jar")) {
                inputName = inputName.substring(0, inputName.length() - 4);
            }

            String hex = DigestUtils.md5Hex(jarInput.getFile().getAbsolutePath());

            File srcFile = jarInput.getFile();

            String destFileName = inputName + "-" + hex;
            File destFile = outputProvider.getContentLocation(destFileName, jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);

            if (shouldProcessPreDexJarFile(srcFile.getAbsolutePath())) {
                scanJarFile(srcFile, destFile);
            }

            FileUtils.copyFile(srcFile, destFile);
        }
        Log.println("handleJarInputs end");
    }

    //Users/xxx/.gradle/caches/transforms-1/files-1.1/cursoradapter-1.0.0.aar/6f2bc1b47d5cce5ab25d89f7c1b420fa/jars/classes.jar
    private boolean shouldProcessPreDexJarFile(String path) {
        Log.println("shouldProcessPreDexJarFile " + path);
        return !path.contains("com.android.support") && !path.contains("/android/m2repository");
    }

    private void scanJarFile(File src, File dest) throws IOException {
        Log.println("scanJarFile:");
        if (null != src) {

            JarFile jarFile = new JarFile(src);

            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String entryName = jarEntry.getName();
                Log.println(entryName);
                if (FLAP_CLASS_FILE_NAME.equals(entryName)) {
                    Log.println("发现 Flap class file");
                    flapFile = dest;
                } else if (entryName.startsWith(PROXY_PACKAGE_NAME)) {
                    InputStream inputStream = jarFile.getInputStream(jarEntry);
                    // TODO: 2020/9/8 处理文件
                    inputStream.close();
                }
            }

            jarFile.close();
        }

    }

    private boolean shouldProcessClass(String entryName) {
        Log.i("shouldProcessClass " + entryName);
        return entryName != null && entryName.startsWith(PROXY_PACKAGE_NAME);
    }
}