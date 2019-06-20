package br.com.ericbraga.enment;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TestMockHelper {

    private final File mTemporaryDirectory;
    private final String mFileName;

    public TestMockHelper(File temporaryDirectory) {
        mTemporaryDirectory = temporaryDirectory;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd__HH:mm:ss");
        String prefix = sdf.format(Calendar.getInstance().getTime());

        mFileName = String.format("test.txt_%s", prefix);
    }

    public void createTemporaryFile() throws IOException {
        final String fileContent = "This is a file from test purpose";

        File file = new File(mTemporaryDirectory, mFileName);
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(fileContent.getBytes());

        } finally {

            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Assert.fail(e.getMessage());
                }
            }
        }
    }

    public File getTemporaryFile() {
        return new File(mTemporaryDirectory, mFileName);
    }

    public String getCompleteFileName() {
        return new File(mTemporaryDirectory, mFileName).getAbsolutePath();
    }

    public String getFileName() {
        return mFileName;
    }

    public void removeTemporaryFile() {
        File file = getTemporaryFile();
        if (file.exists()) {
            file.delete();
        }
    }

}
