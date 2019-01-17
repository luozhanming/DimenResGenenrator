package com.example.administrator.dimenresgenenrator;

import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class DimenResGenerator {

    public static final String PATH_DIMEN_RES = Environment.getExternalStorageDirectory().getPath() + File.separator + "DimenResGenerator" + File.separator;
    public static final String RESOURCE = "resources";
    public static final String NAME = "name";
    public static final String DIMEN = "dimen";


    private String namePattern = "";
    private int baseSize;


    public DimenResGenerator(int baseSize, String namePattern) {
        this.baseSize = baseSize;
        this.namePattern = namePattern;
    }

    public Observable<File> generatorDimenRes(final int referenceSize, boolean isBase) {
        float ratio = 0;
        ratio = (float) referenceSize / baseSize;
        final float finalRatio = ratio;
        final boolean finalIsBaseSize = isBase;
        return Observable.just(true)
                .flatMap(new Function<Boolean, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(Boolean aBoolean) throws Exception {
                        File file = null;
                        if (finalIsBaseSize) {
                            file = new File(PATH_DIMEN_RES + "dimens/values/dimens.xml");
                        } else {
                            file = new File(PATH_DIMEN_RES + "dimens/values-sw" + referenceSize + "dp/dimens.xml");
                        }
                        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                        return Observable.just(file);
                    }
                })
                .flatMap(new Function<File, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(File file) throws Exception {
                        XmlSerializer serializer = Xml.newSerializer();
                        FileOutputStream fos = new FileOutputStream(file);
                        serializer.setOutput(fos, "UTF-8");
                        serializer.startDocument("UTF-8", true);
                        serializer.startTag(null, RESOURCE);

                        //1-30dp
                        for (int i = 0; i <= 30; i++) {
                            generateDimenTag(serializer, i, finalRatio, true);
                        }

                        //30-60dp
                        for (int i = 32; i <= 60; i = i + 2) {
                            generateDimenTag(serializer, i, finalRatio, true);
                        }
                        //65-140dp
                        for (int i = 65; i <= 140; i = i + 5) {
                            generateDimenTag(serializer, i, finalRatio, true);
                        }
                        //150-320
                        for (int i = 150; i <= 800; i = i + 10) {
                            generateDimenTag(serializer, i, finalRatio, true);
                        }

                        for (int i = 0; i <= 30; i++) {
                            generateDimenTag(serializer, i, finalRatio, false);
                        }

                        serializer.endTag(null, RESOURCE);
                        serializer.endDocument();
                        fos.close();
                        return Observable.just(file);
                    }
                });
    }

    public Observable<File> zipDimensRes(List<Observable<File>> observables) {
        return Observable.zip(observables, new Function<Object[], File>() {
            @Override
            public File apply(Object[] objects) throws Exception {
                File file = new File(PATH_DIMEN_RES + "dimens.zip");
                if (file.exists()) file.delete();
                if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                ZipUtil.compress(PATH_DIMEN_RES + "dimens/", file.getPath());
                return file;
            }
        });
    }


    private void generateDimenTag(XmlSerializer serializer, int i, float ratio, boolean isDip) throws IOException {
        serializer.startTag(null, DIMEN);
        if (isDip) {
            serializer.attribute(null, NAME, String.format(namePattern, i));
            serializer.text(String.format("%.2fdp", (float) i * ratio));
        } else {
            serializer.attribute(null, NAME, String.format(namePattern.replace("dp", "sp"), i));
            serializer.text(String.format("%.2fsp", (float) i * ratio));
        }
        serializer.endTag(null, DIMEN);
    }
}
